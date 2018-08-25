package com.jonnycaley.cryptomanager.ui.home

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.ui.news.NewsFragment
import com.jonnycaley.cryptomanager.ui.portfolio.PortfolioFragment
import com.jonnycaley.cryptomanager.ui.settings.SettingsFragment
import kotlinx.android.synthetic.main.content_home.*

class HomeActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->

        val fragmentManager: FragmentManager = supportFragmentManager

        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        when (item.itemId) {
            R.id.navigation_home -> {
                fragmentTransaction.replace(R.id.frame_placeholder, PortfolioFragment()).commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                fragmentTransaction.replace(R.id.frame_placeholder, NewsFragment()).commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                fragmentTransaction.replace(R.id.frame_placeholder, SettingsFragment()).commit()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
//        setSupportActionBar(toolbar)


        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        loadPortfolioFragment()
    }

    lateinit var portfolioFragment: PortfolioFragment

    private fun loadPortfolioFragment() {
        val transaction : FragmentTransaction = supportFragmentManager.beginTransaction()
        portfolioFragment = PortfolioFragment().newInstance("Portfolio")/* parcelable */
        transaction.replace(R.id.frame_placeholder, portfolioFragment)
        transaction.commit()
    }

}
