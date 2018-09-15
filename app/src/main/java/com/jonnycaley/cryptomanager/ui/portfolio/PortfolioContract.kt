package com.jonnycaley.cryptomanager.ui.portfolio

import com.jonnycaley.cryptomanager.data.model.DataBase.Holding
import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView

interface PortfolioContract {
    interface View : BaseView<Presenter> {
        fun showRefreshing()
        fun stopRefreshing()
        fun showNoTransactionsLayout()
        fun showTransactionsLayout()
        fun showTransactions(transactions: ArrayList<Holding>)
    }

    interface Presenter : BasePresenter
}