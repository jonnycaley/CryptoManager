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

        transactions.forEach { if(!transactionKeys.contains(it.currency)){ transactionKeys.add(it.currency)} }

        transactionKeys.forEach { key ->
            val getAllTransactionsFor = transactions.filter { it.currency == key }
            var getTotalAmount = 0.toDouble()
            getAllTransactionsFor.filter { it.type == Variables.Transaction.FiatType.deposit }.forEach { getTotalAmount += it.quantity }
            getAllTransactionsFor.filter { it.type == Variables.Transaction.FiatType.widthdrawl }.forEach { getTotalAmount -= it.quantity }

            var getTotalCost = 0.toLong()

            getAllTransactionsFor.filter { it.type == Variables.Transaction.FiatType.deposit }.forEach { getTotalCost += (it.price * it.quantity) }
            getAllTransactionsFor.filter { it.type == Variables.Transaction.FiatType.widthdrawl }.forEach { getTotalCost -= (it.price * it.quantity) }

            if(getAllTransactionsFor[0].type == Variables.Transaction.FiatType.deposit || getAllTransactionsFor[0].type == Variables.Transaction.FiatType.widthdrawl)
                holdings.add(Holding(key, getTotalAmount, getTotalCost, Variables.Transaction.Type.fiat))
            else
                holdings.add(Holding(key, getTotalAmount, getTotalCost, Variables.Transaction.Type.crypto))
        }

        return holdings
    }

    override fun detachView() {
        compositeDisposable?.dispose()
    }

}