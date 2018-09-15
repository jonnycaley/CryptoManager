package com.jonnycaley.cryptomanager.ui.fiat

import android.content.Context
import android.content.Intent
import com.jonnycaley.cryptomanager.utils.interfaces.ActivityArgs

data class FiatArgs(val fiat : String) : ActivityArgs {

    override fun intent(activity: Context): Intent = Intent(activity, FiatActivity::class.java).apply {
        putExtra(FIAT_KEY, fiat)
    }

    companion object {
        fun deserializeFrom(intent: Intent): FiatArgs{
            return FiatArgs(
                    fiat = intent.getStringExtra(FIAT_KEY)
            )
        }
    }
}
private const val FIAT_KEY = "fiat_key"