package com.jonnycaley.cryptomanager.ui.transactions.fiat

import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView

interface FiatTransactionContract {
    interface View : BaseView<Presenter>
    interface Presenter : BasePresenter {
        fun saveFiatTransaction(isDeposit: Boolean, exchange: CharSequence, currency: CharSequence, quantity: CharSequence, date: CharSequence, notes: CharSequence)
    }
}