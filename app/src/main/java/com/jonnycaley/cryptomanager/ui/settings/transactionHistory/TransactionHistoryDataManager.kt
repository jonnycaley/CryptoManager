package com.jonnycaley.cryptomanager.ui.settings.transactionHistory

import android.content.Context
import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.ExchangeRates
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.Rate
import com.jonnycaley.cryptomanager.utils.Constants
import com.jonnycaley.cryptomanager.utils.Utils
import com.jonnycaley.cryptomanager.utils.prefs.UserPreferences
import com.pacoworks.rxpaper2.RxPaperBook
import io.paperdb.Paper
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers


class TransactionHistoryDataManager private constructor(val UserPreferences: UserPreferences) {

    companion object {

        private var INSTANCE: TransactionHistoryDataManager? = null

        private lateinit var context: Context

        private val TAG = "TransactionHistoryData"

        @JvmStatic
        fun getInstance(context: Context): TransactionHistoryDataManager {
            if (INSTANCE == null) {
                INSTANCE = TransactionHistoryDataManager(UserPreferences.getInstance(context))
                this.context = context
            }
            return INSTANCE!!
        }
    }

    fun checkConnection(): Boolean {
        return Utils.isNetworkConnected(context)
    }

    fun getTransactions(): Single<ArrayList<Transaction>> {
        return RxPaperBook.with(Schedulers.io()).read(Constants.PAPER_TRANSACTIONS, ArrayList())
    }
}