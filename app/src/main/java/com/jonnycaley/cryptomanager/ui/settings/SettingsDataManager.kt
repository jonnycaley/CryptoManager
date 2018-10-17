package com.jonnycaley.cryptomanager.ui.settings

import android.content.Context
import com.jonnycaley.cryptomanager.utils.Constants
import com.jonnycaley.cryptomanager.utils.Utils
import com.jonnycaley.cryptomanager.utils.prefs.UserPreferences
import com.pacoworks.rxpaper2.RxPaperBook
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class SettingsDataManager private constructor(val UserPreferences: UserPreferences) {

    companion object {

        private var INSTANCE: SettingsDataManager? = null

        private lateinit var context: Context

        private val TAG = "SettingsData"

        @JvmStatic
        fun getInstance(context: Context): SettingsDataManager {
            if (INSTANCE == null) {
                INSTANCE = SettingsDataManager(UserPreferences.getInstance(context))
                this.context = context
            }
            return INSTANCE!!
        }
    }

    fun checkConnection(): Boolean {
        return Utils.isNetworkConnected(context)
    }


    fun deletePortfolio(): Completable {
        return RxPaperBook.with(Schedulers.newThread()).delete(Constants.PAPER_TRANSACTIONS)
    }

    fun deleteSavedArticles(): Completable {
        return RxPaperBook.with(Schedulers.newThread()).delete(Constants.SAVED_ARTICLES)
    }

}