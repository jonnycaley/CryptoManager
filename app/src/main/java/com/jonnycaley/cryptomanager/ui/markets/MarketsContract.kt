package com.jonnycaley.cryptomanager.ui.markets

import android.widget.SearchView
import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Currency
import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Market.Market
import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.News.Article
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.Rate
import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView

interface MarketsContract {
    interface View : BaseView<Presenter> {
        fun showTop100Changes(currencies: ArrayList<Currency>, baseFiat: Rate, resultsCount: Int)
        fun getCurrencySearchView(): SearchView
        fun hideProgressBarLayout()
        fun showContentLayout()
        fun showProgressBarLayout()
        fun hideContentLayout()
        fun showMarketData(marketData: Market)
        fun stopRefreshing()
        fun getCurrenciesAdapterCount(): Int
        fun getSort(): String
        fun showNoInternetLayout()
        fun hideNoInternetLayout()
    }

    interface Presenter : BasePresenter {
        fun refresh()
        fun saveArticle(article: Article)
        fun removeArticle(article: Article)
        fun onResume()
        fun loadMoreItems(currencies: ArrayList<Currency>?, moreItemsCount: Int, searchString: CharSequence)
        fun getResultsCounter(): Int
        fun getOnlineData()
    }
}