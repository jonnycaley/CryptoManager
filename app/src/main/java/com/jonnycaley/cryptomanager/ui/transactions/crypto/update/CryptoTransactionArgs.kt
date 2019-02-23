package com.jonnycaley.cryptomanager.ui.transactions.crypto.update

import android.content.Context
import android.content.Intent
import com.jonnycaley.cryptomanager.data.model.DataBase.NotTransaction
import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import com.jonnycaley.cryptomanager.utils.interfaces.ActivityArgs

data class CryptoTransactionArgs(val transaction: Transaction? , val notTransactions: NotTransaction?, val backPressOnEnd : Boolean) : ActivityArgs {

    override fun intent(activity: Context): Intent = Intent(activity, CryptoTransactionActivity::class.java).apply {
        putExtra(TRANSACTION_KEY, transaction)
        putExtra(NOT_TRANSACTION_KEY, notTransactions)
        putExtra(BACKPRESS_KEY, backPressOnEnd)
    }

    companion object {
        fun deserializeFrom(intent: Intent): CryptoTransactionArgs {
            return CryptoTransactionArgs(
                    transaction = intent.getSerializableExtra(TRANSACTION_KEY) as Transaction?,
                    notTransactions = intent.getSerializableExtra(NOT_TRANSACTION_KEY) as NotTransaction?,
                    backPressOnEnd = intent.getBooleanExtra(BACKPRESS_KEY, false)
            )
        }
    }
}

private const val TRANSACTION_KEY = "transaction_key"
private const val NOT_TRANSACTION_KEY = "not_transaction_key"
private const val BACKPRESS_KEY = "backpress_key"