package com.jonnycaley.cryptomanager.ui.transactions.fiat.create

import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView
import java.util.*

interface CreateFiatTransactionContract {
    interface View : BaseView<Presenter> {
        fun showProgressBar()
        fun onTransactionComplete()
    }

    interface Presenter : BasePresenter {
        fun saveFiatTransaction(exchange: String, currency: String, quantity: Float, date: Date, notes: String)
    }
}