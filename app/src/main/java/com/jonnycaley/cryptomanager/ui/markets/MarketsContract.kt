package com.jonnycaley.cryptomanager.ui.markets

import android.widget.SearchView
import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Currency
import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Market.Market
import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.Article
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.Rate
import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView

interface MarketsContract {
    interface View : BaseView<Presenter> {
        fun showTop100Changes(currencies: List<Currency>?, baseFiat : Rate)
        fun showLatestArticles(latestArticles: ArrayList<Article>, savedArticles: ArrayList<Article>)
        fun getCurrencySearchView(): SearchView
        fun hideProgressBarLayout()
        fun showContentLayout()
        fun showProgressBarLayout()
        fun hideContentLayout()
        fun showMarketData(marketData: Market?)
    }

    interface Presenter : BasePresenter {
        fun refresh()
        fun saveArticle(article: Article)
        fun removeArticle(article: Article)
        fun onResume()
        fun loadMoreItems(currencies: ArrayList<Currency>?)
    }
}