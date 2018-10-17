package com.jonnycaley.cryptomanager.ui.article

import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.Article
import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView

interface ArticleContract {
    interface View : BaseView<Presenter> {
        fun getArticleUrl(): String?
        fun setLikeButton(isLiked: Boolean)
    }

    interface Presenter : BasePresenter {
        fun saveArticle(topArticle: Article)
        fun removeArticle(topArticle: Article)
    }
}