package com.jonnycaley.cryptomanager.ui.transactions.crypto.update

import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.AllCurrencies.Currencies
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

class UpdateCryptoTransactionPresenter(var dataManager: UpdateCryptoTransactionDataManager, var view: UpdateCryptoTransactionContract.View) : UpdateCryptoTransactionContract.Presenter {

    var compositeDisposable: CompositeDisposable? = null

    init {
        this.view.setPresenter(this)
    }

    override fun attachView() {
        if (compositeDisposable == null || (compositeDisposable as CompositeDisposable).isDisposed) {
            compositeDisposable = CompositeDisposable()
        }
    }

    override fun detachView() {
        compositeDisposable?.dispose()
    }

    override fun updateCryptoTransaction(originalTransaction: Transaction, isBuy: Boolean, exchange: String, pair: String, price: Float, quantity: Float, date: Date?, isDeducted: Boolean, notes: String) {

        var correctQuantity = quantity.toBigDecimal()
        var priceUsd = 1.toBigDecimal()
        var isDeductedPriceUsd = 1.toBigDecimal()
        var allCryptos: Currencies? = null
        var btcPrice = 1.toBigDecimal()
        var ethPrice = 1.toBigDecimal()

        if (!isBuy)
            correctQuantity *= (-1).toBigDecimal()

        if (dataManager.checkConnection()) {

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
                    .flatMap { dataManager.getCryptoCompareServiceWithScalars().getPriceAtTimestamp(pair, "USD", date?.time.toString().substring(0, date?.time.toString().length - 3)) }
                    .map { json ->
                        val gson = Gson().fromJson(JsonModifiers.jsonToTimeStampPrice(json), Price::class.java)

                        if(gson.uSD != null)
                            isDeductedPriceUsd = gson.uSD!!

                        println("isDeductedPriceUsd PRICE: $isDeductedPriceUsd")

                    }
                    .flatMap { dataManager.getCryptoCompareServiceWithScalars().getPriceAtTimestamp(view.getSymbol(), "USD", date?.time.toString().substring(0, date?.time.toString().length - 3)) }
                    .map { json ->
                        val gson = Gson().fromJson(JsonModifiers.jsonToTimeStampPrice(json), Price::class.java)

                        if(gson.uSD != null)
                            priceUsd = gson.uSD!!
                        println("priceUsd PRICE: $priceUsd ")


                    }
                    .flatMapSingle { dataManager.getAllCryptos() }
                    .map { cryptos -> allCryptos = cryptos }
                    .flatMapSingle { dataManager.getTransactions() }
                    .observeOn(Schedulers.computation())
                    .map { transactions ->

                        val newTransaction = Transaction(exchange, view.getSymbol(), pair, correctQuantity, price.toBigDecimal(), priceUsd, date!!, notes, isDeducted, isDeductedPriceUsd, allCryptos!!.baseImageUrl + allCryptos!!.data?.firstOrNull { it.symbol == view.getSymbol() }?.imageUrl, allCryptos!!.baseImageUrl + allCryptos!!.data?.firstOrNull { it.symbol == pair }?.imageUrl, btcPrice, ethPrice)
                        transactions.remove(originalTransaction)
                        transactions.add(newTransaction)
                        return@map transactions
                    }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<java.util.ArrayList<Transaction>> {

                        override fun onComplete() {

                        }

                        override fun onNext(transactions: java.util.ArrayList<Transaction>) {
                            saveTransactions(transactions)
                        }

                        override fun onSubscribe(d: Disposable) {
                            compositeDisposable?.add(d)
                        }

                        override fun onError(e: Throwable) {
                            println("onError: ${e.message}")
                        }
                    })
        } else {
//                view.showNoInternet()
        }
    }


//    private fun updateTransaction(originalTransaction: Transaction, isBuy: Boolean, exchange: String, pair: String, price: Float, quantity: Float, date: Date?, notes: String, isDeductedPriceUsd: Double?) {
//
//        var correctQuantity = quantity
//        if (!isBuy)
//            correctQuantity *= -1
//
//        var priceUsd = 1.toDouble()
//
//        dataManager.getCryptoCompareServiceWithScalars().getPriceAtMinute(view.getSymbol(), "USD", "1", "1", date?.time.toString())
//                .map { response ->
//                    if (!response.data?.isEmpty()!!)
//                        priceUsd = response.data?.get(1)?.close!!
//                }
//                .flatMap { dataManager.getHoldings() }
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(object : SingleObserver<ArrayList<Transaction>> {
//                    override fun onSuccess(holdings: ArrayList<Transaction>) {
//
//                        val newTransaction = Transaction(exchange, view.getSymbol(), pair, correctQuantity, price, priceUsd, date!!, notes, isDeductedPriceUsd)
//                        holdings.remove(originalTransaction)
//                        holdings.add(newTransaction)
//
//                        saveTransactions(holdings)
//                    }
//
//                    override fun onSubscribe(d: Disposable) {
//                        compositeDisposable?.add(d)
//                    }
//
//                    override fun onError(e: Throwable) {
//                        println("onError: ${e.message}")
//                    }
//
//                })
//
//    }

    override fun deleteTransaction(transaction: Transaction) {

        dataManager.getTransactions()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<ArrayList<Transaction>> {
                    override fun onSuccess(transactions: ArrayList<Transaction>) {
                        saveTransactions(ArrayList(transactions.filter { it != transaction }))
                    }

                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable?.add(d)
                    }

                    override fun onError(e: Throwable) {
                        println("onError: ${e.message}")
                    }

                })

    }

    private fun saveTransactions(transactions: ArrayList<Transaction>) {
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


    override fun getAllHoldings(symbol: String?): Long {
//        dataManager.getHoldings()
//                .map { holdings -> holdings.filter { it.symbol == symbol || it.pairSymbol == symbol } }
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(object : SingleObserver<List<Transaction>?> {
//                    override fun onSuccess(holdings: List<Transaction>) {
//                        var maxQuantity = 0.toLong()
//                        holdings.forEach { if(it.symbol == symbol) maxQuantity += it. }
//                    }
//
//                    override fun onSubscribe(d: Disposable) {
//                        compositeDisposable?.add(d)
//                    }
//
//                    override fun onError(e: Throwable) {
//                        println("onError")
//                    }
//
//                })

        return 0.toLong()
    }

    companion object {
        val TAG = "UpdateCryptoTransPres"
    }
}