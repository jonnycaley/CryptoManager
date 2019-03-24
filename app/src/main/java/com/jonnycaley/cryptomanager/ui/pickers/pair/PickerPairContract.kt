package com.jonnycaley.cryptomanager.ui.pickers.pair

import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView

interface PickerPairContract {
    interface View : BaseView<Presenter> {
        fun getExchange(): String?
        fun getCryproSymbol(): String?
        fun showPairs(pairs: List<String>?)
        fun showProgressBar()
        fun hideProgressBar()
        fun onPairChosen(pair: String?)
        fun showError()
        fun showSearchbar()
    }

    interface Presenter : BasePresenter {
        fun getPairs(exchange: String?, crytpoSymbol: String?)
        fun filterPairs(trim: String)
    }
}