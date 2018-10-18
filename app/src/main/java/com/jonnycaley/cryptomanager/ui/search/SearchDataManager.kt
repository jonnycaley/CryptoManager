package com.jonnycaley.cryptomanager.ui.search

import android.content.Context
import com.jonnycaley.cryptomanager.data.ExchangeRatesService
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.ExchangeRates
import com.jonnycaley.cryptomanager.utils.Constants
import com.jonnycaley.cryptomanager.utils.RetrofitHelper
import com.jonnycaley.cryptomanager.utils.Utils
import com.jonnycaley.cryptomanager.utils.prefs.UserPreferences
import com.pacoworks.rxpaper2.RxPaperBook
import io.paperdb.Paper
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

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

    fun readStorage(key : String) : Single<String?> {
        return RxPaperBook.with(Schedulers.newThread()).read(key, null)
    }


    fun getFiats(): Single<ExchangeRates> {
        return RxPaperBook.with(Schedulers.newThread()).read(Constants.PAPER_ALL_RATES, ExchangeRates())
    }
}