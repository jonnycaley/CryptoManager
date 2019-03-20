package com.jonnycaley.cryptomanager.ui.transactions.fiat

import com.google.gson.Gson
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.PriceAtTimestampForReal.Price
import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import com.jonnycaley.cryptomanager.utils.JsonModifiers
import com.jonnycaley.cryptomanager.utils.Utils
import io.reactivex.CompletableObserver
import io.reactivex.Observable
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function4
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList

class FiatTransactionPresenter(var dataManager: FiatTransactionDataManager, var view: FiatTransactionContract.View) : FiatTransactionContract.Presenter {

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

        if (dataManager.checkConnection()) {

            val getBtcPrice: Observable<String> = dataManager.getCryptoCompareServiceWithScalars().getPriceAtTimestamp("BTC", "USD", date.time.toString().substring(0, date.time.toString().length - 3))
            val getEthPrice: Observable<String> = dataManager.getCryptoCompareServiceWithScalars().getPriceAtTimestamp("ETH", "USD", date.time.toString().substring(0, date.time.toString().length - 3))
            val getIsDeductedPrice: Observable<String> = dataManager.getCryptoCompareServiceWithScalars().getPriceAtTimestamp(currency, "USD", date.time.toString().substring(0, date.time.toString().length - 3))

            val getTransactions: Observable<ArrayList<Transaction>> = dataManager.getTransactions().toObservable()

            Observable.zip(getBtcPrice, getEthPrice, getIsDeductedPrice, getTransactions, Function4<String, String, String, ArrayList<Transaction>, ArrayList<Transaction>> { res1, res2, res3, transactions ->

                val gson1 = Gson().fromJson(JsonModifiers.jsonToTimeStampPrice(res1), Price::class.java)
                gson1.uSD?.let { btcPrice = it }

                val gson2 = Gson().fromJson(JsonModifiers.jsonToTimeStampPrice(res2), Price::class.java)
                gson2.uSD?.let { ethPrice = it }

                val gson3 = Gson().fromJson(JsonModifiers.jsonToTimeStampPrice(res3), Price::class.java)
                gson3.uSD?.let { priceUsd = it }

                val newTransaction = Transaction(exchange, currency, Utils.getFiatName(currency), null, quantity.toBigDecimal(), priceUsd, priceUsd, date, notes, false, 1.toBigDecimal(), null, null, btcPrice, ethPrice)
                transactions.remove(oldTransaction)
                transactions.add(newTransaction)

                transactions
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .flatMapCompletable { transactions -> dataManager.saveTransactions(transactions) }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : CompletableObserver {

                        override fun onComplete() {

                            view.onTransactionUpdated()
                        }

                        override fun onSubscribe(d: Disposable) {
                            view.showProgressBar()
                            view.disableTouchEvents()
                            compositeDisposable?.add(d)
//                        view.showProgressBar()
                        }

                        override fun onError(e: Throwable) {
                            view.hideProgressBar()
                            view.enableTouchEvents()
                            view.showError()
                            println("onError: ${e.message}")
                        }
                    })


//            dataManager.getCryptoCompareServiceWithScalars().getPriceAtTimestamp("BTC", "USD", date.time.toString().substring(0, date.time.toString().length - 3))
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(Schedulers.computation())
//                    .map {
//                        json ->
//
//                        val gson = Gson().fromJson(JsonModifiers.jsonToTimeStampPrice(json), Price::class.java)
//
//                        gson.uSD?.let { btcPrice = it }
//
//                        println("btcPrice PRICE: $btcPrice")
//                    }
//                    .observeOn(Schedulers.io())
//                    .flatMap { dataManager.getCryptoCompareServiceWithScalars().getPriceAtTimestamp("ETH", "USD", date.time.toString().substring(0, date.time.toString().length - 3)) }
//                    .observeOn(Schedulers.computation())
//                    .map { json ->
//
//                        val gson = Gson().fromJson(JsonModifiers.jsonToTimeStampPrice(json), Price::class.java)
//
//                        if(gson.uSD != null)
//                            ethPrice = gson.uSD!!
//
//                        println("ethPrice PRICE: $ethPrice")
//
//                    }
//                    .observeOn(Schedulers.io())
//                    .flatMap { dataManager.getCryptoCompareServiceWithScalars().getPriceAtTimestamp(currency, "USD", date.time.toString().substring(0, date.time.toString().length - 3)) }
//                    .observeOn(Schedulers.computation())
//                    .map { json ->
//                        val gson = Gson().fromJson(JsonModifiers.jsonToTimeStampPrice(json), Price::class.java)
//
//                        if(gson.uSD != null)
//                            priceUsd = gson.uSD!!
//                        println("priceUsd PRICE: $priceUsd ")
//
//
//                    }
//                    .observeOn(Schedulers.io())
//                    .flatMapSingle { dataManager.getTransactions() }
//                    .observeOn(Schedulers.computation())
//                    .map { transactions ->
//
//                        val newTransaction = Transaction(exchange, currency, Utils.getFiatName(currency), null, quantity.toBigDecimal(), priceUsd, priceUsd, date, notes, false, 1.toBigDecimal(), null, null, btcPrice, ethPrice)
//
//                        //TODO: check the "false, 1.toDouble()" above
//
//                        transactions.remove(oldTransaction)
//                        transactions.add(newTransaction)
//
//                        return@map transactions
//                    }
//                    .observeOn(Schedulers.io())
//                    .flatMapCompletable { transactions -> dataManager.saveTransactions(transactions) }
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(object : CompletableObserver {
//
//                        override fun onComplete() {
//                            view.onTransactionUpdated()
//                        }
//
//                        override fun onSubscribe(d: Disposable) {
//                            compositeDisposable?.add(d)
////                        view.showProgressBar()
//                        }
//
//                        override fun onError(e: Throwable) {
//                            println("onError: ${e.message}")
//                        }
//                    })
        } else {
            view.showNoInternet()
        }
    }


    override fun deleteTransaction(transaction: Transaction) {

        dataManager.getTransactions()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<ArrayList<Transaction>> {
                    override fun onSuccess(transactions: ArrayList<Transaction>) {
                        saveTransactions(ArrayList(transactions.filter { it != transaction }))
                    }

                    override fun onSubscribe(d: Disposable) {
                        view.disableTouchEvents()
                        compositeDisposable?.add(d)
                    }

                    override fun onError(e: Throwable) {
                        println("onError: ${e.message}")
                        view.disableTouchEvents()
                        view.showError()
                    }
                })
    }


    override fun createFiatTransaction(exchange: String, currency: String, quantity: Float, date: Date, notes: String) {

        var priceUsd = 1.toBigDecimal()

        var btcPrice = 1.toBigDecimal()
        var ethPrice = 1.toBigDecimal()

        if (dataManager.checkConnection()) {

            val getBtcPrice: Observable<String> = dataManager.getCryptoCompareServiceWithScalars().getPriceAtTimestamp("BTC", "USD", date.time.toString().substring(0, date.time.toString().length - 3))
            val getEthPrice: Observable<String> = dataManager.getCryptoCompareServiceWithScalars().getPriceAtTimestamp("ETH", "USD", date.time.toString().substring(0, date.time.toString().length - 3))
            val getIsDeductedPrice: Observable<String> = dataManager.getCryptoCompareServiceWithScalars().getPriceAtTimestamp(currency, "USD", date.time.toString().substring(0, date.time.toString().length - 3))

            val getTransactions: Observable<ArrayList<Transaction>> = dataManager.getTransactions().toObservable()


            Observable.zip(getBtcPrice, getEthPrice, getIsDeductedPrice, getTransactions, Function4<String, String, String, ArrayList<Transaction>, ArrayList<Transaction>> { res1, res2, res3, transactions ->

                val gson1 = Gson().fromJson(JsonModifiers.jsonToTimeStampPrice(res1), Price::class.java)
                gson1.uSD?.let { btcPrice = it }

                val gson2 = Gson().fromJson(JsonModifiers.jsonToTimeStampPrice(res2), Price::class.java)
                gson2.uSD?.let { ethPrice = it }

                val gson3 = Gson().fromJson(JsonModifiers.jsonToTimeStampPrice(res3), Price::class.java)
                gson3.uSD?.let { priceUsd = it }

                val newTransaction = Transaction(exchange, currency, Utils.getFiatName(currency), null, quantity.toBigDecimal(), priceUsd, priceUsd, date, notes, false, 1.toBigDecimal(), null, null, btcPrice, ethPrice)

                transactions.add(newTransaction)
                transactions
            })
                    .subscribeOn(Schedulers.io())
                    .flatMapCompletable { transactions -> dataManager.saveTransactions(transactions) }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : CompletableObserver {

                        override fun onComplete() {
                            view.onTransactionCreated()
                        }

                        override fun onSubscribe(d: Disposable) {
                            view.showProgressBar()
                            view.disableTouchEvents()
                            compositeDisposable?.add(d)
//                        view.showProgressBar()
                        }

                        override fun onError(e: Throwable) {
                            view.hideProgressBar()
                            view.enableTouchEvents()
                            view.showError()
                            println("onError: ${e.message}")
                        }
                    })

//            dataManager.getCryptoCompareServiceWithScalars().getPriceAtTimestamp("BTC", "USD", date.time.toString().substring(0, date.time.toString().length - 3))
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(Schedulers.computation())
//                    .map { json ->
//
//                        val gson = Gson().fromJson(JsonModifiers.jsonToTimeStampPrice(json), Price::class.java)
//
//                        if (gson.uSD != null)
//                            btcPrice = gson.uSD!!
//
//                        println("btcPrice PRICE: $btcPrice")
//                    }
//                    .observeOn(Schedulers.io())
//                    .flatMap { dataManager.getCryptoCompareServiceWithScalars().getPriceAtTimestamp("ETH", "USD", date.time.toString().substring(0, date.time.toString().length - 3)) }
//                    .observeOn(Schedulers.computation())
//                    .map { json ->
//
//                        val gson = Gson().fromJson(JsonModifiers.jsonToTimeStampPrice(json), Price::class.java)
//
//                        if (gson.uSD != null)
//                            ethPrice = gson.uSD!!
//
//                        println("ethPrice PRICE: $ethPrice")
//                    }
//                    .observeOn(Schedulers.io())
//                    .flatMap { dataManager.getCryptoCompareServiceWithScalars().getPriceAtTimestamp(currency, "USD", date.time.toString().substring(0, date.time.toString().length - 3)) }
//                    .observeOn(Schedulers.computation())
//                    .map { json ->
//                        val gson = Gson().fromJson(JsonModifiers.jsonToTimeStampPrice(json), Price::class.java)
//
//                        if (gson.uSD != null)
//                            priceUsd = gson.uSD!!
//                        println("priceUsd PRICE: $priceUsd ")
//
//
//                    }
//                    .observeOn(Schedulers.io())
//                    .flatMapSingle { dataManager.getTransactions() }
//                    .observeOn(Schedulers.computation())
//                    .map { transactions ->
//
//                        val newTransaction = Transaction(exchange, currency, Utils.getFiatName(currency), null, quantity.toBigDecimal(), priceUsd, priceUsd, date, notes, false, 1.toBigDecimal(), null, null, btcPrice, ethPrice)
//
//                        //TODO: check the "false, 1.toDouble()" above
//
//                        transactions.add(newTransaction)
//                        return@map transactions
//                    }
//                    .observeOn(Schedulers.io())
//                    .flatMapCompletable { transactions -> dataManager.saveTransactions(transactions) }
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(object : CompletableObserver {
//
//                        override fun onComplete() {
//                            view.onTransactionCreated()
//                        }
//
//                        override fun onSubscribe(d: Disposable) {
//                            compositeDisposable?.add(d)
////                            view.showProgressBar()
//                        }
//
//                        override fun onError(e: Throwable) {
//                            println("onError: ${e.message}")
//                        }
//
//                    })
        } else {
            view.showNoInternet()
        }
    }

    private fun saveTransactions(transactions: ArrayList<Transaction>) {
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
                            view.hideProgressBar()
                            view.enableTouchEvents()
                            view.showError()
                            println("onError: ${e.message}")
                        }

                    })

    }

    override fun detachView() {
        compositeDisposable?.dispose()
    }

    companion object {
        val TAG = "UpdateCryptoTransPres"
    }

}