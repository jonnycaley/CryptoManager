package com.jonnycaley.cryptomanager.ui.pickers.currency

import com.jonnycaley.cryptomanager.data.model.ExchangeRates.ExchangeRates
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.Rate
import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView

interface PickerCurrencyContract {
    interface View : BaseView<Presenter> {
        fun showFiats(fiats: List<Rate>?)
        fun onPickerChosen(symbol: String?)
        fun hideNoInternetLayout()
        fun showNoInternetLayout()
        fun showError()
        fun hideProgressBar()
        fun showProgressBar()
        fun showSearchBar()
    }

    interface Presenter : BasePresenter {
        fun getFiats()
        fun filterFiats(trim: String)
    }
}