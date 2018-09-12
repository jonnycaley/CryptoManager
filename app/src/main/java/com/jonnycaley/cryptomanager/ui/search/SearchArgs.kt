package com.jonnycaley.cryptomanager.ui.search

import android.content.Context
import android.content.Intent
import com.jonnycaley.cryptomanager.utils.interfaces.ActivityArgs

data class SearchArgs(val transactionString: String) : ActivityArgs {

    override fun intent(activity: Context): Intent = Intent(activity, SearchActivity::class.java).apply {
        putExtra(ACTIVITY_KEY, transactionString)
    }

    companion object {
        fun deserializeFrom(intent: Intent): SearchArgs {
            return SearchArgs(
                    transactionString = intent.getStringExtra(ACTIVITY_KEY)
            )
        }
    }
}

private const val ACTIVITY_KEY = "activity_key"