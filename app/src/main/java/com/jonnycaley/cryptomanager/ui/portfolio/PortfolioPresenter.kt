package com.jonnycaley.cryptomanager.ui.portfolio

import com.google.gson.Gson
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.MultiPrice.MultiPrices
import com.jonnycaley.cryptomanager.data.model.DataBase.Holding
import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import com.jonnycaley.cryptomanager.data.model.DataBase.Variables
import com.jonnycaley.cryptomanager.utils.JsonModifiers
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class PortfolioPresenter(var dataManager: PortfolioDataManager, var view: PortfolioContract.View) : PortfolioContract.Presenter{

    var compositeDisposable: CompositeDisposable? = null

    init {
        this.view.setPresenter(this)
    }

    override fun attachView() {
        if (compositeDisposable == null || (compositeDisposable as CompositeDisposable).isDisposed) {
            compositeDisposable = CompositeDisposable()
        }

    }

    override fun getTransactions() {

        dataManager.getTransactions()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<ArrayList<Transaction>> {

                    override fun onSuccess(transactions: ArrayList<Transaction>) {
                        view.stopRefreshing()

                        if (transactions.isEmpty())
                            view.showNoHoldingsLayout()
                        else {
                            getLatestPrices(combineTransactions(transactions))
                        }
                    }

                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable?.add(d)
                        view.showRefreshing()
                    }

                    override fun onError(e: Throwable) {
                        println("onError")
                        view.stopRefreshing()
                        view.showError()
                    }

                })
    }

    private fun getLatestPrices(combinedTransactions: ArrayList<Holding>) {

        if(dataManager.checkConnection()){

            var symbolsQueryString = ""

            combinedTransactions.forEach{symbolsQueryString += it.symbol + ","}

            symbolsQueryString = symbolsQueryString.substring(0, symbolsQueryString.length -1)

            dataManager.getCryptoCompareService().getMultiPrice(symbolsQueryString, "USD,BTC")
                    .map { json -> JsonModifiers.jsonToMultiPrices(json) }
                    .map { json -> Gson().fromJson(json, MultiPrices::class.java) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : SingleObserver<MultiPrices> {

                        override fun onSuccess(prices: MultiPrices) {
                            view.showHoldingsLayout()
                            view.showHoldings(ArrayList(combinedTransactions.sortedBy { transaction -> prices.prices?.filter { it.symbol?.toLowerCase() == transaction.symbol.toLowerCase() }?.get(0)?.prices?.uSD?.times(transaction.quantity) }.asReversed()), prices)
                            view.showBalance(getBalance(combinedTransactions, prices))
                            view.showChange(getChange(combinedTransactions,prices))
                        }

                        override fun onSubscribe(d: Disposable) {
                            compositeDisposable?.add(d)
                        }

                        override fun onError(e: Throwable) {
                            println("onError: ${e.message}")
                            view.stopRefreshing()
                            view.showError()
                        }

                    })

        } else {

        }

    }

    private fun getChange(combinedTransactions: ArrayList<Holding>, prices: MultiPrices): Double {

        var change = 0.toDouble()

        combinedTransactions.forEach { transaction ->

            val price = prices.prices?.filter { it.symbol?.toLowerCase() == transaction.symbol.toLowerCase() }?.get(0)?.prices?.uSD
            val value = price?.times(transaction.quantity)
            change += value?.minus(transaction.cost)!!

        }

        return change
    }

    private fun getBalance(combinedTransactions: ArrayList<Holding>, prices: MultiPrices): Double {

        var balance = 0.toDouble()

        combinedTransactions.forEach { transaction -> balance += prices.prices?.filter { it.symbol?.toLowerCase() == transaction.symbol.toLowerCase() }?.get(0)?.prices?.uSD?.times(transaction.quantity)!! }

        return balance

    }

    private fun combineTransactions(transactions: ArrayList<Transaction>): ArrayList<Holding> {

        val holdings =  ArrayList<Holding>()

        val transactionKeys = ArrayList<String>()

        transactions.forEach { println("Transaction(exchange = ${it.exchange}, symbol = ${it.symbol}, pairSymbol = ${it.pairSymbol}, quantity = ${it.quantity}, price = ${it.price}, priceUSD = ${it.priceUSD}, date = ${it.date}, notes = ${it.notes}, isDeducted = ${it.isDeducted}), isDeductedPrice = ${it.isDeductedPrice})") }

        transactions.forEach { if(!transactionKeys.contains(it.symbol)){ transactionKeys.add(it.symbol)} } //gets symbol for each symbol (fiat/symbol)

//        transactions.forEach {
//            println(it.symbol + "/" + it.pairSymbol )
//            println(it.price)
//            println(it.quantity)
//            println(it.isDeducted)
//            println("---------")
//        }

        transactionKeys.forEach { key ->

            var getCurrentHoldings = 0.toFloat()

            var getCost = 0.toDouble()

            val getAllTransactionsFor = transactions.filter { it.symbol == key }

            getAllTransactionsFor.forEach {
                getCurrentHoldings += it.quantity
                getCost += it.quantity * it.price * it.isDeductedPrice
            }

            val getAllTransactionsAgainst = transactions.filter { (it.pairSymbol == key) && (it.isDeducted) }

            getAllTransactionsAgainst.forEach {
                getCurrentHoldings -= (it.price * it.quantity)
                getCost -= it.price * it.quantity * it.isDeductedPrice
            }

            if(getAllTransactionsFor[0].pairSymbol == null)
                holdings.add(Holding(key, getCurrentHoldings, getCost, Variables.Transaction.Type.fiat))
            else
                holdings.add(Holding(key, getCurrentHoldings, getCost, Variables.Transaction.Type.crypto))
        }

        return holdings
    }

    override fun detachView() {
        compositeDisposable?.dispose()
    }

}