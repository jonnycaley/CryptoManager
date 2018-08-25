package com.jonnycaley.cryptomanager.ui.news

import android.content.Context
import com.jonnycaley.cryptomanager.utils.Utils
import com.jonnycaley.cryptomanager.utils.prefs.UserPreferences

class NewsDataManager private constructor(val UserPreferences: UserPreferences) {

    companion object {

        private var INSTANCE: NewsDataManager? = null

        private lateinit var context: Context

        private val TAG = "NewsData"

        @JvmStatic
        fun getInstance(context: Context): NewsDataManager {
            if (INSTANCE == null) {
                INSTANCE = NewsDataManager(UserPreferences.getInstance(context))
                this.context = context
            }
            return INSTANCE!!
        }
    }

    fun checkConnection(): Boolean {
        return Utils.isNetworkConnected(context)
    }

}