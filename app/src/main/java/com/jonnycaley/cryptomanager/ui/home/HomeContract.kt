package com.jonnycaley.cryptomanager.ui.home

import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Currency
import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.Article
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.Rate
import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView

interface HomeContract {
    interface View : BaseView<Presenter> {
        fun showNews(news: ArrayList<Article>, savedArticles: ArrayList<Article>)
        fun showTop100Changes(sortedBy: List<Currency>?, baseCurrency: Rate)
        fun hideProgressBar()
        fun showScrollLayout()
        fun showProgressBar()
        fun showNoInternet()
    }

    interface Presenter : BasePresenter {
        fun getNews()
        fun saveArticle(topArticle: Article)
        fun removeArticle(topArticle: Article)
        fun onResume()
    }
}