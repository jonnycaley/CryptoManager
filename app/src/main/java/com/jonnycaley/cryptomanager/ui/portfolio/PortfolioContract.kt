package com.jonnycaley.cryptomanager.ui.portfolio

import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView

class PortfolioContract {
    interface View : BaseView<Presenter>
    interface Presenter : BasePresenter
}