package com.jonnycaley.cryptomanager.ui.transaction

import android.content.Context
import android.content.Intent
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.AllCurrencies.Datum
import com.jonnycaley.cryptomanager.utils.interfaces.ActivityArgs

data class TransactionArgs(val currency: Datum, val imageUrl: String?, val baseUrl: String?) : ActivityArgs {

    override fun intent(activity: Context): Intent = Intent(activity, TransactionActivity::class.java).apply {
        putExtra(CURRENCY_KEY, currency)
    }

    companion object {
        fun deserializeFrom(intent: Intent): TransactionArgs {
            return TransactionArgs(
                    currency = intent.getSerializableExtra(CURRENCY_KEY) as Datum,
                    imageUrl = intent.getSerializableExtra(IMAGE_URL_KEY) as String?,
                    baseUrl = intent.getSerializableExtra(BASE_URL_KEY) as String?
            )
        }
    }
}

private const val CURRENCY_KEY = "currency_key"
private const val IMAGE_URL_KEY = "image_url_key"
private const val BASE_URL_KEY = "base_url_key"