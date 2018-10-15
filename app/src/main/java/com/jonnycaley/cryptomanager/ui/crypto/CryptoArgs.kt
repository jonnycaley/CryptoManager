package com.jonnycaley.cryptomanager.ui.crypto

import android.content.Context
import android.content.Intent
import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Currency
import com.jonnycaley.cryptomanager.utils.interfaces.ActivityArgs

data class CryptoArgs(val currencySymbol : String) : ActivityArgs {

    override fun intent(activity: Context): Intent = Intent(activity, CryptoActivity::class.java).apply {
        putExtra(CURRENCY_KEY, currencySymbol)
    }

    companion object {
        fun deserializeFrom(intent: Intent): CryptoArgs{
            return CryptoArgs(
                    currencySymbol = intent.getSerializableExtra(CURRENCY_KEY) as String
            )
        }
    }
}
private const val CURRENCY_KEY = "currency_key"