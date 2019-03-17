package com.jonnycaley.cryptomanager.ui.crypto.viewpager.general

import android.content.Context
import com.jonnycaley.cryptomanager.data.CryptoCompareService
import com.jonnycaley.cryptomanager.data.CryptoControlService
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.AllCurrencies.Currencies
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


class GeneralDataManager private constructor(val userPreferences: UserPreferences) {

    companion object {

        private var INSTANCE: GeneralDataManager? = null

        private lateinit var context: Context

        private val TAG = "GeneralData"

        @JvmStatic
        fun getInstance(context: Context): GeneralDataManager {
            if (INSTANCE == null) {
                INSTANCE = GeneralDataManager(UserPreferences.getInstance(context))
                this.context = context
            }
            return INSTANCE!!
        }
    }


    fun checkConnection(): Boolean {
        return Utils.isNetworkConnected(context)
    }

    fun getCryptoCompareService(): CryptoCompareService {
        val retrofit = RetrofitHelper().createRetrofit(Constants.CRYPTOCOMPARE_URL, Constants.CRYPTOCOMPARE_NAME, Constants.CRYPTOCOMPARE_KEY)
        return retrofit.create(CryptoCompareService::class.java)
    }

    fun getCryptoCompareServiceWithScalars(): CryptoCompareService {
        val retrofit = RetrofitHelper().createRetrofitWithScalars(Constants.CRYPTOCOMPARE_URL, Constants.CRYPTOCOMPARE_NAME, Constants.CRYPTOCOMPARE_KEY)
        return retrofit.create(CryptoCompareService::class.java)
    }

    fun getCryptoControlNewsService(): CryptoControlService {
        val retrofit = RetrofitHelper().createRetrofit(Constants.CRYPTOCONTROL_URL, Constants.CRYPTOCONTROL_NAME, Constants.CRYPTOCONTROL_KEY)
        return retrofit.create(CryptoControlService::class.java)
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

    fun getAllCryptos(): Single<Currencies> {
        return RxPaperBook.with(Schedulers.io()).read(Constants.PAPER_ALL_CRYPTOS)
    }

}