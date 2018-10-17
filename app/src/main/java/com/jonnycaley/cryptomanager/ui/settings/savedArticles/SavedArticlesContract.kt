package com.jonnycaley.cryptomanager.ui.settings.savedArticles

import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.Article
import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView

interface SavedArticlesContract {
    interface View : BaseView<Presenter> {
        fun showSavedNews(news: ArrayList<Article>)
        fun showNoArticles()
        fun hideNoArticles()
    }

    interface Presenter : BasePresenter {
        fun removeArticle(savedArticles : ArrayList<Article>?, topArticle: Article)
    }
}