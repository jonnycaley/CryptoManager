package com.jonnycaley.cryptomanager.utils

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.pacoworks.rxpaper2.RxPaperBook
import io.paperdb.Paper

class App : Application() {

    private var isNightModeEnabled = false

    override fun onCreate() {
        super.onCreate()
        RxPaperBook.init(this)

        FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/Roboto-Regular.ttf");
        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/Roboto-Regular.ttf");
        FontsOverride.setDefaultFont(this, "SERIF", "fonts/Roboto-Regular.ttf");
        FontsOverride.setDefaultFont(this, "SANS_SERIF", "fonts/Roboto-Regular.ttf");

        val mPrefs : SharedPreferences =  PreferenceManager.getDefaultSharedPreferences(this);
        this.isNightModeEnabled = mPrefs.getBoolean("NIGHT_MODE", false)
    }

    fun isNightModeEnabled(): Boolean {
        return isNightModeEnabled
    }

    fun setIsNightModeEnabled(isNightModeEnabled: Boolean) {
        this.isNightModeEnabled = isNightModeEnabled
    }
}