package com.jonnycaley.cryptomanager.ui.splash

import android.content.Context
import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView

interface SplashContract {
    interface View : BaseView<Presenter> {
        fun toBaseActivity()
        fun showInternetRequired()
        fun showUsingStorage()
        fun setDarkTheme()
        fun hideProgressBar()
    }

    interface Presenter : BasePresenter {
        fun getCurrencies()
    }
}