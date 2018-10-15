package com.jonnycaley.cryptomanager.ui.transactions.fiat.update

import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import io.reactivex.CompletableObserver
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList

class UpdateFiatTransactionPresenter(var dataManager: UpdateFiatTransactionDataManager, var view: UpdateFiatTransactionContract.View) : UpdateFiatTransactionContract.Presenter {

    var compositeDisposable: CompositeDisposable? = null

    var otherTransactions = ArrayList<Transaction>()

    init {
        this.view.setPresenter(this)
    }

    override fun attachView() {
        if (compositeDisposable == null || (compositeDisposable as CompositeDisposable).isDisposed) {
            compositeDisposable = CompositeDisposable()
        }
    }

    override fun updateFiatTransaction(oldTransaction: Transaction, exchange: String, currency: String, quantity: Float, date: Date, notes: String) {

        var priceUsd = 1.toDouble()

        dataManager.getCryptoCompareService().getPriceAtMinute(currency, "USD", "1", "1", date.time.toString())
                .map { response ->
                    if (!response.data?.isEmpty()!!)
                        priceUsd = response.data?.get(1)?.close!!
                }
                .flatMap { dataManager.getTransactions() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<ArrayList<Transaction>> {
                    override fun onSuccess(transactions: ArrayList<Transaction>) {

                        val newTransaction = Transaction(exchange, currency, null, quantity, priceUsd.toFloat(), priceUsd, date, notes, false, 1.toDouble(), null, null)

                        //TODO: check the "false, 1.toDouble()" above

                        otherTransactions = (transactions.filter { it != oldTransaction } as ArrayList<Transaction>)
                        otherTransactions.add(newTransaction)

                        saveTransactions(otherTransactions)
                    }

                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable?.add(d)
//                        view.showProgressBar()
                    }

                    override fun onError(e: Throwable) {
                        println("onError: ${e.message}")
                    }

                })
    }


    private fun saveTransactions(transactions: ArrayList<Transaction>) {
        if(dataManager.checkConnection()){

            dataManager.saveTransactions(transactions)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : CompletableObserver {
                        override fun onComplete() {
                            view.onTransactionUpdated()
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
        val TAG = "UpdateCryptoTransactionPres"
    }

}