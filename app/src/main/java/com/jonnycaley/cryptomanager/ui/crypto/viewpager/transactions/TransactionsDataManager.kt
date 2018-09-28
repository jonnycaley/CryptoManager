package com.jonnycaley.cryptomanager.ui.crypto.viewpager.transactions

import android.content.Context
import com.jonnycaley.cryptomanager.data.CryptoCompareService
import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import com.jonnycaley.cryptomanager.utils.Constants
import com.jonnycaley.cryptomanager.utils.RetrofitHelper
import com.jonnycaley.cryptomanager.utils.Utils
import com.jonnycaley.cryptomanager.utils.prefs.UserPreferences
import com.pacoworks.rxpaper2.RxPaperBook
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class TransactionsDataManager private constructor(val userPreferences: UserPreferences) {

    companion object {

        private var INSTANCE: TransactionsDataManager? = null

        private lateinit var context: Context

        private val TAG = "TransactionsData"

        @JvmStatic
        fun getInstance(context: Context): TransactionsDataManager {
            if (INSTANCE == null) {
                INSTANCE = TransactionsDataManager(UserPreferences.getInstance(context))
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

    fun getTransactions(): Single<ArrayList<Transaction>> {
        return RxPaperBook.with(Schedulers.newThread()).read(Constants.PAPER_TRANSACTIONS, ArrayList())
    }
}