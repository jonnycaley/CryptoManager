package com.jonnycaley.cryptomanager.ui.pickers.exchange

import android.content.Context
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.Exchanges.Exchanges
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.ExchangeRates
import com.jonnycaley.cryptomanager.utils.Constants
import com.jonnycaley.cryptomanager.utils.Utils
import com.jonnycaley.cryptomanager.utils.prefs.UserPreferences
import com.pacoworks.rxpaper2.RxPaperBook
import io.paperdb.Paper
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class PickerExchangeDataManager private constructor(val UserPreferences: UserPreferences) {

    companion object {

        private var INSTANCE: PickerExchangeDataManager? = null

        private lateinit var context: Context

        private val TAG = "PickerExchangeData"

        @JvmStatic
        fun getInstance(context: Context): PickerExchangeDataManager {
            if (INSTANCE == null) {
                INSTANCE = PickerExchangeDataManager(UserPreferences.getInstance(context))
                this.context = context
            }
            return INSTANCE!!
        }
    }

    /*
    Function returns the current status of the internet connection
    */
    fun checkConnection(): Boolean {
        return Utils.isNetworkConnected(context)
    }

    fun readAllExchanges(): Single<Exchanges> {
        return RxPaperBook.with(Schedulers.io()).read(Constants.PAPER_ALL_EXCHANGES)
    }

}