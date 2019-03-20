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
        fun getName(): String
        fun showProgressBar()
        fun disableTouchEvents()
        fun hideProgressBar()
        fun enableTouchEvents()
        fun showError()
        fun showNoInternet()
    }

    interface Presenter : BasePresenter {
        fun getAllHoldings(symbol: String)
        fun updateCryptoTransaction(transaction: Transaction, isBuy: Boolean, exchange: String, pair: String, price: Float, quantity: Float, date: Date, deductFromHoldings: Boolean, notes: String)
        fun deleteTransaction(transaction: Transaction)
        fun createCryptoTransaction(isBuy: Boolean, exchange: String, pair: String, price: Float, quantity: Float, date: Date, isDeducted: Boolean, notes: String)
        fun getCurrentPrice(transactionSymbol: String, pair: String)
    }
}