package com.jonnycaley.cryptomanager.ui.transactions.fiat.create

import android.content.Context
import android.content.Intent
import com.jonnycaley.cryptomanager.utils.interfaces.ActivityArgs

data class CreateFiatTransactionArgs(val currency: String, val backpressToPortfolio : Boolean) : ActivityArgs {

    override fun intent(activity: Context): Intent = Intent(activity, CreateFiatTransactionActivity::class.java).apply {
        putExtra(FIAT_KEY, currency)
        putExtra(IS_FROM_PORTFOLIO, backpressToPortfolio)
    }

    companion object {
        fun deserializeFrom(intent: Intent): CreateFiatTransactionArgs {
            return CreateFiatTransactionArgs(
                    currency = intent.getSerializableExtra(FIAT_KEY) as String,
                    backpressToPortfolio = intent.getBooleanExtra(IS_FROM_PORTFOLIO, false)
            )
        }
    }
}

private const val FIAT_KEY = "fiat_key"
private const val IS_FROM_PORTFOLIO = "from_protfolio_key"