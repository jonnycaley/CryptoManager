package com.jonnycaley.cryptomanager.ui.markets

import android.content.Context
import com.jonnycaley.cryptomanager.data.CoinMarketCapService
import com.jonnycaley.cryptomanager.data.CryptoControlService
import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.Article
import com.jonnycaley.cryptomanager.utils.Constants
import com.jonnycaley.cryptomanager.utils.RetrofitHelper
import com.jonnycaley.cryptomanager.utils.Utils
import com.jonnycaley.cryptomanager.utils.prefs.UserPreferences
import com.pacoworks.rxpaper2.RxPaperBook
import io.paperdb.Paper
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

    fun getCryptoControlService(): CryptoControlService {
        val retrofit = RetrofitHelper().createRetrofit(Constants.CRYPTOCONTROL_URL, Constants.CRYPTOCONTROL_NAME, Constants.CRYPTOCONTROL_KEY)
        return retrofit.create(CryptoControlService::class.java)
    }

    fun checkConnection(): Boolean {
        return Utils.isNetworkConnected(context)
    }

    fun getSavedArticles(): Single<ArrayList<Article>> {
        return RxPaperBook.with(Schedulers.newThread()).read(Constants.SAVED_ARTICLES, ArrayList())
    }

    fun saveArticles(savedArticles: ArrayList<Article>) {
        Paper.book().write(Constants.SAVED_ARTICLES, savedArticles)
    }


}