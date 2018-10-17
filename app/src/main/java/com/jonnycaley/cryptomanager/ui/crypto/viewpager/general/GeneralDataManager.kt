package com.jonnycaley.cryptomanager.ui.crypto.viewpager.general

import android.content.Context
import com.jonnycaley.cryptomanager.data.CryptoCompareService
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
        val retrofit = RetrofitHelper().createRetrofit(Constants.CRYPTOCOMPARE_URL, null, null)
        return retrofit.create(CryptoCompareService::class.java)
    }

    fun getCryptoCompareServiceWithScalars(): CryptoCompareService {
        val retrofit = RetrofitHelper().createRetrofitWithScalars(Constants.CRYPTOCOMPARE_URL, null, null)
        return retrofit.create(CryptoCompareService::class.java)
    }

    fun getCryptoControlNewsService(): CryptoControlService {
        val retrofit = RetrofitHelper().createRetrofit(Constants.CRYPTOCONTROL_URL, Constants.CRYPTOCONTROL_NAME, Constants.CRYPTOCONTROL_KEY)
        return retrofit.create(CryptoControlService::class.java)
    }

    fun readStorage(key : String) : Single<String?> {
        return RxPaperBook.with(Schedulers.newThread()).read(key, null)
    }

    fun getSavedArticles(): Single<ArrayList<Article>> {
        return RxPaperBook.with(Schedulers.newThread()).read(Constants.PAPER_SAVED_ARTICLES, ArrayList())
    }

    fun saveArticles(savedArticles: ArrayList<Article>) {
        Paper.book().write(Constants.PAPER_SAVED_ARTICLES, savedArticles)
    }

}