package com.jonnycaley.cryptomanager.ui.article

import android.content.Context
import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.News.Article
import com.jonnycaley.cryptomanager.utils.Constants
import com.jonnycaley.cryptomanager.utils.Utils
import com.jonnycaley.cryptomanager.utils.prefs.UserPreferences
import com.pacoworks.rxpaper2.RxPaperBook
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class ArticleDataManager private constructor(val UserPreferences: UserPreferences) {

    companion object {

        private var INSTANCE: ArticleDataManager? = null

        private lateinit var context: Context

        private val TAG = this::class.java.simpleName

        @JvmStatic
        fun getInstance(context: Context): ArticleDataManager {
            if (INSTANCE == null) {
                INSTANCE = ArticleDataManager(UserPreferences.getInstance(context))
                this.context = context
            }
            return INSTANCE!!
        }
    }

    /*
    Function returns the connection status
     */
    fun checkConnection(): Boolean {
        return Utils.isNetworkConnected(context)
    }

    /*
    Function gets the saved articles from storage
     */
    fun getSavedArticles(): Single<ArrayList<Article>> {
        return RxPaperBook.with(Schedulers.io()).read(Constants.PAPER_SAVED_ARTICLES, ArrayList())
    }

    /*
    Function saves the articles to storage
     */
    fun saveArticles(savedArticles: ArrayList<Article>) : Completable {
        return RxPaperBook.with(Schedulers.io()).write(Constants.PAPER_SAVED_ARTICLES, savedArticles)
    }

}