package com.jonnycaley.cryptomanager.ui.article

import android.content.Context
import com.jonnycaley.cryptomanager.utils.Utils
import com.jonnycaley.cryptomanager.utils.prefs.UserPreferences

class ArticleDataManager private constructor(val UserPreferences: UserPreferences) {

    companion object {

        private var INSTANCE: ArticleDataManager? = null

        private lateinit var context: Context

        private val TAG = "ArticleData"

        @JvmStatic
        fun getInstance(context: Context): ArticleDataManager {
            if (INSTANCE == null) {
                INSTANCE = ArticleDataManager(UserPreferences.getInstance(context))
                this.context = context
            }
            return INSTANCE!!
        }
    }


    fun checkConnection(): Boolean {
        return Utils.isNetworkConnected(context)
    }

}