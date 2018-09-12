package com.jonnycaley.cryptomanager.ui.search

import android.content.Context
import com.jonnycaley.cryptomanager.data.ExchangeRatesService
import com.jonnycaley.cryptomanager.utils.Constants
import com.jonnycaley.cryptomanager.utils.RetrofitHelper
import com.jonnycaley.cryptomanager.utils.Utils
import com.jonnycaley.cryptomanager.utils.prefs.UserPreferences
import io.paperdb.Paper

class SearchDataManager private constructor(val UserPreferences: UserPreferences) {

    companion object {

        private var INSTANCE: SearchDataManager? = null

        private lateinit var context: Context

        private val TAG = "SearchData"

        @JvmStatic
        fun getInstance(context: Context): SearchDataManager {
            if (INSTANCE == null) {
                INSTANCE = SearchDataManager(UserPreferences.getInstance(context))
                this.context = context
            }
            return INSTANCE!!
        }
    }

    fun getExchangeRateService(): ExchangeRatesService {
        val retrofit = RetrofitHelper().createRetrofitWithScalars(Constants.EXCHANGERATES_URL, null, null)
        return retrofit.create(ExchangeRatesService::class.java)
    }

    fun checkConnection(): Boolean {
        return Utils.isNetworkConnected(context)
    }

    fun readStorage(key : String) : String? {
        return Paper.book().read(key, null)
    }

}