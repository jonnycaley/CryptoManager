package com.jonnycaley.cryptomanager.ui.transactions.fiat

import android.content.Context
import com.jonnycaley.cryptomanager.data.CryptoCompareService
import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import com.jonnycaley.cryptomanager.utils.Constants
import com.jonnycaley.cryptomanager.utils.RetrofitHelper
import com.jonnycaley.cryptomanager.utils.Utils
import com.jonnycaley.cryptomanager.utils.prefs.UserPreferences
import com.pacoworks.rxpaper2.RxPaperBook
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers


class FiatTransactionDataManager private constructor(val UserPreferences: UserPreferences) {

    companion object {

        private var INSTANCE: FiatTransactionDataManager? = null

        private lateinit var context: Context

        private val TAG = "UpdateCryptoTransactionData"

        @JvmStatic
        fun getInstance(context: Context): FiatTransactionDataManager {
            if (INSTANCE == null) {
                INSTANCE = FiatTransactionDataManager(UserPreferences.getInstance(context))
                Companion.context = context
            }
            return INSTANCE!!
        }
    }

    fun getTransactions(): Single<ArrayList<Transaction>> {
        return RxPaperBook.with(Schedulers.io()).read(Constants.PAPER_TRANSACTIONS, ArrayList())
    }

    fun saveTransactions(transactions : ArrayList<Transaction>): Completable {
        return RxPaperBook.with(Schedulers.io()).write(Constants.PAPER_TRANSACTIONS, transactions)
    }

    fun getCryptoCompareService(): CryptoCompareService {
        val retrofit = RetrofitHelper().createRetrofit(Constants.CRYPTOCOMPARE_URL, Constants.CRYPTOCOMPARE_NAME, Constants.CRYPTOCOMPARE_KEY)
        return retrofit.create(CryptoCompareService::class.java)
    }

    fun checkConnection(): Boolean {
        return Utils.isNetworkConnected(context)
    }


    fun getCryptoCompareServiceWithScalars(): CryptoCompareService {
        val retrofit = RetrofitHelper().createRetrofitWithScalars(Constants.CRYPTOCOMPARE_URL, Constants.CRYPTOCOMPARE_NAME, Constants.CRYPTOCOMPARE_KEY)
        return retrofit.create(CryptoCompareService::class.java)
    }

}