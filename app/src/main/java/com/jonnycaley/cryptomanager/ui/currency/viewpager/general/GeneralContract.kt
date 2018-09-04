package com.jonnycaley.cryptomanager.ui.currency.viewpager.general

import com.jonnycaley.cryptomanager.data.model.CryptoCompare.HistoricalData.Data
import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView

interface GeneralContract {

    interface View : BaseView<Presenter> {
        fun getSymbol(): String
        fun showCandlestickChart(response: Data, timeUnit: String, aggregate: Int)
    }

    interface Presenter : BasePresenter {
        fun getCurrencyData(timeString: String, symbol: String, conversion: String, limit: Int, aggregate: Int)
        fun clearChartDisposable()
    }
}