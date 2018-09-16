package com.jonnycaley.cryptomanager.ui.pickers.pair

import android.content.Context
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.Exchanges.Exchanges
import com.jonnycaley.cryptomanager.utils.Constants
import com.jonnycaley.cryptomanager.utils.Utils
import com.jonnycaley.cryptomanager.utils.prefs.UserPreferences
import com.pacoworks.rxpaper2.RxPaperBook
import io.paperdb.Paper
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class PickerPairDataManager private constructor(val UserPreferences: UserPreferences) {

    companion object {

        private var INSTANCE: PickerPairDataManager? = null

        private lateinit var context: Context

        private val TAG = "PickerPairData"

        @JvmStatic
        fun getInstance(context: Context): PickerPairDataManager {
            if (INSTANCE == null) {
                INSTANCE = PickerPairDataManager(UserPreferences.getInstance(context))
                this.context = context
            }
            return INSTANCE!!
        }
    }



    fun checkConnection(): Boolean {
        return Utils.isNetworkConnected(context)
    }

    fun getExchanges(): Single<String?> {
        return RxPaperBook.with(Schedulers.newThread()).read(Constants.PAPER_ALL_EXCHANGES, null)
    }
}