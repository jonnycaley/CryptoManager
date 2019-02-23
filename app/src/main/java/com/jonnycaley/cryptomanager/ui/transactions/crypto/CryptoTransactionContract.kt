package com.jonnycaley.cryptomanager.ui.transactions.crypto

import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView
import java.math.BigDecimal
import java.util.*


interface CryptoTransactionContract {
    interface View : BaseView<Presenter> {
        fun getSymbol(): String
        fun onTransactionComplete()
        fun onTransactionDeleted()
        fun showCurrentPrice(price: String)
        fun showSellAllAmount(amount: BigDecimal)
    }

    interface Presenter : BasePresenter {
        fun getAllHoldings(symbol: String)
        fun updateCryptoTransaction(transaction: Transaction, isBuy: Boolean, exchange: String, pair: String, price: Float, quantity: Float, date: Date?, deductFromHoldings: Boolean, notes: String)
        fun deleteTransaction(transaction: Transaction)
        fun createCryptoTransaction(checked: Boolean, toString: String, toString1: String, parseFloat: Float, parseFloat1: Float, chosenDate: Date, checked1: Boolean, toString2: String)
        fun getCurrentPrice(transactionSymbol: String?, pair: String?)
    }
}