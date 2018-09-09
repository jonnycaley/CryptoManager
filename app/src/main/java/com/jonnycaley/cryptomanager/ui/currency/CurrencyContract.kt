package com.jonnycaley.cryptomanager.ui.currency

import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView

interface CurrencyContract {
    interface View : BaseView<Presenter>
    interface Presenter : BasePresenter
}