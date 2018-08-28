package com.jonnycaley.cryptomanager.ui.currency

import android.content.Context
import android.content.Intent
import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Currency
import com.jonnycaley.cryptomanager.utils.interfaces.ActivityArgs

data class CurrencyArgs(val currency : Currency) : ActivityArgs {

    override fun intent(activity: Context): Intent = Intent(activity, CurrencyActivity::class.java).apply {
        putExtra(CURRENCY_KEY, currency)
    }

    companion object {
        fun deserializeFrom(intent: Intent): CurrencyArgs{
            return CurrencyArgs(
                    currency = intent.getSerializableExtra(CURRENCY_KEY) as Currency
            )
        }
    }
}
private const val CURRENCY_KEY = "currency_key"