package com.jonnycaley.cryptomanager.ui.transactions.update.fiat

import android.content.Context
import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import com.jonnycaley.cryptomanager.utils.Constants
import com.jonnycaley.cryptomanager.utils.Utils
import com.jonnycaley.cryptomanager.utils.prefs.UserPreferences
import com.pacoworks.rxpaper2.RxPaperBook
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers


class UpdateFiatTransactionDataManager private constructor(val UserPreferences: UserPreferences) {

    companion object {

        private var INSTANCE: UpdateFiatTransactionDataManager? = null

        private lateinit var context: Context

        private val TAG = "UpdateCryptoTransactionData"

        @JvmStatic
        fun getInstance(context: Context): UpdateFiatTransactionDataManager {
            if (INSTANCE == null) {
                INSTANCE = UpdateFiatTransactionDataManager(UserPreferences.getInstance(context))
                this.context = context
            }
            return INSTANCE!!
        }
    }

    fun getTransactions(): Single<ArrayList<Transaction>> {
        return RxPaperBook.with(Schedulers.newThread()).read(Constants.PAPER_TRANSACTIONS, ArrayList())
    }

    fun saveTransactions(transactions : ArrayList<Transaction>): Completable {
        return RxPaperBook.with().write(Constants.PAPER_TRANSACTIONS, transactions)
    }

    fun checkConnection(): Boolean {
        return Utils.isNetworkConnected(context)
    }

}