package com.jonnycaley.cryptomanager.ui.news

import android.content.Context
import com.jonnycaley.cryptomanager.data.CoinMarketCapService
import com.jonnycaley.cryptomanager.data.CryptoControlService
import com.jonnycaley.cryptomanager.data.ExchangeRatesService
import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Currency
import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.News.Article
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.Rate
import com.jonnycaley.cryptomanager.utils.Constants
import com.jonnycaley.cryptomanager.utils.RetrofitHelper
import com.jonnycaley.cryptomanager.utils.Utils
import com.jonnycaley.cryptomanager.utils.prefs.UserPreferences
import com.pacoworks.rxpaper2.RxPaperBook
import io.reactivex.*
import io.reactivex.schedulers.Schedulers


class NewsDataManager private constructor(val UserPreferences: UserPreferences) {

    companion object {

        private var INSTANCE: NewsDataManager? = null

        private lateinit var context: Context

        private val TAG = "NewsDataManager"

        @JvmStatic
        fun getInstance(context: Context): NewsDataManager {
            if (INSTANCE == null) {
                INSTANCE = NewsDataManager(UserPreferences.getInstance(context))
                this.context = context
            }
            return INSTANCE!!
        }
    }

    /*
    Function returns the current status of the internet connection
    */
    fun checkConnection(): Boolean {
        return Utils.isNetworkConnected(context)
    }

    /*
    Function gets the crypto control service
    */
    fun getCryptoControlService(): CryptoControlService {
        val retrofit = RetrofitHelper().createRetrofit(Constants.CRYPTOCONTROL_URL, Constants.CRYPTOCONTROL_NAME, Constants.CRYPTOCONTROL_KEY)
        return retrofit.create(CryptoControlService::class.java)
    }

    /*
    Function gets the coin market cap service
    */
    fun getCoinMarketCapService(): CoinMarketCapService {
        val retrofit = RetrofitHelper().createRetrofit(Constants.COINMARKETCAP_URL, Constants.COINMARKETCAP_NAME, Constants.COINMARKETCAP_KEY)
        return retrofit.create(CoinMarketCapService::class.java)
    }

    /*
    Function reads the base fiat from storage
    */
    fun getBaseFiat(): Single<Rate> {
        return RxPaperBook.with(Schedulers.io()).read(Constants.PAPER_BASE_RATE)
    }

    /*
    Function reads the saved articles from storage
    */
    fun getSavedArticles(): Single<ArrayList<Article>> {
        return RxPaperBook.with(Schedulers.io()).read(Constants.PAPER_SAVED_ARTICLES, ArrayList())
    }

    /*
    Function saves the articles to storage
    */
    fun saveArticles(articles: ArrayList<Article>): Completable {
        return RxPaperBook.with(Schedulers.io()).write(Constants.PAPER_SAVED_ARTICLES, articles)
    }

    /*
    Function saves the top news to storage
    */
    fun saveTopNews(news: ArrayList<Article>): Completable {
        return RxPaperBook.with(Schedulers.io()).write(Constants.PAPER_HOME_TOP_NEWS, news)
    }

    /*
    Function saves the top 100 to storage
    */
    fun saveTop100(currencies: List<Currency>?): Completable {
        return RxPaperBook.with(Schedulers.io()).write(Constants.PAPER_HOME_TOP_100, currencies)
    }
}