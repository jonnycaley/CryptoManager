package com.jonnycaley.cryptomanager.ui.base

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.ui.home.HomeFragment
import com.jonnycaley.cryptomanager.ui.portfolio.MarketsFragment
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
            R.id.navigation_markets -> {
                fragmentTransaction.replace(R.id.frame_placeholder, MarketsFragment()).commit()
                return@OnNavigationItemSelectedListener true
            }
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

    val args by lazy { BaseArgs.deserializeFrom(Intent()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
//        setupToolbar()

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        loadPortfolioFragment(args.fragment)
    }

//    private fun setupToolbar() {
//        val toolbar = findViewById<Toolbar>(R.id.toolbar)
//        setSupportActionBar(toolbar)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
//        supportActionBar?.title = "Splash"
//    }

    lateinit var fragment: Fragment

    private fun loadPortfolioFragment(fragment: Int) {

        val transaction : FragmentTransaction = supportFragmentManager.beginTransaction()
        when(fragment){
            0 -> this.fragment = HomeFragment().newInstance("Home")/* parcelable */
            1 -> this.fragment = MarketsFragment().newInstance("Markets")/* parcelable */
            2 -> this.fragment = PortfolioFragment().newInstance("Portfolio")/* parcelable */
            3 -> this.fragment = SettingsFragment().newInstance("Settings")/* parcelable */
        }
        transaction.replace(R.id.frame_placeholder, this.fragment)
        transaction.commit()
    }

}
