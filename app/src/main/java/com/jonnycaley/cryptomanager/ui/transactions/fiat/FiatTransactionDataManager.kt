package com.jonnycaley.cryptomanager.ui.transactions.fiat

import android.content.Context
import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import com.jonnycaley.cryptomanager.utils.Constants
import com.jonnycaley.cryptomanager.utils.Utils
import com.jonnycaley.cryptomanager.utils.prefs.UserPreferences
import com.pacoworks.rxpaper2.RxPaperBook
import io.paperdb.Paper
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kotlin.collections.ArrayList

class FiatTransactionDataManager private constructor(val UserPreferences: UserPreferences) {

    companion object {

        private var INSTANCE: FiatTransactionDataManager? = null

        private lateinit var context: Context

        private val TAG = "FiatTransactionData"

        @JvmStatic
        fun getInstance(context: Context): FiatTransactionDataManager {
            if (INSTANCE == null) {
                INSTANCE = FiatTransactionDataManager(UserPreferences.getInstance(context))
                this.context = context
            }
            return INSTANCE!!
        }
    }

    fun checkConnection(): Boolean {
        return Utils.isNetworkConnected(context)
    }

    fun getTransactions(): Single<ArrayList<Transaction>> {
        println("getTransactions")
        return RxPaperBook.with(Schedulers.newThread()).read(Constants.PAPER_ALL_FIAT, ArrayList())
    }

    fun saveTransactions(transactions : ArrayList<Transaction>): Completable {
        println("saveTransactions")
        return RxPaperBook.with().write(Constants.PAPER_ALL_FIAT, transactions)
    }

}