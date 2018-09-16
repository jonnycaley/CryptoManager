package com.jonnycaley.cryptomanager.ui.portfolio

import com.jonnycaley.cryptomanager.data.model.DataBase.Holding
import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import com.jonnycaley.cryptomanager.data.model.DataBase.Variables
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

//        getTransactions()
    }

    override fun getTransactions() {
        dataManager.getTransactions()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<ArrayList<Transaction>> {

                    override fun onSuccess(transactions: ArrayList<Transaction>) {
                        view.stopRefreshing()

                        if (transactions.isEmpty())
                            view.showNoTransactionsLayout()
                        else {
                            view.showTransactionsLayout()
                            view.showTransactions(combineTransactions(transactions))
                        }
                    }

                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable?.add(d)
                        view.showRefreshing()
                    }

                    override fun onError(e: Throwable) {
                        println("onError")
                    }

                })
    }

    private fun combineTransactions(transactions: ArrayList<Transaction>): ArrayList<Holding> {

        val holdings =  ArrayList<Holding>()

        val transactionKeys = ArrayList<String>()

        transactions.forEach { if(!transactionKeys.contains(it.symbol)){ transactionKeys.add(it.symbol)} } //gets symbol for each currency (fiat/currency)

//        transactions.forEach {
//            println(it.symbol + "/" + it.pairSymbol )
//            println(it.price)
//            println(it.quantity)
//            println(it.isDeducted)
//            println("---------")
//        }

        transactionKeys.forEach { key ->

            var getCurrentHoldings = 0.toFloat()

            val getAllTransactionsFor = transactions.filter { it.symbol == key }

            getAllTransactionsFor.forEach { getCurrentHoldings += it.quantity }

            val getAllTransactionsAgainst = transactions.filter { (it.pairSymbol == key) && (it.isDeducted == true) }

            getAllTransactionsAgainst.forEach { getCurrentHoldings -= (it.price * it.quantity) }

            if(getAllTransactionsFor[0].pairSymbol == null)
                holdings.add(Holding(key, getCurrentHoldings, Variables.Transaction.Type.fiat))
            else
                holdings.add(Holding(key, getCurrentHoldings, Variables.Transaction.Type.crypto))
        }

        return holdings
    }

    override fun detachView() {
        compositeDisposable?.dispose()
    }

}