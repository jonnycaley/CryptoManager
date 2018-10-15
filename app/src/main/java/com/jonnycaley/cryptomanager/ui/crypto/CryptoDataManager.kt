package com.jonnycaley.cryptomanager.ui.crypto

import android.content.Context
import com.jonnycaley.cryptomanager.utils.Utils
import com.jonnycaley.cryptomanager.utils.prefs.UserPreferences

class CryptoDataManager private constructor(val UserPreferences: UserPreferences) {

    companion object {

        private var INSTANCE: CryptoDataManager? = null

        private lateinit var context: Context

        private val TAG = "CurrencyData"

        @JvmStatic
        fun getInstance(context: Context): CryptoDataManager {
            if (INSTANCE == null) {
                INSTANCE = CryptoDataManager(UserPreferences.getInstance(context))
                this.context = context
            }
            return INSTANCE!!
        }
    }



    fun checkConnection(): Boolean {
        return Utils.isNetworkConnected(context)
    }

}