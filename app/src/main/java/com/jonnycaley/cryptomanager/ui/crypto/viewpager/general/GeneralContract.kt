package com.jonnycaley.cryptomanager.ui.crypto.viewpager.general

import com.jonnycaley.cryptomanager.data.model.CryptoCompare.HistoricalData.HistoricalData
import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.Article
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.Rate
import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView
import java.math.BigDecimal

interface GeneralContract {

    interface View : BaseView<Presenter> {
        fun getSymbol(): String
        fun loadCandlestickChart(response: HistoricalData, timeUnit: String, aggregate: Int, baseFiat: Rate)
        fun loadCurrencyNews(news: Array<Article>, savedArticles: ArrayList<Article>)
        fun getName(): String
        fun showCurrentPrice(close: BigDecimal?, baseFiat: Rate)
        fun showPriceChange(open: BigDecimal?, close: BigDecimal?, baseFiat: Rate)
        fun showMarketCap(marketCap: String?, baseFiat : Rate)
        fun showGeneralDataError()
        fun showDaysRange(lOW24HOUR: String?, hIGH24HOUR: String?, pRICE: String?, baseFiat: Rate)
        fun show24High(hIGH24HOUR: String?, baseFiat: Rate)
        fun show24Low(lOW24HOUR: String?, baseFiat: Rate)
        fun show24Change(cHANGEPCT24HOUR: String?)
        fun showCirculatingSupply(sUPPLY: String?)
    }

    interface Presenter : BasePresenter {
        fun getCurrencyChart(timeString: String, symbol: String, conversion: String, limit: Int, aggregate: Int)
        fun clearChartDisposable()
        fun getData()
        fun saveArticle(item: Article)
        fun removeArticle(item: Article)
    }
}