package com.jonnycaley.cryptomanager.ui.transactions.fiat.update

import com.google.gson.Gson
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.PriceAtTimestampForReal.Price
import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import com.jonnycaley.cryptomanager.utils.JsonModifiers
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

        var priceUsd = 1.toBigDecimal()

        var btcPrice = 1.toBigDecimal()
        var ethPrice = 1.toBigDecimal()

        if(dataManager.checkConnection()) {

            dataManager.getCryptoCompareServiceWithScalars().getPriceAtTimestamp("BTC", "USD", date?.time.toString().substring(0, date?.time.toString().length - 3))
                    .map {
                        json ->

                        val gson = Gson().fromJson(JsonModifiers.jsonToTimeStampPrice(json), Price::class.java)

                        if(gson.uSD != null)
                            btcPrice = gson.uSD!!

                        println("btcPrice PRICE: $btcPrice")
                    }
                    .flatMap { dataManager.getCryptoCompareServiceWithScalars().getPriceAtTimestamp("ETH", "USD", date?.time.toString().substring(0, date?.time.toString().length - 3)) }
                    .map { json ->

                        val gson = Gson().fromJson(JsonModifiers.jsonToTimeStampPrice(json), Price::class.java)

                        if(gson.uSD != null)
                            ethPrice = gson.uSD!!

                        println("ethPrice PRICE: $ethPrice")

                    }
                    .flatMap { dataManager.getCryptoCompareServiceWithScalars().getPriceAtTimestamp(currency, "USD", date?.time.toString().substring(0, date?.time.toString().length - 3)) }
                    .map { json ->
                        val gson = Gson().fromJson(JsonModifiers.jsonToTimeStampPrice(json), Price::class.java)

                        if(gson.uSD != null)
                            priceUsd = gson.uSD!!
                        println("priceUsd PRICE: $priceUsd ")


                    }
                    .flatMapSingle { dataManager.getTransactions() }
                    .map { transactions ->

                        val newTransaction = Transaction(exchange, currency, null, quantity.toBigDecimal(), priceUsd, priceUsd, date, notes, false, 1.toBigDecimal(), null, null, btcPrice, ethPrice)

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