package com.jonnycaley.cryptomanager.ui.crypto

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.widget.Toolbar
import android.view.MenuItem
import android.widget.ImageView
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.GeneralInfo.GeneralInfo
import com.jonnycaley.cryptomanager.ui.crypto.viewpager.general.GeneralFragment
import com.jonnycaley.cryptomanager.ui.crypto.viewpager.transactions.TransactionsFragment
import com.jonnycaley.cryptomanager.utils.CircleTransform
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_crypto.*
import androidx.palette.graphics.Palette
import com.squareup.picasso.Callback
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatDelegate
import android.widget.TextView
import android.widget.Toast
import com.jonnycaley.cryptomanager.utils.Utils
import java.util.*

class CryptoActivity : AppCompatActivity(), CryptoContract.View {

    private lateinit var presenter: CryptoContract.Presenter

    val args by lazy { CryptoArgs.deserializeFrom(intent) }

    val viewPager : androidx.viewpager.widget.ViewPager by lazy { findViewById<androidx.viewpager.widget.ViewPager>(R.id.pager) }
    val tabLayout : TabLayout by lazy { findViewById<TabLayout>(R.id.tablayout) }

    override fun onCreate(savedInstanceState: Bundle?) {

        if(Utils.isDarkTheme()) {
            setTheme(R.style.darktheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crypto)

        setupViewPager() //set up viewpager
        setupToolbar() //set up toolbar

        presenter = CryptoPresenter(CryptoDataManager.getInstance(this), this)
        presenter.attachView() //attach the presenter
    }

    /*
    Function loads the crypto color scheme
    */
    override fun connectionAvailable() {
        if(!isPicassoLoaded){
            presenter.getCoinColorScheme() //get the color scheme
        }
    }

    /*
    Function gets the crypto symbol
    */
    override fun getSymbol(): String {
        if(args.currencySymbol == "MIOTA")
            return "IOTA"
        return args.currencySymbol
    }

    /*
    Function shows the no internet snackbar
    */
    override fun showNoInternet() {
        showSnackBar(resources.getString(R.string.internet_required))
    }

    var snackBar : Snackbar? = null

    /*
    Function shows the snackbar with the input message
    */
    fun showSnackBar(message: String) {

        snackBar = Snackbar.make(viewPager, message, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry) {
                    presenter.getCoinColorScheme()
                }
        snackBar.let { it?.show() }
    }

    var isPicassoLoaded = false

    /*
    Function loads the crypto theme
    */
    override fun loadTheme(info: GeneralInfo) {

        isPicassoLoaded = true

        if(info.data?.isNotEmpty() == true) { //if there is data

            Picasso.with(this)
                    .load("https://www.cryptocompare.com" + info.data?.first()?.coinInfo?.imageUrl)
                    .fit()
                    .centerCrop()
                    .transform(CircleTransform())
                    .placeholder(R.drawable.circle)
                    .into(image, object : Callback {
                        override fun onSuccess() {
                        }

                        override fun onError() {
                        }
                    })
        }
    }

    /*
    Function gets the dominant color of the crypot icon
    */
    fun getDominantColor(bitmap: Bitmap): Int {
        val swatchesTemp = androidx.palette.graphics.Palette.from(bitmap).generate().swatches
        val swatches = ArrayList(swatchesTemp)
        swatches.sortWith(Comparator { swatch1, swatch2 -> swatch2.population - swatch1.population })
        return if (swatches.size > 0) swatches[0].rgb else R.color.theme_color
    }

    var myPagerAdapter : CryptoActivity.MyPagerAdapter? = null

    /*
    Function sets up the viewpager
    */
    override fun setupViewPager() {

        myPagerAdapter = MyPagerAdapter(supportFragmentManager)
        viewPager.adapter = myPagerAdapter
        tabLayout.setupWithViewPager(viewPager) //set up with view pager
        tabLayout.setSelectedTabIndicatorColor(resources.getColor(R.color.theme_color)) //Set the initial tab color to background so you cant see it so it looks neat when loading in

    }

    val title : TextView by lazy { findViewById<TextView>(R.id.title) }
    val image : ImageView by lazy { findViewById<ImageView>(R.id.image) }

    val toolbar : Toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }

    /*
    Function sets up the toolbar title and arrow color
    */
    fun setupToolbar() {

        if(!Utils.isDarkTheme()) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp) //set up icon
        }

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        if(args.currencyName != "") //set titles
            title.text = args.currencyName
        else
            title.text = args.currencySymbol
    }

    /*
    Function handles the toolbar item presses
    */
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

    /*
    Function shows the no data available toast
    */
    override fun showNoDataAvailable() {
        Toast.makeText(this, "No data available for ${args.currencyName}", Toast.LENGTH_SHORT).show()
    }

    /*
    Adapter class
    */
    inner class MyPagerAdapter(fm: androidx.fragment.app.FragmentManager) : androidx.fragment.app.FragmentPagerAdapter(fm) {

        val NUM_PAGES = 2

        override fun getItem(position: Int): Fragment {
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