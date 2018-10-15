package com.jonnycaley.cryptomanager.ui.settings.savedArticles

import android.content.Context
import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.News
import com.jonnycaley.cryptomanager.utils.Constants
import com.jonnycaley.cryptomanager.utils.Utils
import com.jonnycaley.cryptomanager.utils.prefs.UserPreferences
import com.pacoworks.rxpaper2.RxPaperBook
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers


class SavedArticlesDataManager private constructor(val UserPreferences: UserPreferences) {

    companion object {

        private var INSTANCE: SavedArticlesDataManager? = null

        private lateinit var context: Context

        private val TAG = this::class.java.simpleName

        @JvmStatic
        fun getInstance(context: Context): SavedArticlesDataManager {
            if (INSTANCE == null) {
                INSTANCE = SavedArticlesDataManager(UserPreferences.getInstance(context))
                this.context = context
            }
            return INSTANCE!!
        }
    }

    fun checkConnection(): Boolean {
        return Utils.isNetworkConnected(context)
    }


    fun getArticles(): Single<ArrayList<News>> {
        return RxPaperBook.with(Schedulers.newThread()).read(Constants.SAVED_ARTICLES, ArrayList())
    }

}