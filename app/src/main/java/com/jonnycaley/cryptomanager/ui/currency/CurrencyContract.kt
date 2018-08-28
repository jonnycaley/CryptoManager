package com.jonnycaley.cryptomanager.ui.currency

import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView


class CurrencyContract {
    interface View : BaseView<Presenter>
    interface Presenter : BasePresenter
}