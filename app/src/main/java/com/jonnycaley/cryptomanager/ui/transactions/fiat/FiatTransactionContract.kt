package com.jonnycaley.cryptomanager.ui.transactions.fiat

import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView
import java.util.*

interface FiatTransactionContract {
    interface View : BaseView<Presenter> {
        fun showProgressBar()
        fun onTransactionComplete()
    }

    interface Presenter : BasePresenter {
        fun saveFiatTransaction(isDeposit: Boolean, exchange: String, currency: String, quantity: Double, date: Date, notes: String)
    }
}