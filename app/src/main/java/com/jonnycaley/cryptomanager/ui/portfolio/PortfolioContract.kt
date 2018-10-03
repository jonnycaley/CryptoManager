package com.jonnycaley.cryptomanager.ui.portfolio

import com.jonnycaley.cryptomanager.data.model.CryptoCompare.MultiPrice.MultiPrices
import com.jonnycaley.cryptomanager.data.model.DataBase.Holding
import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView

interface PortfolioContract {
    interface View : BaseView<Presenter> {
        fun showRefreshing()
        fun stopRefreshing()
        fun showNoHoldingsLayout()
        fun showHoldingsLayout()
        fun showHoldings(holdings: ArrayList<Holding>, prices: MultiPrices)
        fun showError()
        fun showBalance(balance: Double)
        fun showChange(change: Double)
    }

    interface Presenter : BasePresenter {
        fun getTransactions()
    }
}