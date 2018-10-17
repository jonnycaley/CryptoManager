package com.jonnycaley.cryptomanager.ui.settings.selectCurrency

import com.jonnycaley.cryptomanager.data.model.ExchangeRates.ExchangeRates
import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView

interface SelectCurrencyContract {
    interface View : BaseView<Presenter> {
        fun showFiats(fiats: ExchangeRates, baseFiat: String)
        fun onCurrencySaved()
    }

    interface Presenter : BasePresenter {
        fun saveBaseCurrency(symbol: String?)
    }
}