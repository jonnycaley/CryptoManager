package com.jonnycaley.cryptomanager.ui.settings.savedArticles

import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.News
import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView

interface SavedArticlesContract {
    interface View : BaseView<Presenter> {
        fun showSavedNews(news: ArrayList<News>)
    }

    interface Presenter : BasePresenter
}