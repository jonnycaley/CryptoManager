package com.jonnycaley.cryptomanager.ui.currency.viewpager.general

import com.jonnycaley.cryptomanager.data.model.CryptoCompare.HistoricalData.Data
import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView

interface GeneralContract {

    interface View : BaseView<Presenter> {
        fun getSymbol(): String
        fun loadCandlestickChart(response: Data, timeUnit: String, aggregate: Int)
        fun loadCurrencyNews(news: Array<com.jonnycaley.cryptomanager.data.model.CryptoControlNews.News>)
        fun getName(): String
    }

    interface Presenter : BasePresenter {
        fun getCurrencyChart(timeString: String, symbol: String, conversion: String, limit: Int, aggregate: Int)
        fun clearChartDisposable()
        fun getData()
    }
}