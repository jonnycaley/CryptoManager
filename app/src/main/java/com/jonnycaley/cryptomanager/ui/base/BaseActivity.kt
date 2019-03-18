package com.jonnycaley.cryptomanager.ui.base

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.ui.news.NewsFragment
import com.jonnycaley.cryptomanager.ui.markets.MarketsFragment
import com.jonnycaley.cryptomanager.ui.home.HomeFragment
import com.jonnycaley.cryptomanager.ui.settings.SettingsFragment
import com.jonnycaley.cryptomanager.utils.interfaces.OnThemeChangedListener
import kotlinx.android.synthetic.main.content_home.*

class BaseActivity : AppCompatActivity(), OnThemeChangedListener {

    lateinit var fragment: Fragment

    val args by lazy { BaseArgs.deserializeFrom(intent) }

    val fragment1: Fragment = HomeFragment()
    val fragment2: Fragment = MarketsFragment()
    val fragment3: Fragment = NewsFragment()
    val fragment4: Fragment = SettingsFragment()

    val fragment1TAG = "1"
    val fragment2TAG = "2"
    val fragment3TAG = "3"
    val fragment4TAG = "4"

    val fm = supportFragmentManager
    var active = fragment1

    var TAG = this.javaClass.simpleName

    private var currentFragment: Fragment? = null

    override fun updateThemeChanged() {
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
            navigation.background = resources.getDrawable(R.color.black)
        else
            navigation.background = resources.getDrawable(R.color.white)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.darktheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)

//        fm.beginTransaction().add(R.id.frame_placeholder, fragment4, fragment4TAG).hide(fragment4).commit()
//        fm.beginTransaction().add(R.id.frame_placeholder, fragment3, fragment3TAG).hide(fragment3).commit()
//        fm.beginTransaction().add(R.id.frame_placeholder, fragment2, fragment2TAG).hide(fragment2).commit()
//        fm.beginTransaction().add(R.id.frame_placeholder, fragment1, fragment1TAG).hide(fragment1).commit()

//        if(fm.findFragmentByTag(fragment1TAG) == null)
//            fm.beginTransaction().add(R.id.frame_placeholder, fragment1, fragment1TAG).hide(fragment1).commit()
//        fm.beginTransaction().hide(active).show(fragment1).commit()
//        active = fragment1

        loadFragmentNew(args.fragment)

        navigation.enableItemShiftingMode(false)
        navigation.enableShiftingMode(false)
        navigation.setTextVisibility(false)
        navigation.setPadding(0,15,0,15)

        navigation.onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_portfolio -> {
                    if(fm.findFragmentByTag(fragment1TAG) == null)
                        fm.beginTransaction().add(R.id.frame_placeholder, fragment1, fragment1TAG).hide(fragment1).commit()
                    else
                        (fragment1 as HomeFragment).onTabClicked(active == fragment1)
                    fm.beginTransaction().hide(active).show(fragment1).commit()
                    active = fragment1
                    return@OnNavigationItemSelectedListener true
                }

                R.id.navigation_markets -> {
                    if(fm.findFragmentByTag(fragment2TAG) == null)
                        fm.beginTransaction().add(R.id.frame_placeholder, fragment2, fragment2TAG).hide(fragment2).commit()
                    else
                        (fragment2 as MarketsFragment).onTabClicked(active == fragment2)
                    fm.beginTransaction().hide(active).show(fragment2).commit()
                    active = fragment2
                    return@OnNavigationItemSelectedListener true
                }

                R.id.navigation_home -> {
                    if(fm.findFragmentByTag(fragment3TAG) == null)
                        fm.beginTransaction().add(R.id.frame_placeholder, fragment3, fragment3TAG).hide(fragment3).commit()
                    else
                        (fragment3 as NewsFragment).onTabClicked(active == fragment3)
                    fm.beginTransaction().hide(active).show(fragment3).commit()
                    active = fragment3
                    return@OnNavigationItemSelectedListener true
                }

                R.id.navigation_settings -> {
                    if(fm.findFragmentByTag(fragment4TAG) == null)
                        fm.beginTransaction().add(R.id.frame_placeholder, fragment4, fragment4TAG).hide(fragment4).commit()
                    else
                        (fragment4 as SettingsFragment).onTabClicked(active == fragment4)
                    fm.beginTransaction().hide(active).show(fragment4).commit()
                    active = fragment4
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

//        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
//        loadFragment(args.fragment)
    }

    private fun loadFragmentNew(fragment: Int) {

        var selectedItem = 0

        when(fragment){
            0 -> {
                if(fm.findFragmentByTag(fragment1TAG) == null)
                    fm.beginTransaction().add(R.id.frame_placeholder, fragment1, fragment1TAG).commit()
                fm.beginTransaction().show(fragment1).commit()
                active = fragment1
                selectedItem = R.id.navigation_portfolio
            }
            1 -> {
                if(fm.findFragmentByTag(fragment2TAG) == null)
                    fm.beginTransaction().add(R.id.frame_placeholder,fragment2, fragment2TAG).commit()
                fm.beginTransaction().show(fragment2).commit()
                active = fragment2
                selectedItem = R.id.navigation_markets
            }
            2 -> {
                if(fm.findFragmentByTag(fragment3TAG) == null)
                    fm.beginTransaction().add(R.id.frame_placeholder,fragment3, fragment3TAG).commit()
                fm.beginTransaction().show(fragment3).commit()
                active = fragment3
                selectedItem = R.id.navigation_home
            }
            3 -> {
                if(fm.findFragmentByTag(fragment4TAG) == null)
                    fm.beginTransaction().add(R.id.frame_placeholder,fragment4, fragment4TAG).commit()
                fm.beginTransaction().show(fragment4).commit()
                active = fragment4
                selectedItem = R.id.navigation_settings
            }
        }
        navigation.selectedItemId = selectedItem
    }

//    private fun replaceFragment(fragment: Fragment, tag: String) {
//        if (fragment != currentFragment) {
//            fragmentManager
//                    ?.beginTransaction()
//                    ?.replace(R.id.frame_placeholder, fragment, tag)
//                    ?.commit()
//            currentFragment = fragment
//        }
//    }

//    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
//
//        val fragmentManager: FragmentManager = supportFragmentManager
//
//        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
//
//        when (item.itemId) {
//            R.id.navigation_home -> {
//                fragmentTransaction.replace(R.id.frame_placeholder, NewsFragment()).commit()
//                return@OnNavigationItemSelectedListener true
//            }
//            R.id.navigation_markets -> {
//                fragmentTransaction.replace(R.id.frame_placeholder, MarketsFragment()).commit()
//                return@OnNavigationItemSelectedListener true
//            }
//            R.id.navigation_portfolio -> {
//                fragmentTransaction.replace(R.id.frame_placeholder, HomeFragment()).commit()
//                return@OnNavigationItemSelectedListener true
//            }
//            R.id.navigation_settings -> {
//                fragmentTransaction.replace(R.id.frame_placeholder, SettingsFragment()).commit()
//                return@OnNavigationItemSelectedListener true
//            }
//        }
//        false
//    }

//    private fun loadFragment(fragment: Int) {
//
//        var selectedItem = 0
//
//        val transaction : FragmentTransaction = supportFragmentManager.beginTransaction()
//        when(fragment){
//            0 -> {
//                this.fragment = NewsFragment().newInstance("Home")/* parcelable */
//                selectedItem = R.id.navigation_home
//            }
//            1 -> {
//                this.fragment = MarketsFragment().newInstance("Markets")/* parcelable */
//                selectedItem = R.id.navigation_markets
//            }
//            2 -> {
//                this.fragment = HomeFragment().newInstance("Portfolio")/* parcelable */
//                selectedItem = R.id.navigation_portfolio
//            }
//            3 -> {
//                this.fragment = SettingsFragment().newInstance("Settings")/* parcelable */
//                selectedItem = R.id.navigation_settings
//            }
//        }
//        transaction.replace(R.id.frame_placeholder, this.fragment)
//        transaction.commit()
//        navigation.selectedItemId = selectedItem
//    }

}
