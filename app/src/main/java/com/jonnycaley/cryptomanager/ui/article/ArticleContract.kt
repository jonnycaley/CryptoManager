package com.jonnycaley.cryptomanager.ui.article

import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView


class ArticleContract {
    interface View : BaseView<Presenter>

    interface Presenter : BasePresenter
}