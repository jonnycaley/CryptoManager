package com.jonnycaley.cryptomanager.ui.splash

import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView

interface SplashContract {
    interface View : BaseView<Presenter> {
        fun toBaseActivity()
        fun showInternetRequired()
    }

    interface Presenter : BasePresenter {
        fun getCurrencies()
    }
}