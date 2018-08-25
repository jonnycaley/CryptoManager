package com.jonnycaley.cryptomanager.utils.prefs

import android.content.Context

class UserPreferences private constructor(context: Context) : PreferencesHelper() {

    companion object {

        private var INSTANCE: UserPreferences? = null

        @JvmStatic fun getInstance(context: Context): UserPreferences {
            if(INSTANCE == null) {
                INSTANCE = UserPreferences((context))
                init(context)
            }

            return INSTANCE!!
        }
    }
}