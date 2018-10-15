package com.jonnycaley.cryptomanager.ui.crypto.viewpager.general

import com.jonnycaley.cryptomanager.data.model.CryptoCompare.HistoricalData.Data
import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView

interface GeneralContract {

    interface View : BaseView<Presenter> {
        fun getSymbol(): String
        fun loadCandlestickChart(response: Data, timeUnit: String, aggregate: Int)
        fun loadCurrencyNews(news: Array<com.jonnycaley.cryptomanager.data.model.CryptoControlNews.News>)
        fun getName(): String
        fun showCurrentPrice(close: Double?)
        fun showPriceChange(open: Double?, close: Double?)
        fun showMarketCap(marketCap: String?)
        fun showGeneralDataError()
        fun showDaysRange(lOW24HOUR: String?, hIGH24HOUR: String?, pRICE: String?)
        fun show24High(hIGH24HOUR: String?)
        fun show24Low(lOW24HOUR: String?)
        fun show24Change(cHANGEPCT24HOUR: String?)
        fun showCirculatingSupply(sUPPLY: String?)
    }

    interface Presenter : BasePresenter {
        fun getCurrencyChart(timeString: String, symbol: String, conversion: String, limit: Int, aggregate: Int)
        fun clearChartDisposable()
        fun getData()
    }
}