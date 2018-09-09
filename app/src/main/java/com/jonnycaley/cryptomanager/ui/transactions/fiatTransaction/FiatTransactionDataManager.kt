package com.jonnycaley.cryptomanager.ui.transactions.fiatTransaction

import android.content.Context
import com.jonnycaley.cryptomanager.data.ExchangeRatesService
import com.jonnycaley.cryptomanager.utils.Constants
import com.jonnycaley.cryptomanager.utils.RetrofitHelper
import com.jonnycaley.cryptomanager.utils.Utils
import com.jonnycaley.cryptomanager.utils.prefs.UserPreferences

class FiatTransactionDataManager private constructor(val UserPreferences: UserPreferences) {

    companion object {

        private var INSTANCE: FiatTransactionDataManager? = null

        private lateinit var context: Context

        private val TAG = "FiatTransactionData"

        @JvmStatic
        fun getInstance(context: Context): FiatTransactionDataManager {
            if (INSTANCE == null) {
                INSTANCE = FiatTransactionDataManager(UserPreferences.getInstance(context))
                this.context = context
            }
            return INSTANCE!!
        }
    }

    fun getExchangeRateService(): ExchangeRatesService {
        val retrofit = RetrofitHelper().createRetrofit(Constants.EXCHANGERATES_URL, null, null)
        return retrofit.create(ExchangeRatesService::class.java)
    }


    fun checkConnection(): Boolean {
        return Utils.isNetworkConnected(context)
    }

}