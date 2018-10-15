package com.jonnycaley.cryptomanager.ui.transactions.crypto.create

import android.content.Context
import android.content.Intent
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.AllCurrencies.Datum
import com.jonnycaley.cryptomanager.utils.interfaces.ActivityArgs

data class CreateCryptoTransactionArgs(val currency: Datum, val imageUrl: String?, val baseUrl: String?) : ActivityArgs {

    override fun intent(activity: Context): Intent = Intent(activity, CreateCryptoTransactionActivity::class.java).apply {
        putExtra(CURRENCY_KEY, currency)
        putExtra(IMAGE_URL_KEY, imageUrl)
        putExtra(BASE_URL_KEY, baseUrl)
    }

    companion object {
        fun deserializeFrom(intent: Intent): CreateCryptoTransactionArgs {
            return CreateCryptoTransactionArgs(
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