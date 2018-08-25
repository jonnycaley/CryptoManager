package com.jonnycaley.cryptomanager.ui.settings

import android.content.Context
import com.jonnycaley.cryptomanager.utils.Utils
import com.jonnycaley.cryptomanager.utils.prefs.UserPreferences

class SettingsDataManager private constructor(val UserPreferences: UserPreferences) {

    companion object {

        private var INSTANCE: SettingsDataManager? = null

        private lateinit var context: Context

        private val TAG = "SettingsData"

        @JvmStatic
        fun getInstance(context: Context): SettingsDataManager {
            if (INSTANCE == null) {
                INSTANCE = SettingsDataManager(UserPreferences.getInstance(context))
                this.context = context
            }
            return INSTANCE!!
        }
    }

    fun checkConnection(): Boolean {
        return Utils.isNetworkConnected(context)
    }

}