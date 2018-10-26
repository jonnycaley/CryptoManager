package com.jonnycaley.cryptomanager.ui.portfolio

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
        fun showHoldings()
        fun showError()
        fun showBalance()
        fun showChange()
        fun hideRefreshing()
        fun getToggledCurrency() : String
        fun saveData(holdingsSorted: ArrayList<Holding>, newPrices: ArrayList<Price>, baseFiat: Rate, currenteBtcPrice: Price, currentEthPrice: Price, balance : Double, changeUsd : Double, historicalBtcPrice : Double, historicalEthPrice : Double)
    }

    interface Presenter : BasePresenter {
        fun getTransactions(timePeriod : String)
        fun clearDisposable()
    }
}