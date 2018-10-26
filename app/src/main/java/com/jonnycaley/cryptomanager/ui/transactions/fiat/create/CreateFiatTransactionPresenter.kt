package com.jonnycaley.cryptomanager.ui.transactions.fiat.create

import com.google.gson.Gson
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.PriceAtTimestampForReal.Price
import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import com.jonnycaley.cryptomanager.utils.JsonModifiers
import io.reactivex.*
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList

class CreateFiatTransactionPresenter(var dataManager: CreateFiatTransactionDataManager, var view: CreateFiatTransactionContract.View) : CreateFiatTransactionContract.Presenter {

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

        var btcPrice = 1.toDouble()
        var ethPrice = 1.toDouble()

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
                    .observeOn(Schedulers.computation())
                    .map { transactions ->

                        val newTransaction = Transaction(exchange, currency, null, quantity, priceUsd.toFloat(), priceUsd, date, notes, false, 1.toDouble(), null, null, ethPrice, btcPrice)

                        //TODO: check the "false, 1.toDouble()" above

                        transactions.add(newTransaction)
                        return@map transactions
                    }
                    .observeOn(Schedulers.io())
                    .flatMapCompletable { transactions -> dataManager.saveTransactions(transactions) }
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
        } else {

        }

    }

    private fun saveTransactions(transactions: ArrayList<Transaction>) {
        if(dataManager.checkConnection()){

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