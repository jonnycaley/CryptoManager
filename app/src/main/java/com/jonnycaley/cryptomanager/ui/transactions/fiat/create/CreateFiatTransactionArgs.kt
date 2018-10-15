package com.jonnycaley.cryptomanager.ui.transactions.fiat.create

import android.content.Context
import android.content.Intent
import com.jonnycaley.cryptomanager.utils.interfaces.ActivityArgs

data class CreateFiatTransactionArgs(val currency: String) : ActivityArgs {

    override fun intent(activity: Context): Intent = Intent(activity, CreateFiatTransactionActivity::class.java).apply {
        putExtra(FIAT_KEY, currency)
    }

    companion object {
        fun deserializeFrom(intent: Intent): CreateFiatTransactionArgs {
            return CreateFiatTransactionArgs(
                    currency = intent.getSerializableExtra(FIAT_KEY) as String
            )
        }
    }
}

private const val FIAT_KEY = "fiat_key"