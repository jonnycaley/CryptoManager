package com.jonnycaley.cryptomanager.ui.crypto.viewpager.transactions

import com.jonnycaley.cryptomanager.data.model.CryptoCompare.AllCurrencies.Datum
import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.Rate
import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView

interface TransactionsContract {

    interface View : BaseView<Presenter> {
        fun getSymbol(): String
        fun loadTransactions(transactions: List<Transaction>, currentUsdPrice: Double?, baseFiat: Rate)
        fun startTransaction(currency: Datum?, baseImageUrl: String?, baseLinkUrl: String?)
        fun hideRefreshing()
    }

    interface Presenter : BasePresenter {
        fun getAllCurrencies()
        fun onResume()
    }
}