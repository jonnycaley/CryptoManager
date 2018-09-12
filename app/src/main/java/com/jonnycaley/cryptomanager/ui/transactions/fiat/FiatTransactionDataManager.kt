package com.jonnycaley.cryptomanager.ui.transactions.fiat

import android.content.Context
import com.jonnycaley.cryptomanager.utils.Constants
import com.jonnycaley.cryptomanager.utils.Utils
import com.jonnycaley.cryptomanager.utils.prefs.UserPreferences
import io.paperdb.Paper

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

    fun saveFiatTransaction(depositType: String, exchange: CharSequence, currency: CharSequence, quantity: CharSequence, date: CharSequence, notes: CharSequence) {

    }

}