package com.jonnycaley.cryptomanager.ui.markets

import android.content.Context
import com.jonnycaley.cryptomanager.data.CoinMarketCapService
import com.jonnycaley.cryptomanager.data.CryptoControlService
import com.jonnycaley.cryptomanager.data.NomicsService
import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Currency
import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Market.Market
import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.News.Article
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.Rate
import com.jonnycaley.cryptomanager.utils.Constants
import com.jonnycaley.cryptomanager.utils.RetrofitHelper
import com.jonnycaley.cryptomanager.utils.Utils
import com.jonnycaley.cryptomanager.utils.prefs.UserPreferences
import com.pacoworks.rxpaper2.RxPaperBook
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class MarketsDataManager private constructor(val UserPreferences: UserPreferences) {

    companion object {

        private var INSTANCE: MarketsDataManager? = null

        private lateinit var context: Context

        private val TAG = "MarketsData"

        @JvmStatic
        fun getInstance(context: Context): MarketsDataManager {
            if (INSTANCE == null) {
                INSTANCE = MarketsDataManager(UserPreferences.getInstance(context))
                this.context = context
            }
            return INSTANCE!!
        }
    }

    fun getCoinMarketCapService(): CoinMarketCapService {
        val retrofit = RetrofitHelper().createRetrofit(Constants.COINMARKETCAP_URL, Constants.COINMARKETCAP_NAME, Constants.COINMARKETCAP_KEY)
        return retrofit.create(CoinMarketCapService::class.java)
    }

    fun getNomicsService(): NomicsService {
        val retrofit = RetrofitHelper().createRetrofit(Constants.NOMICS_URL, Constants.NOMICS_NAME, Constants.NOMICS_KEY)
        return retrofit.create(NomicsService::class.java)
    }

    fun getCryptoControlService(): CryptoControlService {
        val retrofit = RetrofitHelper().createRetrofit(Constants.CRYPTOCONTROL_URL, Constants.CRYPTOCONTROL_NAME, Constants.CRYPTOCONTROL_KEY)
        return retrofit.create(CryptoControlService::class.java)
    }

    fun checkConnection(): Boolean {
        return Utils.isNetworkConnected(context)
    }

    fun getSavedArticles(): Single<ArrayList<Article>> {
        return RxPaperBook.with(Schedulers.io()).read(Constants.PAPER_SAVED_ARTICLES, ArrayList())
    }

    fun saveArticles(savedArticles: ArrayList<Article>) : Completable {
        return RxPaperBook.with(Schedulers.io()).write(Constants.PAPER_SAVED_ARTICLES, savedArticles)
    }

    fun getBaseFiat(): Single<Rate> {
        return RxPaperBook.with(Schedulers.io()).read(Constants.PAPER_BASE_RATE)
    }

    fun saveCurrencies(currencies: ArrayList<Currency>) : Completable {
        return RxPaperBook.with(Schedulers.io()).write(Constants.PAPER_MARKETS_ALL_CURRENCIES, currencies)
    }

    fun getCurrencies() : Single<ArrayList<Currency>>{
        return RxPaperBook.with(Schedulers.io()).read(Constants.PAPER_MARKETS_ALL_CURRENCIES, ArrayList())
    }

    fun saveMarketInfo(currencies: Market) : Completable {
        return RxPaperBook.with(Schedulers.io()).write(Constants.PAPER_MARKETS_GENERAL_INFO, currencies)
    }

    fun getMarketInfo() : Single<Market> {
        return RxPaperBook.with(Schedulers.io()).read(Constants.PAPER_MARKETS_GENERAL_INFO, Market())
    }
}