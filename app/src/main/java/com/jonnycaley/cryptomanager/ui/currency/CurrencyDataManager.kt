package com.jonnycaley.cryptomanager.ui.currency

import android.content.Context
import com.jonnycaley.cryptomanager.utils.Utils
import com.jonnycaley.cryptomanager.utils.prefs.UserPreferences


class CurrencyDataManager private constructor(val UserPreferences: UserPreferences) {

    companion object {

        private var INSTANCE: CurrencyDataManager? = null

        private lateinit var context: Context

        private val TAG = "CurrencyData"

        @JvmStatic
        fun getInstance(context: Context): CurrencyDataManager {
            if (INSTANCE == null) {
                INSTANCE = CurrencyDataManager(UserPreferences.getInstance(context))
                this.context = context
            }
            return INSTANCE!!
        }
    }



    fun checkConnection(): Boolean {
        return Utils.isNetworkConnected(context)
    }

}