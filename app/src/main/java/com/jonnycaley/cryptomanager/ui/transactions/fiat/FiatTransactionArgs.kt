package com.jonnycaley.cryptomanager.ui.transactions.fiat

import android.content.Context
import android.content.Intent
import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import com.jonnycaley.cryptomanager.utils.interfaces.ActivityArgs


data class UpdateFiatTransactionArgs(val transaction: Transaction?, val currency: String?, val backpressToPortfolio : Boolean?) : ActivityArgs {

    override fun intent(activity: Context): Intent = Intent(activity, FiatTransactionActivity::class.java).apply {
        putExtra(TRANSACTION_KEY, transaction)
        putExtra(FIAT_KEY, currency)
        putExtra(IS_FROM_PORTFOLIO, backpressToPortfolio)
    }

    companion object {
        fun deserializeFrom(intent: Intent): UpdateFiatTransactionArgs {
            return UpdateFiatTransactionArgs(
                    transaction = intent.getSerializableExtra(TRANSACTION_KEY) as Transaction?,
                    currency = intent.getSerializableExtra(FIAT_KEY) as String?,
                    backpressToPortfolio = intent.getBooleanExtra(IS_FROM_PORTFOLIO, false)
            )
        }
    }
}

private const val TRANSACTION_KEY = "transaction_key"

private const val FIAT_KEY = "fiat_key"
private const val IS_FROM_PORTFOLIO = "from_protfolio_key"