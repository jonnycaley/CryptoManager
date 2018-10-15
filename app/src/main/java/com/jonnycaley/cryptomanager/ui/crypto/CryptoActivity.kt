package com.jonnycaley.cryptomanager.ui.crypto

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.ui.crypto.viewpager.general.GeneralFragment
import com.jonnycaley.cryptomanager.ui.crypto.viewpager.transactions.TransactionsFragment

class CryptoActivity : AppCompatActivity(), CryptoContract.View {

    private lateinit var presenter: CryptoContract.Presenter

    val args by lazy { CryptoArgs.deserializeFrom(intent) }

    val viewPager : ViewPager by lazy { findViewById<ViewPager>(R.id.pager) }
    val tabLayout : TabLayout by lazy { findViewById<TabLayout>(R.id.tablayout) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crypto)

        setupToolbar()
        setupViewPager()

        presenter = CryptoPresenter(CryptoDataManager.getInstance(this), this)
        presenter.attachView()
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
        supportActionBar?.title = args.currencySymbol
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
                0 -> GeneralFragment.newInstance(args.currencySymbol)
                else -> TransactionsFragment.newInstance(args.currencySymbol)
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
