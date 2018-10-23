package com.jonnycaley.cryptomanager.ui.pickers.currency

import android.content.Context
import com.jonnycaley.cryptomanager.data.ExchangeRatesService
import com.jonnycaley.cryptomanager.utils.Constants
import com.jonnycaley.cryptomanager.utils.RetrofitHelper
import com.jonnycaley.cryptomanager.utils.Utils
import com.jonnycaley.cryptomanager.utils.prefs.UserPreferences

class PickerCurrencyDataManager private constructor(val UserPreferences: UserPreferences) {

    companion object {

        private var INSTANCE: PickerCurrencyDataManager? = null

        private lateinit var context: Context

        private val TAG = "PickerCurrencyData"

        @JvmStatic
        fun getInstance(context: Context): PickerCurrencyDataManager {
            if (INSTANCE == null) {
                INSTANCE = PickerCurrencyDataManager(UserPreferences.getInstance(context))
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

}