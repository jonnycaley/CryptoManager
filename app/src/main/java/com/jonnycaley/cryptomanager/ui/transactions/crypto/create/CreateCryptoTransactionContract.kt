package com.jonnycaley.cryptomanager.ui.transactions.crypto.create

import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView
import java.util.*

interface CreateCryptoTransactionContract {
    interface View : BaseView<Presenter> {
        fun getSymbol(): String
        fun onTransactionComplete()
    }

    interface Presenter : BasePresenter {
        fun getAllHoldings(symbol: String?): Long
        fun saveCryptoTransaction(isBuy: Boolean, exchange: String, pair: String, price: Float, quantity: Float, date: Date?, deductFromHoldings: Boolean, notes: String)
    }
}