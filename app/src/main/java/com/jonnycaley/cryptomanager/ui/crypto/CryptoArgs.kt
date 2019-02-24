package com.jonnycaley.cryptomanager.ui.crypto

import android.content.Context
import android.content.Intent
import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Currency
import com.jonnycaley.cryptomanager.utils.interfaces.ActivityArgs

data class CryptoArgs(val currencySymbol : String, val currencyName : String) : ActivityArgs {

    override fun intent(activity: Context): Intent = Intent(activity, CryptoActivity::class.java).apply {
        putExtra(CURRENCY_SYMBOL_KEY, currencySymbol)
        putExtra(CURRENCY_NAME_KEY, currencyName)
    }

    companion object {
        fun deserializeFrom(intent: Intent): CryptoArgs{
            return CryptoArgs(
                    currencySymbol = intent.getSerializableExtra(CURRENCY_SYMBOL_KEY) as String,
                    currencyName = intent.getSerializableExtra(CURRENCY_NAME_KEY) as String
            )
        }
    }
}
private const val CURRENCY_SYMBOL_KEY = "currency_symbol_key"
private const val CURRENCY_NAME_KEY = "currency_name_key"