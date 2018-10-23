package com.jonnycaley.cryptomanager.ui.transactions.fiat.create

import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import io.reactivex.*
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList

class CreateFiatTransactionPresenter(var dataManagerCreate: CreateFiatTransactionDataManager, var view: CreateFiatTransactionContract.View) : CreateFiatTransactionContract.Presenter {

    var compositeDisposable: CompositeDisposable? = null

    init {
        this.view.setPresenter(this)
    }

    override fun attachView() {
        if (compositeDisposable == null || (compositeDisposable as CompositeDisposable).isDisposed) {
            compositeDisposable = CompositeDisposable()
        }

    }

    override fun saveFiatTransaction(exchange: String, currency: String, quantity: Float, date: Date, notes: String) {

        var priceUsd = 1.toDouble()

        dataManagerCreate.getCryptoCompareService().getPriceAtMinute(currency, "USD", "1", "1", date.time.toString())
                .map { response ->
                    if (!response.data?.isEmpty()!!)
                        priceUsd = response.data?.get(1)?.close!!
                }
                .flatMapSingle { dataManagerCreate.getTransactions() }
                .observeOn(Schedulers.computation())
                .map { transactions ->

                    val newTransaction = Transaction(exchange, currency, null, quantity, priceUsd.toFloat(), priceUsd, date, notes, false, 1.toDouble(), null, null)

                    //TODO: check the "false, 1.toDouble()" above

                    transactions.add(newTransaction)
                    return@map transactions
                }
                .observeOn(Schedulers.io())
                .flatMapCompletable { transactions -> dataManagerCreate.saveTransactions(transactions) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {

                    override fun onComplete() {
                        view.onTransactionComplete()

                    }

                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable?.add(d)
                        view.showProgressBar()
                    }

                    override fun onError(e: Throwable) {
                        println("onError: ${e.message}")
                    }

                })

    }

    private fun saveTransactions(transactions: ArrayList<Transaction>) {
        if(dataManagerCreate.checkConnection()){

            dataManagerCreate.saveTransactions(transactions)
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

        } else {
            //TODO
        }
    }

    override fun detachView() {
        compositeDisposable?.dispose()
    }

    companion object {
        val TAG = "CreateFiatTransPres"
    }

}