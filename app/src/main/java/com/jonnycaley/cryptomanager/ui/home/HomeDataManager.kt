package com.jonnycaley.cryptomanager.ui.home

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

    fun readStorage(key : String) : Single<String?> {
        return RxPaperBook.with(Schedulers.newThread()).read(key, "")
    }

    fun writeToStorage(key: String, data: String) {
        Paper.book().write(key, data)
    }

    fun getSavedArticles(): Single<ArrayList<Article>> {
        return RxPaperBook.with(Schedulers.newThread()).read(Constants.PAPER_SAVED_ARTICLES, ArrayList())
    }

    fun saveArticles(savedArticles: ArrayList<Article>) {
        Paper.book().write(Constants.PAPER_SAVED_ARTICLES, savedArticles)
    }

}