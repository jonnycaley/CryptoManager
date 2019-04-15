package com.jonnycaley.cryptomanager.ui.news

import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Currency
import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.News.Article
import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView

interface NewsContract {
    interface View : BaseView<Presenter> {
        fun showNews(news: HashMap<Article, Currency?>, savedArticles: ArrayList<Article>)
        fun showTop8Changes(sortedBy: ArrayList<Currency>, illuminate : Boolean)
        fun hideProgressBar()
        fun showScrollLayout()
        fun showProgressBar()
        fun showNoInternetLayout()
//        fun showMoreNews(linkedCrypto: HashMap<Article, Currency?>, savedArticles: ArrayList<Article>)
        fun setIsLoading(b: Boolean)
        fun hideNoInternetLayout()
        fun showError()
    }

    interface Presenter : BasePresenter {
        fun saveArticle(topArticle: Article)
        fun removeArticle(topArticle: Article)
        fun onRefresh()
        fun getMoreArticles(size: Int)
        fun onResume()
        fun getNews(showProgressLayout: Boolean)
    }
}