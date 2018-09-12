package com.jonnycaley.cryptomanager.ui.pickers.exchange

import com.jonnycaley.cryptomanager.data.model.CryptoCompare.Exchanges.Exchanges
import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView

interface PickerExchangeContract {
    interface View : BaseView<Presenter> {
        fun showExchanges(exchanges: Exchanges?)
        fun onExchangeChosen(name: String?)
    }

    interface Presenter : BasePresenter
}