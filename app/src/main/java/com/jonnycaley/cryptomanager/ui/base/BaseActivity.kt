package com.jonnycaley.cryptomanager.ui.base

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.ui.news.NewsFragment
import com.jonnycaley.cryptomanager.ui.markets.MarketsFragment
import com.jonnycaley.cryptomanager.ui.portfolio.PortfolioFragment
import com.jonnycaley.cryptomanager.ui.settings.SettingsFragment
import com.jonnycaley.cryptomanager.utils.Utils
import com.jonnycaley.cryptomanager.utils.interfaces.OnThemeChangedListener
import kotlinx.android.synthetic.main.content_home.*

class BaseActivity : AppCompatActivity(), OnThemeChangedListener {

    lateinit var fragment: androidx.fragment.app.Fragment

    val args by lazy { BaseArgs.deserializeFrom(intent) }

    val fragment1: androidx.fragment.app.Fragment = PortfolioFragment()
    val fragment2: androidx.fragment.app.Fragment = MarketsFragment()
    val fragment3: androidx.fragment.app.Fragment = NewsFragment()
    val fragment4: androidx.fragment.app.Fragment = SettingsFragment()

    val fragment1TAG = "1"
    val fragment2TAG = "2"
    val fragment3TAG = "3"
    val fragment4TAG = "4"

    val fm = supportFragmentManager
    var active = fragment1

    var TAG = this.javaClass.simpleName

    private var currentFragment: androidx.fragment.app.Fragment? = null


    /*
    Function updates the theme when it is changed
    */
    override fun updateThemeChanged() {
        if(Utils.isDarkTheme()) {  //dark theme
            navigation.background = resources.getDrawable(R.color.black)
        }
        else { //light theme
            navigation.background = resources.getDrawable(R.color.white)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        if(Utils.isDarkTheme()) { //dark theme
            setTheme(R.style.darktheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)

        loadFragmentNew(args.fragment)

        navigation.enableItemShiftingMode(false)
        navigation.enableShiftingMode(false)
        navigation.setTextVisibility(true)
        navigation.setPadding(0,15,0,15)
        navigation.setTextSize(10F)

        navigation.onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item -> //set the navigations
            when (item.itemId) {
                R.id.navigation_portfolio -> {
                    if(fm.findFragmentByTag(fragment1TAG) == null)
                        fm.beginTransaction().add(R.id.frame_placeholder, fragment1, fragment1TAG).hide(fragment1).commit()
                    else
                        (fragment1 as PortfolioFragment).onTabClicked(active == fragment1)
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
    }

    /*
    Function loads a new fragment if it is not already there, otherwise it just shows the already created one
    */
    private fun loadFragmentNew(fragment: Int) {

        var selectedItem = 0

        when(fragment){ //load the corresponding fragments
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

    /*
    Function handles the back pressed button and stops it from going back to the splash screen
    */
    override fun onBackPressed() {

    }

}
