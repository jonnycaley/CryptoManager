package com.jonnycaley.cryptomanager.ui.portfolio

import android.content.Context
import com.jonnycaley.cryptomanager.data.CryptoCompareService
import com.jonnycaley.cryptomanager.data.ExchangeRatesService
import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.ExchangeRates
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.Rate
import com.jonnycaley.cryptomanager.utils.Constants
import com.jonnycaley.cryptomanager.utils.RetrofitHelper
import com.jonnycaley.cryptomanager.utils.Utils
import com.jonnycaley.cryptomanager.utils.prefs.UserPreferences
import com.pacoworks.rxpaper2.RxPaperBook
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import android.R.id.edit
import android.content.Context.MODE_PRIVATE
//import com.thefinestartist.utils.content.ContextUtil.getApplicationContext
import android.content.SharedPreferences



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

    /*
    Function gets the crypto compare service with scalars
    */
    fun getCryptoCompareServiceWithScalars(): CryptoCompareService {
        val retrofit = RetrofitHelper().createRetrofitWithScalars(Constants.CRYPTOCOMPARE_URL, Constants.CRYPTOCOMPARE_NAME, Constants.CRYPTOCOMPARE_KEY)
        return retrofit.create(CryptoCompareService::class.java)
    }

    /*
    Function gets the crypto compare service
    */
    fun getCryptoCompareService(): CryptoCompareService {
        val retrofit = RetrofitHelper().createRetrofit(Constants.CRYPTOCOMPARE_URL, Constants.CRYPTOCOMPARE_NAME, Constants.CRYPTOCOMPARE_KEY)
        return retrofit.create(CryptoCompareService::class.java)
    }

    /*
    Function gets the transactions
    */
    fun getTransactions(): Single<ArrayList<Transaction>> {
        return RxPaperBook.with(Schedulers.io()).read(Constants.PAPER_TRANSACTIONS, ArrayList())
    }

    /*
    Function gets the exchange rates service
    */
    fun getExchangeRateService(): ExchangeRatesService {
        val retrofit = RetrofitHelper().createRetrofitWithScalars(Constants.EXCHANGERATES_URL, null, null)
        return retrofit.create(ExchangeRatesService::class.java)
    }

    fun checkConnection(): Boolean {
        return Utils.isNetworkConnected(context)
    }

    fun readBaseFiat(): Single<Rate> {
        return RxPaperBook.with(Schedulers.io()).read(Constants.PAPER_BASE_RATE)
    }

    fun readFiats(): Single<ExchangeRates> {
        return RxPaperBook.with(Schedulers.io()).read(Constants.PAPER_ALL_RATES, ExchangeRates())
    }
}