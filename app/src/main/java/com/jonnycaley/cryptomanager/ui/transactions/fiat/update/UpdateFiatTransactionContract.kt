package com.jonnycaley.cryptomanager.ui.transactions.fiat.update

import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView
import java.util.*


interface UpdateFiatTransactionContract {
    interface View : BaseView<Presenter> {
        fun getTransaction(): Transaction
        fun onTransactionUpdated()
    }

    interface Presenter : BasePresenter {
        fun updateFiatTransaction(oldTransaction : Transaction, exchange: String, currency: String, quantity: Float, chosenDate: Date, notes: String)
    }
}