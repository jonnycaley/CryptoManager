package com.jonnycaley.cryptomanager.ui.settings.transactionHistory

import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView

interface TransactionHistoryContract {
    interface View : BaseView<Presenter> {
        fun showTransactions(transactions: ArrayList<Transaction>)
        fun showNoTransactionsLayout()
        fun hideNoTransactionsLayout()
    }

    interface Presenter : BasePresenter {
        fun getTransactions()
    }
}