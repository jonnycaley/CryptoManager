package com.jonnycaley.cryptomanager.ui.transactions.crypto.update

import com.google.gson.Gson
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.AllCurrencies.Currencies
import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import io.reactivex.CompletableObserver
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

    companion object {
        val TAG = "UpdateCryptoTransactionPresenter"
    }

    override fun updateCryptoTransaction(originalTransaction: Transaction, isBuy: Boolean, exchange: String, pair: String, price: Float, quantity: Float, date: Date?, isDeducted: Boolean, notes: String) {

        var correctQuantity = quantity
        var priceUsd = 1.toDouble()
        var isDeductedPrice = 1.toDouble()
        var allCryptos: Currencies? = null

        if (!isBuy)
            correctQuantity *= -1

        if (dataManager.checkConnection()) {

            dataManager.getCryptoCompareService().getPriceAtMinute(pair, "USD", "1", "1", date?.time.toString())
                    .map { response ->
                        if (response.data?.isNotEmpty()!!)
                            isDeductedPrice = response.data!![1].close!!
                    }
                    .flatMap {
                        dataManager.getCryptoCompareService().getPriceAtMinute(view.getSymbol(), "USD", "1", "1", date?.time.toString())
                    }
                    .map { response ->
                        if (response.data?.isNotEmpty()!!)
                            priceUsd = response.data?.get(1)?.close!!
                    }
                    .flatMap { dataManager.getAllCryptos() }
                    .map { json -> allCryptos = Gson().fromJson(json, Currencies::class.java) }
                    .flatMap { dataManager.getTransactions() }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : SingleObserver<java.util.ArrayList<Transaction>> {
                        override fun onSuccess(transactions: java.util.ArrayList<Transaction>) {

                            val newTransaction = Transaction(exchange, view.getSymbol(), pair, correctQuantity, price, priceUsd, date!!, notes, isDeducted, isDeductedPrice, allCryptos!!.baseImageUrl + allCryptos!!.data?.firstOrNull { it.symbol == view.getSymbol() }?.imageUrl, allCryptos!!.baseImageUrl + allCryptos!!.data?.firstOrNull { it.symbol == pair }?.imageUrl)
                            transactions.remove(originalTransaction)
                            transactions.add(newTransaction)

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


//    private fun updateTransaction(originalTransaction: Transaction, isBuy: Boolean, exchange: String, pair: String, price: Float, quantity: Float, date: Date?, notes: String, isDeductedPrice: Double?) {
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
//                        val newTransaction = Transaction(exchange, view.getSymbol(), pair, correctQuantity, price, priceUsd, date!!, notes, isDeductedPrice)
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

}