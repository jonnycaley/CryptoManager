package com.jonnycaley.cryptomanager.ui.fiat

import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView

interface FiatContract {
    interface View : BaseView<Presenter> {
        fun getFiatCode(): String
        fun showProgressBar()
        fun showTransactions(fiatSymbol : String, showTransactions: List<Transaction>)
        fun showAvailableFiat(fiatSymbol: String, availableFiatCount: Long)
        fun showDepositedFiat(fiatSymbol: String, depositedFiatCount: Long)
        fun showWithdrawnFiat(fiatSymbol: String, withdrawnFiatCount: Long)
    }

    interface Presenter : BasePresenter {
        fun getTransactions(fiatSymbol: String)
    }
}