package com.jonnycaley.cryptomanager.ui.pickers.currency

import com.jonnycaley.cryptomanager.data.model.ExchangeRates.ExchangeRates
import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView

interface PickerCurrencyContract {
    interface View : BaseView<Presenter> {
        fun showFiats(fiats: ExchangeRates)
        fun onPickerChosen(symbol: String?)
    }

    interface Presenter : BasePresenter
}