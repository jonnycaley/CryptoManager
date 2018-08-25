package com.jonnycaley.cryptomanager.ui.portfolio

import android.content.Context
import com.jonnycaley.cryptomanager.utils.Utils
import com.jonnycaley.cryptomanager.utils.prefs.UserPreferences

class PortfolioDataManager private constructor(val UserPreferences: UserPreferences) {

    companion object {

        private var INSTANCE: PortfolioDataManager? = null

        private lateinit var context: Context

        private val TAG = "PortfolioData"

        @JvmStatic
        fun getInstance(context: Context): PortfolioDataManager {
            if (INSTANCE == null) {
                INSTANCE = PortfolioDataManager(UserPreferences.getInstance(context))
                this.context = context
            }
            return INSTANCE!!
        }
    }

    fun checkConnection(): Boolean {
        return Utils.isNetworkConnected(context)
    }

}