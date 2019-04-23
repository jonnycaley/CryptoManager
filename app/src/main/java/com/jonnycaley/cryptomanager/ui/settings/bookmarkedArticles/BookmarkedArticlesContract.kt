package com.jonnycaley.cryptomanager.ui.settings.bookmarkedArticles

import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.News.Article
import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView

interface BookmarkedArticlesContract {
    interface View : BaseView<Presenter> {
        fun showSavedNews(news: ArrayList<Article>)
        fun showNoArticles()
        fun hideNoArticles()
        fun showError()
        fun hideProgressLayout()
        fun showProgressLayout()
    }

    interface Presenter : BasePresenter {
        fun removeArticle(savedArticles : ArrayList<Article>?, topArticle: Article)
        fun loadSavedArticles()
    }
}