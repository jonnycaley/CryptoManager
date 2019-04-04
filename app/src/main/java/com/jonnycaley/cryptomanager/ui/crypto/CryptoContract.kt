package com.jonnycaley.cryptomanager.ui.crypto

import com.jonnycaley.cryptomanager.data.model.CryptoCompare.GeneralInfo.GeneralInfo
import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView

interface CryptoContract {
    interface View : BaseView<Presenter> {
        fun getSymbol(): String
        fun loadTheme(info: GeneralInfo)
        fun setupViewPager()
        fun showNoInternet()
        fun connectionAvailable()
        fun showNoDataAvailable()
        fun onBackPressed()
    }

    interface Presenter : BasePresenter {
        fun checkInternet(): Boolean
        fun getCoinColorScheme()
    }
}