package com.jonnycaley.cryptomanager.ui.transactions.crypto.update

import android.content.Context
import android.content.Intent
import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import com.jonnycaley.cryptomanager.utils.interfaces.ActivityArgs

data class UpdateCryptoTransactionArgs(val transaction: Transaction) : ActivityArgs {

    override fun intent(activity: Context): Intent = Intent(activity, UpdateCryptoTransactionActivity::class.java).apply {
        putExtra(TRANSACTION_KEY, transaction)
    }

    companion object {
        fun deserializeFrom(intent: Intent): UpdateCryptoTransactionArgs {
            return UpdateCryptoTransactionArgs(
                    transaction = intent.getSerializableExtra(TRANSACTION_KEY) as Transaction
            )
        }
    }
}

private const val TRANSACTION_KEY = "transaction_key"