package com.jonnycaley.cryptomanager.ui.home

import android.content.Context
import com.jonnycaley.cryptomanager.data.CoinMarketCapService
import com.jonnycaley.cryptomanager.data.CryptoControlService
import com.jonnycaley.cryptomanager.data.ExchangeRatesService
import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Currencies
import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.Article
import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.Rate
import com.jonnycaley.cryptomanager.utils.Constants
import com.jonnycaley.cryptomanager.utils.RetrofitHelper
import com.jonnycaley.cryptomanager.utils.Utils
import com.jonnycaley.cryptomanager.utils.prefs.UserPreferences
import com.pacoworks.rxpaper2.RxPaperBook
import io.paperdb.Book
import io.paperdb.Paper
import io.reactivex.*
import io.reactivex.schedulers.Schedulers


class HomeDataManager private constructor(val UserPreferences: UserPreferences) {

    companion object {

        private var INSTANCE: HomeDataManager? = null

        private lateinit var context: Context

        private val TAG = "HomeDataManager"

        @JvmStatic
        fun getInstance(context: Context): HomeDataManager {
            if (INSTANCE == null) {
                INSTANCE = HomeDataManager(UserPreferences.getInstance(context))
                this.context = context
            }
            return INSTANCE!!
        }
    }

    fun checkConnection(): Boolean {
        return Utils.isNetworkConnected(context)
    }

    fun getCryptoControlService(): CryptoControlService {
        val retrofit = RetrofitHelper().createRetrofit(Constants.CRYPTOCONTROL_URL, Constants.CRYPTOCONTROL_NAME, Constants.CRYPTOCONTROL_KEY)
        return retrofit.create(CryptoControlService::class.java)
    }

    fun getCoinMarketCapService(): CoinMarketCapService {
        val retrofit = RetrofitHelper().createRetrofit(Constants.COINMARKETCAP_URL, Constants.COINMARKETCAP_NAME, Constants.COINMARKETCAP_KEY)
        return retrofit.create(CoinMarketCapService::class.java)
    }

    fun getExchangeRateService(): ExchangeRatesService {
        val retrofit = RetrofitHelper().createRetrofitWithScalars(Constants.EXCHANGERATES_URL, null, null)
        return retrofit.create(ExchangeRatesService::class.java)
    }

    fun getBaseFiat(): Single<Rate> {
        return RxPaperBook.with().read(Constants.PAPER_BASE_RATE)
    }

    fun getSavedArticles(): Single<ArrayList<Article>> {
        return RxPaperBook.with().read(Constants.PAPER_SAVED_ARTICLES, ArrayList())
    }

    fun saveArticles(articles: ArrayList<Article>): Completable {
        return RxPaperBook.with().write(Constants.PAPER_SAVED_ARTICLES, articles)
    }

    fun saveTopNews(news: ArrayList<Article>): Completable {
        return RxPaperBook.with().write(Constants.PAPER_HOME_TOP_NEWS, news)
    }

    fun saveTop100(currencies: Currencies): Completable {
        return RxPaperBook.with().write(Constants.PAPER_HOME_TOP_100, currencies)
    }

    fun readTop100(): Single<Currencies> {
        return RxPaperBook.with().read(Constants.PAPER_HOME_TOP_100)
    }

    fun readTopNews(): Single<ArrayList<Article>> {
        return RxPaperBook.with().read(Constants.PAPER_HOME_TOP_NEWS)
    }

}