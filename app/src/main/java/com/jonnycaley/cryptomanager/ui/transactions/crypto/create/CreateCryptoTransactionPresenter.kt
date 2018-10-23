package com.jonnycaley.cryptomanager.ui.transactions.crypto.create

import com.google.gson.Gson
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.AllCurrencies.Currencies
import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import io.reactivex.CompletableObserver
import io.reactivex.Observer
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*

class CreateCryptoTransactionPresenter(var dataManager: CreateCryptoTransactionDataManager, var view: CreateCryptoTransactionContract.View) : CreateCryptoTransactionContract.Presenter {

    var compositeDisposable: CompositeDisposable? = null

    init {
        this.view.setPresenter(this)
    }

    override fun attachView() {
        if (compositeDisposable == null || (compositeDisposable as CompositeDisposable).isDisposed) {
            compositeDisposable = CompositeDisposable()
        }
        getCoinDetails()
    }

    override fun saveCryptoTransaction(isBuy: Boolean, exchange: String, pair: String, price: Float, quantity: Float, date: Date?, isDeducted: Boolean, notes: String) {

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
                            isDeductedPrice = response.data!!.last().close!!
                    }
                    .flatMap {
                        dataManager.getCryptoCompareService().getPriceAtMinute(view.getSymbol(), "USD", "1", "1", date?.time.toString())
                    }
                    .map { response ->
                        println(response.data?.isNotEmpty())
                        if (response.data?.isNotEmpty()!!)
                            priceUsd = response.data?.get(1)?.close!!
                    }
                    .flatMapSingle { dataManager.getAllCryptos() }
                    .map { currencies -> allCryptos = currencies }
                    .observeOn(Schedulers.io())
                    .flatMapSingle { dataManager.getTransactions() }
                    .observeOn(Schedulers.computation())
                    .map { transactions ->
                        val newTransaction = Transaction(exchange, view.getSymbol(), pair, correctQuantity, price, priceUsd, date!!, notes, isDeducted, isDeductedPrice, allCryptos!!.baseImageUrl + allCryptos!!.data?.firstOrNull { it.symbol == view.getSymbol() }?.imageUrl, allCryptos!!.baseImageUrl + allCryptos!!.data?.firstOrNull { it.symbol == pair }?.imageUrl)
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
                        }

                        override fun onError(e: Throwable) {
                            println("onError: ${e.message}")
                        }
                    })
        } else {

        }
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

//    private fun saveTransaction(isBuy: Boolean, exchange: String, pair: String, price: Float, quantity: Float, date: Date?, notes: String, isDeducted: Boolean, isDeductedPrice: Double) {
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
//                        holdings.add(newTransaction)
//
//                        dataManager.saveTransactions(holdings)
//                                .subscribeOn(Schedulers.io())
//                                .observeOn(AndroidSchedulers.mainThread())
//                                .subscribe(object : CompletableObserver {
//                                    override fun onComplete() {
//                                        view.onTransactionComplete()
//                                    }
//
//                                    override fun onSubscribe(d: Disposable) {
//                                        compositeDisposable?.add(d)
//                                    }
//
//                                    override fun onError(e: Throwable) {
//                                        println("onError: ${e.message}")
//                                    }
//
//                                })
//
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

    private fun getCoinDetails() {
        if (dataManager.checkConnection()) {


        } else {

        }
    }


    override fun detachView() {
        compositeDisposable?.dispose()
    }

    companion object {
        val TAG = "CreateCryptoTransPres"
    }

}