package com.jonnycaley.cryptomanager.ui.crypto.viewpager.general

import com.jonnycaley.cryptomanager.data.model.CryptoCompare.GeneralData.Data
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.HistoricalData.HistoricalData
import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.News.Article
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.Rate
import com.jonnycaley.cryptomanager.data.model.Utils.Chart
import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView
import java.math.BigDecimal

interface GeneralContract {

    interface View : BaseView<Presenter> {
        fun getSymbol(): String
        fun loadCandlestickChart(response: HistoricalData, timeUnit: Chart, aggregate: Int, baseFiat: Rate)
        fun loadCurrencyNews(news: Array<Article>, savedArticles: ArrayList<Article>)
        fun getName(): String
        fun showCurrentPrice(close: BigDecimal?, baseFiat: Rate)
        fun showPriceChange(open: BigDecimal, close: BigDecimal, baseFiat: Rate)
        fun showGeneralDataError()
        fun hideRefreshing()
        fun getSelectedChartTimeFrame(): Chart
        fun showVolume(vOLUME24HOUR: String, baseFiat: Rate)
        fun showGlobalData(data: Data?, baseFiat: Rate)
        fun updateSavedArticles(articles: ArrayList<Article>)
    }

    interface Presenter : BasePresenter {
        fun getCurrencyChart(chart : Chart, symbol: String, conversion : String)
        fun clearChartDisposable()
        fun getData()
        fun saveArticle(item: Article)
        fun removeArticle(item: Article)
        fun onResume()
    }
}