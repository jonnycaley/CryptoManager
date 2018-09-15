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

    lateinit var fragment: Fragment

    val args by lazy { BaseArgs.deserializeFrom(intent) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        loadFragment(args.fragment)
    }

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

    private fun loadFragment(fragment: Int) {

        var selectedItem = 0

        val transaction : FragmentTransaction = supportFragmentManager.beginTransaction()
        when(fragment){
            0 -> {
                this.fragment = HomeFragment().newInstance("Home")/* parcelable */
                selectedItem = R.id.navigation_home
            }
            1 -> {
                this.fragment = MarketsFragment().newInstance("Markets")/* parcelable */
                selectedItem = R.id.navigation_markets
            }
            2 -> {
                this.fragment = PortfolioFragment().newInstance("Portfolio")/* parcelable */
                selectedItem = R.id.navigation_portfolio
            }
            3 -> {
                this.fragment = SettingsFragment().newInstance("Settings")/* parcelable */
                selectedItem = R.id.navigation_settings
            }
        }
        transaction.replace(R.id.frame_placeholder, this.fragment)
        transaction.commit()
        navigation.selectedItemId = selectedItem
    }

}
