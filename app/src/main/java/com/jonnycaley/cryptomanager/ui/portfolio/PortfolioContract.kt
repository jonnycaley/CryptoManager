package com.jonnycaley.cryptomanager.ui.portfolio

import com.jonnycaley.cryptomanager.data.model.CryptoCompare.AllCurrencies.Currencies
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.MultiPrice.Price
import com.jonnycaley.cryptomanager.data.model.DataBase.Holding
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.Rate
import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView

interface PortfolioContract {
    interface View : BaseView<Presenter> {
        fun showRefreshing()
        fun stopRefreshing()
        fun showNoHoldingsLayout()
        fun showHoldingsLayout()
        fun showHoldings(holdings: ArrayList<Holding>, baseFiat : Rate, prices: ArrayList<Price>)
        fun showError()
        fun showBalance(balance: Double, baseFiat : Rate)
        fun showChange(change: Double, baseFiat : Rate)
        fun hideRefreshing()
    }

    interface Presenter : BasePresenter {
        fun getTransactions(timePeriod : String)
        fun clearDisposable()
    }
}