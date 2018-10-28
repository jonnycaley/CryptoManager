package com.jonnycaley.cryptomanager.ui.fiat

import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView
import java.math.BigDecimal

interface FiatContract {
    interface View : BaseView<Presenter> {
        fun getFiatCode(): String
        fun showProgressBar()
        fun showTransactions(fiatSymbol : String, showTransactions: List<Transaction>)
        fun showAvailableFiat(fiatSymbol: String, availableFiatCount: BigDecimal)
        fun showDepositedFiat(fiatSymbol: String, depositedFiatCount: BigDecimal)
        fun showWithdrawnFiat(fiatSymbol: String, withdrawnFiatCount: BigDecimal)
    }

    interface Presenter : BasePresenter {
        fun getTransactions(fiatSymbol: String)
    }
}