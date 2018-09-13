package com.jonnycaley.cryptomanager.ui.transactions.fiat

import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import com.jonnycaley.cryptomanager.data.model.DataBase.Variables
import com.pacoworks.rxpaper2.RxPaperBook
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList

class FiatTransactionPresenter(var dataManager: FiatTransactionDataManager, var view: FiatTransactionContract.View) : FiatTransactionContract.Presenter {

    var compositeDisposable: CompositeDisposable? = null

    init {
        this.view.setPresenter(this)
    }

    override fun attachView() {
        if (compositeDisposable == null || (compositeDisposable as CompositeDisposable).isDisposed) {
            compositeDisposable = CompositeDisposable()
        }

    }

    override fun saveFiatTransaction(isDeposit: Boolean, exchange: String, currency: String, quantity: Double, date: Date, notes: String) {

        println("SavingTransaction")

        val depositType = if (isDeposit) {
            Variables.Transaction.Type.widthdrawl
        } else {
            Variables.Transaction.Type.deposit
        }

        dataManager.getTransactions()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<ArrayList<Transaction>> {
                    override fun onSuccess(transactions: ArrayList<Transaction>) {

                        dataManager.saveTransactions(transactions)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(object : CompletableObserver {
                                    override fun onComplete() {
                                        view.onTransactionComplete()
                                    }

                                    override fun onSubscribe(d: Disposable) {
                                        compositeDisposable?.add(d)
                                    }

                                    override fun onError(e: Throwable) {
                                        println("onError: ${e.message}")
                                    }

                                })
                    }

                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable?.add(d)
                        view.showProgressBar()
                    }

                    override fun onError(e: Throwable) {
                        println("onError")
                    }

                })

        println("gotTransactions")
//                    var id = 0
//
//                    if (transactions.isNotEmpty())
//                        id = getId(transactions)
//
//                    val transaction = Transaction(id, depositType, exchange, currency, quantity, date, notes)
//
//                    transactions.add(transaction)
//
//                    dataManager.saveTransactions(transactions)
//                            .doOnSubscribe { view.showProgressBar() }
//                            .doOnError { e -> println("onError: ${e.message}") }
//                            .doOnComplete {
//                                println("onTransactionComplete")
//                                view.onTransactionComplete()
//                            }

//        Single.create<ArrayList<Transaction>> { dataManager.getTransactions() }
//                .map { transactions: ArrayList<Transaction> ->
//                    var id = 0
//
//                    if(transactions.isNotEmpty())
//                        id = getId(transactions)
//
//                    val transaction = Transaction(id, depositType, exchange, currency, quantity, date, notes)
//
//                    transactions.add(transaction)
//
//                    dataManager.saveTransactions(transactions)
//                }
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .doOnSubscribe { view.showProgressBar() }
//                .doOnError { e -> println("onError: ${e.message}") }
//                .doOnSuccess { result ->
//                    println("onSuccess")
//                    view.onTransactionComplete()
//                }

//        Single.just(dataManager.getTransactions())
//                .map { transactions: ArrayList<Transaction> ->
//                    var id = 0
//
//                    if(transactions.isNotEmpty())
//                        id = getId(transactions)
//
//                    val transaction = Transaction(id, depositType, exchange, currency, quantity, date, notes)
//
//                    transactions.add(transaction)
//
//                    dataManager.saveTransactions(transactions)
//                }
//                .doOnSubscribe { view.showProgressBar() }
//                .doOnError { e -> println("onError: ${e.message}") }
//                .doOnSuccess { result ->
//                    println("onSuccess")
//                    view.onTransactionComplete()
//                }

    }

    private fun getId(transactions: ArrayList<Transaction>): Int {
        val transactionsSorted = transactions.sortedBy { it.id }.asReversed()

        return transactionsSorted[0].id + 1
    }

    override fun detachView() {
        compositeDisposable?.dispose()
    }

    companion object {
        val TAG = "FiatTransactionPresenter"
    }

}