package com.jonnycaley.cryptomanager.ui.currency.viewpager.general

import com.jonnycaley.cryptomanager.data.model.CryptoCompare.HistoricalData.Data
import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView

interface GeneralContract {

    interface View : BaseView<Presenter> {
        fun getSymbol(): String
        fun showCandlestickChart(response: Data)
    }

    interface Presenter : BasePresenter
}