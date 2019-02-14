package com.jonnycaley.cryptomanager.ui.base

import android.content.Context
import android.content.Intent
import android.util.Log
import com.jonnycaley.cryptomanager.utils.interfaces.ActivityArgs

data class BaseArgs(val fragment: Int) : ActivityArgs {

    override fun intent(activity: Context): Intent = Intent(activity, BaseActivity::class.java).apply {
        putExtra(FRAGMENT_KEY, fragment)
    }

    companion object {
        fun deserializeFrom(intent: Intent): BaseArgs {
            return BaseArgs(
                    fragment = intent.getIntExtra(FRAGMENT_KEY, 0)
            )
        }
    }
}

private const val FRAGMENT_KEY = "fragment_key"
