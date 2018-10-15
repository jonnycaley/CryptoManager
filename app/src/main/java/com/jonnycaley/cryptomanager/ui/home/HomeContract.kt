package com.jonnycaley.cryptomanager.ui.home

import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Currency
import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.News
import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView

interface HomeContract {
    interface View : BaseView<Presenter> {
        fun showNews(news: Array<News>)
        fun showTop100Changes(sortedBy: List<Currency>?)
        fun hideProgressBar()
        fun showScrollLayout()
        fun showProgressBar()
        fun showNoInternet()
    }

    interface Presenter : BasePresenter {
        fun getNews()
        fun saveArticle(topArticle: News)
        fun removeArticle(topArticle: News)
    }
}