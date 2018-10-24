package com.jonnycaley.cryptomanager.ui.transactions.fiat.update

import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import io.reactivex.CompletableObserver
import io.reactivex.Observer
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

        var btcPrice = 1.toDouble()
        var ethPrice = 1.toDouble()

        if(dataManager.checkConnection()) {

            dataManager.getCryptoCompareService().getPriceAtMinute("BTC", "USD", "1", "1", date?.time.toString())
                    .map { response ->
                        if (response.data?.isNotEmpty()!!)
                            btcPrice = response.data!!.last().close!!
                    }
                    .flatMap {
                        dataManager.getCryptoCompareService().getPriceAtMinute("ETH", "USD", "1", "1", date?.time.toString())
                    }
                    .map { response ->
                        if (response.data?.isNotEmpty()!!)
                            ethPrice = response.data!!.last().close!!
                    }
                    .flatMap {
                        dataManager.getCryptoCompareService().getPriceAtMinute(currency, "USD", "1", "1", date.time.toString())
                    }
                    .map { response ->
                        if (!response.data?.isEmpty()!!)
                            priceUsd = response.data?.get(1)?.close!!
                    }
                    .flatMapSingle { dataManager.getTransactions() }
                    .map { transactions ->

                        val newTransaction = Transaction(exchange, currency, null, quantity, priceUsd.toFloat(), priceUsd, date, notes, false, 1.toDouble(), null, null, btcPrice, ethPrice)

                        //TODO: check the "false, 1.toDouble()" above

                        transactions.remove(oldTransaction)
                        transactions.add(newTransaction)

                        return@map transactions
                    }
                    .flatMapCompletable { transactions -> dataManager.saveTransactions(transactions) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : CompletableObserver {

                        override fun onComplete() {
                            view.onTransactionUpdated()
                        }

                        override fun onSubscribe(d: Disposable) {
                            compositeDisposable?.add(d)
//                        view.showProgressBar()
                        }

                        override fun onError(e: Throwable) {
                            println("onError: ${e.message}")
                        }
                    })
        } else {

        }
    }


//    private fun saveTransactions(transactions: ArrayList<Transaction>) {
//        if(dataManager.checkConnection()){
//
//            dataManager.saveTransactions(transactions)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(object : CompletableObserver {
//                        override fun onComplete() {
//                            view.onTransactionUpdated()
//                        }
//
//                        override fun onSubscribe(d: Disposable) {
//                            compositeDisposable?.add(d)
//                        }
//
//                        override fun onError(e: Throwable) {
//                            println("onError: ${e.message}")
//                        }
//
//                    })
//
//        } else {
//            //TODO
//        }
//    }

    override fun detachView() {
        compositeDisposable?.dispose()
    }

    companion object {
        val TAG = "UpdateCryptoTransPres"
    }

}