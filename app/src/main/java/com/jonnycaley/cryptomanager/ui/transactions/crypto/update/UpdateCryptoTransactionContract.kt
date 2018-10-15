package com.jonnycaley.cryptomanager.ui.transactions.crypto.update

import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView
import java.util.*


interface UpdateCryptoTransactionContract {
    interface View : BaseView<Presenter> {
        fun getSymbol(): String
        fun onTransactionComplete()
        fun onTransactionDeleted()
    }

    interface Presenter : BasePresenter {
        fun getAllHoldings(symbol: String?): Long
        fun updateCryptoTransaction(transaction: Transaction, isBuy: Boolean, exchange: String, pair: String, price: Float, quantity: Float, date: Date?, deductFromHoldings: Boolean, notes: String)
        fun deleteTransaction(transaction: Transaction)
    }
}