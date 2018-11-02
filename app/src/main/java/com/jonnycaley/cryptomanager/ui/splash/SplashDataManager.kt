package com.jonnycaley.cryptomanager.ui.splash

import android.content.Context
import com.jonnycaley.cryptomanager.data.CryptoCompareService
import com.jonnycaley.cryptomanager.data.ExchangeRatesService
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.AllCurrencies.Currencies
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.Exchanges.Exchanges
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.ExchangeRates
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.Rate
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
        val retrofit = RetrofitHelper().createRetrofitWithScalars(Constants.CRYPTOCOMPARE_URL, null, null)
        return retrofit.create(CryptoCompareService::class.java)
    }

    fun getExchangeRateService(): ExchangeRatesService {
        val retrofit = RetrofitHelper().createRetrofitWithScalars(Constants.EXCHANGERATES_URL, null, null)
        return retrofit.create(ExchangeRatesService::class.java)
    }

    fun checkConnection(): Boolean {
        return Utils.isNetworkConnected(context)
    }

//    fun writeToStorage(key: String, data: String) {
//        Paper.book().write(key, data)
//    }

//    fun readStorage(key : String) : Single<String?> {
//        return RxPaperBook.with(Schedulers.computation()).read(key, "")
//    }

    fun saveAllRates(rates: ExchangeRates) : Completable {
//        Paper.book().write(Constants.PAPER_ALL_RATES, jsonToCurrencies)
        return RxPaperBook.with(Schedulers.io()).write(Constants.PAPER_ALL_RATES, rates)

    }

    fun saveBaseRate(baseRate : Rate): Completable {
        return RxPaperBook.with(Schedulers.io()).write(Constants.PAPER_BASE_RATE, baseRate)
//        Paper.book().write(Constants.PAPER_BASE_RATE, baseRate)
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
        return RxPaperBook.with().read<Boolean>(Constants.PAPER_THEME, false)
    }

}