package com.jonnycaley.cryptomanager.ui.splash

import android.content.Context
import com.jonnycaley.cryptomanager.data.CryptoCompareService
import com.jonnycaley.cryptomanager.utils.Constants
import com.jonnycaley.cryptomanager.utils.RetrofitHelper
import com.jonnycaley.cryptomanager.utils.Utils
import com.jonnycaley.cryptomanager.utils.prefs.UserPreferences
import io.paperdb.Paper

class   SplashDataManager private constructor(val UserPreferences: UserPreferences) {

    companion object {

        private var INSTANCE: SplashDataManager? = null

        private lateinit var context: Context

        private val TAG = "SplashData"

        @JvmStatic
        fun getInstance(context: Context): SplashDataManager {
            if (INSTANCE == null) {
                INSTANCE = SplashDataManager(UserPreferences.getInstance(context))
                this.context = context
            }
            return INSTANCE!!
        }
    }

    fun getCryptoCompareService(): CryptoCompareService {
        val retrofit = RetrofitHelper().createRetrofitWithScalars(Constants.CRYPTOCOMPARE_URL, null, null)
        return retrofit.create(CryptoCompareService::class.java)
    }

    fun checkConnection(): Boolean {
        return Utils.isNetworkConnected(context)
    }

    fun writeToStorage(key: String, data: String) {
        Paper.book().write(key, data)
    }

}