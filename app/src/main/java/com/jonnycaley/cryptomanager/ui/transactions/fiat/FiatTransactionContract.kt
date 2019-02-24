package com.jonnycaley.cryptomanager.ui.transactions.fiat

import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView
import java.util.*


interface FiatTransactionContract {
    interface View : BaseView<Presenter> {
        fun onTransactionUpdated()
        fun onTransactionCreated()
    }

    interface Presenter : BasePresenter {
        fun updateFiatTransaction(oldTransaction : Transaction, exchange: String, currency: String, quantity: Float, chosenDate: Date, notes: String)
        fun deleteTransaction(transaction: Transaction)
        fun createFiatTransaction(toString: String, toString1: String, correctQuantity: Float, chosenDate: Date, toString2: String)
    }
}