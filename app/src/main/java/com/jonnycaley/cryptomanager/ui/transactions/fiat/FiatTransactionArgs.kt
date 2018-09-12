package com.jonnycaley.cryptomanager.ui.transactions.fiat

import android.content.Context
import android.content.Intent
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.AllCurrencies.Datum
import com.jonnycaley.cryptomanager.utils.interfaces.ActivityArgs

data class FiatTransactionArgs(val currency: String) : ActivityArgs {

    override fun intent(activity: Context): Intent = Intent(activity, FiatTransactionActivity::class.java).apply {
        putExtra(FIAT_KEY, currency)
    }

    companion object {
        fun deserializeFrom(intent: Intent): FiatTransactionArgs {
            return FiatTransactionArgs(
                    currency = intent.getSerializableExtra(FIAT_KEY) as String
            )
        }
    }
}

private const val FIAT_KEY = "fiat_key"