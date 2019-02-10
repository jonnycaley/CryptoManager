package com.jonnycaley.cryptomanager.ui.settings.selectCurrency

import android.content.Context
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.ExchangeRates
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.Rate
import com.jonnycaley.cryptomanager.utils.Constants
import com.jonnycaley.cryptomanager.utils.Utils
import com.jonnycaley.cryptomanager.utils.prefs.UserPreferences
import com.pacoworks.rxpaper2.RxPaperBook
import io.paperdb.Paper
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers


class SelectCurrencyDataManager private constructor(val UserPreferences: UserPreferences) {

    companion object {

        private var INSTANCE: SelectCurrencyDataManager? = null

        private lateinit var context: Context

        private val TAG = "SelectCurrencyData"

        @JvmStatic
        fun getInstance(context: Context): SelectCurrencyDataManager {
            if (INSTANCE == null) {
                INSTANCE = SelectCurrencyDataManager(UserPreferences.getInstance(context))
                this.context = context
            }
            return INSTANCE!!
        }
    }

    fun checkConnection(): Boolean {
        return Utils.isNetworkConnected(context)
    }

    fun getBaseFiat(): Single<Rate> {
        return RxPaperBook.with(Schedulers.io()).read(Constants.PAPER_BASE_RATE, Rate())
    }

    fun getFiats(): Single<ExchangeRates> {
        return RxPaperBook.with(Schedulers.io()).read(Constants.PAPER_ALL_RATES, ExchangeRates())
    }

    fun saveBaseCurrency(symbol: Rate?): Completable {
        return RxPaperBook.with(Schedulers.io()).write(Constants.PAPER_BASE_RATE, symbol)
    }
}