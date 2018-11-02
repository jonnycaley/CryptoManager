package com.jonnycaley.cryptomanager.ui.crypto

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.ImageView
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.GeneralInfo.GeneralInfo
import com.jonnycaley.cryptomanager.ui.crypto.viewpager.general.GeneralFragment
import com.jonnycaley.cryptomanager.ui.crypto.viewpager.transactions.TransactionsFragment
import com.jonnycaley.cryptomanager.utils.CircleTransform
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.android.synthetic.main.activity_crypto.*
import android.widget.Toast
import android.support.v7.graphics.Palette
import android.R.attr.bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import com.squareup.picasso.Callback
import android.graphics.drawable.BitmapDrawable
import android.support.v7.app.AppCompatDelegate
import android.widget.TextView
import java.util.*


class CryptoActivity : AppCompatActivity(), CryptoContract.View {

    private lateinit var presenter: CryptoContract.Presenter

    val args by lazy { CryptoArgs.deserializeFrom(intent) }

    val viewPager : ViewPager by lazy { findViewById<ViewPager>(R.id.pager) }
    val tabLayout : TabLayout by lazy { findViewById<TabLayout>(R.id.tablayout) }

    val title : TextView by lazy { findViewById<TextView>(R.id.title) }
    val image : ImageView by lazy { findViewById<ImageView>(R.id.image) }

    override fun onCreate(savedInstanceState: Bundle?) {

        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.darktheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crypto)

        setupToolbar()
        setupViewPager()

        tabLayout.setSelectedTabIndicatorColor(resources.getColor(R.color.colorPrimary)) //Set the initial tab color to background so you cant see it so it looks neat when loading in

        presenter = CryptoPresenter(CryptoDataManager.getInstance(this), this)
        presenter.attachView()
    }

    override fun getSymbol(): String {
        if(args.currencySymbol == "MIOTA")
            return "IOTA"
        return args.currencySymbol
    }

    override fun loadTheme(info: GeneralInfo) {

        println("Loading theme")

        if(info.data?.isNotEmpty()!!) {

            Picasso.with(this)
                    .load("https://www.cryptocompare.com" + info.data?.first()?.coinInfo?.imageUrl)
                    .fit()
                    .centerCrop()
                    .transform(CircleTransform())
                    .placeholder(R.drawable.circle)
                    .into(image, object : Callback {
                        override fun onSuccess() {

                            val drawable = image.drawable as BitmapDrawable
                            val bitmap = drawable.bitmap

                            tabLayout.setSelectedTabIndicatorColor(getDominantColor(bitmap))
                            title.setTextColor(getDominantColor(bitmap))

                            toolbar.navigationIcon?.setColorFilter(getDominantColor(bitmap), PorterDuff.Mode.SRC_ATOP)

                        }

                        override fun onError() {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }

                    })
        }
    }

    fun getDominantColor(bitmap: Bitmap): Int {
        val swatchesTemp = Palette.from(bitmap).generate().swatches
        val swatches = ArrayList(swatchesTemp)
        swatches.sortWith(Comparator { swatch1, swatch2 -> swatch2.population - swatch1.population })
        return if (swatches.size > 0) swatches[0].rgb else R.color.colorPrimary
    }

    private fun setupViewPager() {
        val myPagerAdapter = MyPagerAdapter(supportFragmentManager)
        viewPager.adapter = myPagerAdapter
        tabLayout.setupWithViewPager(viewPager)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.title = args.currencySymbol

        title.text = args.currencySymbol
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                super.onBackPressed()
                return false
            }
        }
        return false
    }


    inner class MyPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        val NUM_PAGES = 2

        override fun getItem(position: Int): Fragment? {
            return when(position){
                0 -> GeneralFragment.newInstance(getSymbol())
                else -> TransactionsFragment.newInstance(getSymbol())
            }
        }

        override fun getCount(): Int {
            return NUM_PAGES
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when(position){
                0 -> {
                    "General"
                }
                1 -> {
                    "Transactions"
                }
                else -> {
                    "Others"
                }
            }
        }


    }

    override fun setPresenter(presenter: CryptoContract.Presenter) {
        this.presenter = checkNotNull(presenter)
    }

}
