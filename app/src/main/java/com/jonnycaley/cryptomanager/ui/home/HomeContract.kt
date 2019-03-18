package com.jonnycaley.cryptomanager.ui.home

import com.jonnycaley.cryptomanager.data.model.CryptoCompare.MultiPrice.Price
import com.jonnycaley.cryptomanager.data.model.DataBase.Holding
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.Rate
import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView
import java.math.BigDecimal

interface HomeContract {
    interface View : BaseView<Presenter> {
        fun showRefreshing()
        fun stopRefreshing()
        fun showNoHoldingsLayout()
        fun showHoldingsLayout()
        fun showHoldings(holdings: ArrayList<Holding>, prices: ArrayList<Price>, baseFiat: Rate, allFiats: ArrayList<Rate>)
        fun showError()
        fun showBalance(baseFiat: Rate, balanceUsd: BigDecimal, balanceBtc: BigDecimal, balanceEth: BigDecimal)
        fun showChange(baseFiat: Rate, balanceUsd: BigDecimal, balanceBtc: BigDecimal, balanceEth: BigDecimal, changeUsd: BigDecimal, changeBtc: BigDecimal, changeEth: BigDecimal)
        fun hideRefreshing()
        fun getToggledCurrency() : String
    }

    interface Presenter : BasePresenter {
        fun getTransactions(timePeriod : String)
        fun clearDisposable()
        fun updateView()
    }
}