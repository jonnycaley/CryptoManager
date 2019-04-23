package com.jonnycaley.cryptomanager.ui.splash

import android.content.Context
import com.jonnycaley.cryptomanager.data.CryptoCompareService
import com.jonnycaley.cryptomanager.data.ExchangeRatesService
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.AllCurrencies.Currencies
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.Exchanges.Exchanges
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.ExchangeRates
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.Rate
import com.jonnycaley.cryptomanager.ui.base.BaseArgs
import com.jonnycaley.cryptomanager.utils.Constants
import com.jonnycaley.cryptomanager.utils.RetrofitHelper
import com.jonnycaley.cryptomanager.utils.Utils
import com.jonnycaley.cryptomanager.utils.prefs.UserPreferences
import com.pacoworks.rxpaper2.RxPaperBook
import io.paperdb.Paper
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

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
        val retrofit = RetrofitHelper().createRetrofitWithScalars(Constants.CRYPTOCOMPARE_URL, Constants.CRYPTOCOMPARE_NAME, Constants.CRYPTOCOMPARE_KEY)
        return retrofit.create(CryptoCompareService::class.java)
    }

    fun getExchangeRateService(): ExchangeRatesService {
        val retrofit = RetrofitHelper().createRetrofitWithScalars(Constants.EXCHANGERATES_URL, null, null)
        return retrofit.create(ExchangeRatesService::class.java)
    }

    /*
    Function returns the current status of the internet connection
    */
    fun checkConnection(): Boolean {
        return Utils.isNetworkConnected(context)
    }

    fun saveAllRates(rates: ExchangeRates) : Completable {
        return RxPaperBook.with(Schedulers.io()).write(Constants.PAPER_ALL_RATES, rates)
    }

    fun saveBaseRate(baseRate : Rate): Completable {
        return RxPaperBook.with(Schedulers.io()).write(Constants.PAPER_BASE_RATE, baseRate)
    }

    fun readAllExchanges(): Single<Exchanges> {
        return RxPaperBook.with(Schedulers.io()).read(Constants.PAPER_ALL_EXCHANGES)
    }

    fun saveAllCryptos(crypto: Currencies?): Completable {
        return RxPaperBook.with(Schedulers.io()).write(Constants.PAPER_ALL_CRYPTOS, crypto)
    }

    fun saveAllExchanges(exchanges: Exchanges?): Completable {
        return RxPaperBook.with(Schedulers.io()).write(Constants.PAPER_ALL_EXCHANGES, exchanges)
    }

    fun readBaseRate(): Single<Rate> {
        return RxPaperBook.with(Schedulers.io()).read(Constants.PAPER_BASE_RATE)
    }

    fun readAllRates(): Single<ExchangeRates> {
        return RxPaperBook.with(Schedulers.io()).read(Constants.PAPER_ALL_RATES)
    }

    fun readTheme(): Single<Boolean> {
        return RxPaperBook.with(Schedulers.io()).read<Boolean>(Constants.PAPER_THEME, false)
    }

}