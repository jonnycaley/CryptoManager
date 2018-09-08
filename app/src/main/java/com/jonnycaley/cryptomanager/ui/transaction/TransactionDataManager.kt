package com.jonnycaley.cryptomanager.ui.transaction

import android.content.Context
import com.jonnycaley.cryptomanager.utils.Utils
import com.jonnycaley.cryptomanager.utils.prefs.UserPreferences

class TransactionDataManager private constructor(val UserPreferences: UserPreferences) {

    companion object {

        private var INSTANCE: TransactionDataManager? = null

        private lateinit var context: Context

        private val TAG = "TransactionData"

        @JvmStatic
        fun getInstance(context: Context): TransactionDataManager {
            if (INSTANCE == null) {
                INSTANCE = TransactionDataManager(UserPreferences.getInstance(context))
                this.context = context
            }
            return INSTANCE!!
        }
    }



    fun checkConnection(): Boolean {
        return Utils.isNetworkConnected(context)
    }

}