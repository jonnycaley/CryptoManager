package com.jonnycaley.cryptomanager.ui.fiat

import android.content.Context
import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import com.jonnycaley.cryptomanager.utils.Constants
import com.jonnycaley.cryptomanager.utils.Utils
import com.jonnycaley.cryptomanager.utils.prefs.UserPreferences
import com.pacoworks.rxpaper2.RxPaperBook
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class FiatDataManager private constructor(val UserPreferences: UserPreferences) {

    companion object {

        private var INSTANCE: FiatDataManager? = null

        private lateinit var context: Context

        private val TAG = "FiatData"

        @JvmStatic
        fun getInstance(context: Context): FiatDataManager {
            if (INSTANCE == null) {
                INSTANCE = FiatDataManager(UserPreferences.getInstance(context))
                this.context = context
            }
            return INSTANCE!!
        }
    }

    /*
    Function reads the transactions from storage
    */
    fun getTransactions(): Single<ArrayList<Transaction>> {
        return RxPaperBook.with(Schedulers.io()).read(Constants.PAPER_TRANSACTIONS, ArrayList())
    }

    /*
    Function returns the current state of the internet connection
    */
    fun checkConnection(): Boolean {
        return Utils.isNetworkConnected(context)
    }

}