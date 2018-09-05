package com.jonnycaley.cryptomanager.ui.search

import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView

interface SearchContract {
    interface View : BaseView<Presenter>

    interface Presenter : BasePresenter
}