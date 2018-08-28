package com.jonnycaley.cryptomanager.ui.base

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.ui.home.HomeFragment
import com.jonnycaley.cryptomanager.ui.portfolio.PortfolioFragment
import com.jonnycaley.cryptomanager.ui.settings.SettingsFragment
import kotlinx.android.synthetic.main.content_home.*

class BaseActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->

        val fragmentManager: FragmentManager = supportFragmentManager

        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        when (item.itemId) {
            R.id.navigation_home -> {
                fragmentTransaction.replace(R.id.frame_placeholder, HomeFragment()).commit()
                return@OnNavigationItemSelectedListener true
            }
//            R.id.navigation_markets -> {
//                fragmentTransaction.replace(R.id.frame_placeholder, HomeFragment()).commit()
//                return@OnNavigationItemSelectedListener true
//            }
            R.id.navigation_portfolio -> {
                fragmentTransaction.replace(R.id.frame_placeholder, PortfolioFragment()).commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_settings -> {
                fragmentTransaction.replace(R.id.frame_placeholder, SettingsFragment()).commit()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
//        setupToolbar()

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        loadPortfolioFragment()
    }

//    private fun setupToolbar() {
//        val toolbar = findViewById<Toolbar>(R.id.toolbar)
//        setSupportActionBar(toolbar)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
//        supportActionBar?.title = "Splash"
//    }

    lateinit var portfolioFragment: HomeFragment

    private fun loadPortfolioFragment() {
        val transaction : FragmentTransaction = supportFragmentManager.beginTransaction()
        portfolioFragment = HomeFragment().newInstance("Home")/* parcelable */
        transaction.replace(R.id.frame_placeholder, portfolioFragment)
        transaction.commit()
    }

}
