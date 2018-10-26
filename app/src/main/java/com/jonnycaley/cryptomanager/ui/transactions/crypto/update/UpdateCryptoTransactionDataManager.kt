package com.jonnycaley.cryptomanager.ui.transactions.crypto.update

import android.content.Context
import com.jonnycaley.cryptomanager.data.CryptoCompareService
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.AllCurrencies.Currencies
import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import com.jonnycaley.cryptomanager.ui.transactions.crypto.create.CreateCryptoTransactionDataManager
import com.jonnycaley.cryptomanager.utils.Constants
import com.jonnycaley.cryptomanager.utils.RetrofitHelper
import com.jonnycaley.cryptomanager.utils.Utils
import com.jonnycaley.cryptomanager.utils.prefs.UserPreferences
import com.pacoworks.rxpaper2.RxPaperBook
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class UpdateCryptoTransactionDataManager private constructor(val UserPreferences: UserPreferences) {

    companion object {

        private var INSTANCE: UpdateCryptoTransactionDataManager? = null

        private lateinit var context: Context

        private val TAG = "UpdateCryptoTransData"

        @JvmStatic
        fun getInstance(context: Context): UpdateCryptoTransactionDataManager {
            if (INSTANCE == null) {
                INSTANCE = UpdateCryptoTransactionDataManager(UserPreferences.getInstance(context))
                this.context = context
            }
            return INSTANCE!!
        }
    }
    fun checkConnection(): Boolean {
        return Utils.isNetworkConnected(context)
    }

    fun getTransactions(): Single<ArrayList<Transaction>> {
        return RxPaperBook.with().read(Constants.PAPER_TRANSACTIONS, ArrayList())
    }

    fun saveTransactions(transactions : ArrayList<Transaction>): Completable {
        return RxPaperBook.with().write(Constants.PAPER_TRANSACTIONS, transactions)
    }

    fun getCryptoCompareService(): CryptoCompareService {
        val retrofit = RetrofitHelper().createRetrofit(Constants.CRYPTOCOMPARE_URL, null, null)
        return retrofit.create(CryptoCompareService::class.java)
    }


    fun getCryptoCompareServiceWithScalars(): CryptoCompareService {
        val retrofit = RetrofitHelper().createRetrofitWithScalars(Constants.CRYPTOCOMPARE_URL, null, null)
        return retrofit.create(CryptoCompareService::class.java)
    }

    fun getAllCryptos(): Single<Currencies> {
        return RxPaperBook.with().read(Constants.PAPER_ALL_CRYPTOS)
    }
}