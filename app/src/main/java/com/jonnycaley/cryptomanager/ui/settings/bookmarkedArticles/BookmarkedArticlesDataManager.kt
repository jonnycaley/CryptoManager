package com.jonnycaley.cryptomanager.ui.settings.bookmarkedArticles

import android.content.Context
import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.News.Article
import com.jonnycaley.cryptomanager.utils.Constants
import com.jonnycaley.cryptomanager.utils.Utils
import com.jonnycaley.cryptomanager.utils.prefs.UserPreferences
import com.pacoworks.rxpaper2.RxPaperBook
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers


class BookmarkedArticlesDataManager private constructor(val UserPreferences: UserPreferences) {

    companion object {

        private var INSTANCE: BookmarkedArticlesDataManager? = null

        private lateinit var context: Context

        private val TAG = this::class.java.simpleName

        @JvmStatic
        fun getInstance(context: Context): BookmarkedArticlesDataManager {
            if (INSTANCE == null) {
                INSTANCE = BookmarkedArticlesDataManager(UserPreferences.getInstance(context))
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

    fun getArticles(): Single<ArrayList<Article>> {
        return RxPaperBook.with(Schedulers.io()).read(Constants.PAPER_SAVED_ARTICLES, ArrayList())
    }

    fun saveArticles(articles: ArrayList<Article>): Completable {
        return RxPaperBook.with(Schedulers.io()).write(Constants.PAPER_SAVED_ARTICLES, articles)
    }

}