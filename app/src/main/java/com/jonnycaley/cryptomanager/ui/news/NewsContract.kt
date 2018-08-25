package com.jonnycaley.cryptomanager.ui.news

import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView

class NewsContract {
    interface View : BaseView<Presenter>
    interface Presenter : BasePresenter
}