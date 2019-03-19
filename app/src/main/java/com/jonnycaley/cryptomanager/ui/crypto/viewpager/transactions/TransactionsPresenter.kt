package com.jonnycaley.cryptomanager.ui.crypto.viewpager.transactions

import android.util.Log
import com.google.gson.Gson
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.CurrentPrice.Price
import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import com.jonnycaley.cryptomanager.utils.Constants
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.AllCurrencies.Currencies
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.Rate
import io.reactivex.Observer


class TransactionsPresenter(var dataManager: TransactionsDataManager, var view: TransactionsContract.View) : TransactionsContract.Presenter {

    var compositeDisposable: CompositeDisposable? = null

    var price : Double? = null

    init {
        this.view.setPresenter(this)
    }

    override fun attachView() {
        if (compositeDisposable == null || (compositeDisposable as CompositeDisposable).isDisposed) {
            compositeDisposable = CompositeDisposable()
        }

    }

    override fun getCryptoPrice() {

        if (dataManager.checkConnection()) {
            dataManager.getCryptoCompareService().getCurrentPrice(view.getSymbol(), "USD")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<Price> {
                        override fun onComplete() {
                        }

                        override fun onNext(response: Price) {
                            price = response.uSD
                            getTransactions(response.uSD)
                        }

                        override fun onSubscribe(d: Disposable) {
                            compositeDisposable?.add(d)
                        }

                        override fun onError(e: Throwable) {
                            println("onError: ${e.message}")
                            getTransactions(null)
                        }
                    })
        } else {
            getTransactions(null)
        }
    }

    override fun onResume() {
        if(price != null)
            getTransactions(price)
        else
//            view.showNoInternet()
            getCryptoPrice()
    }

    override fun getAllCurrencies() {

        dataManager.readAllCrytpos()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<Currencies> {
                    override fun onSuccess(allCurrencies: Currencies) {
                        view.startTransaction(allCurrencies.data?.first { it.symbol?.toLowerCase() == view.getSymbol()?.toLowerCase() }, allCurrencies.baseImageUrl, allCurrencies.baseLinkUrl)
                    }

                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable?.add(d)
                    }

                    override fun onError(e: Throwable) {
                        Log.i(TAG, "onError: ${e.message}")
                    }
                })

    }

    private fun getTransactions(basePrice: Double?) {

        val symbol = view.getSymbol()

        var transactionz : List<Transaction>? = null

        dataManager.getTransactions()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map { transactions -> transactionz = transactions.filter { it.symbol == symbol || ((it.pairSymbol == symbol) && (it.isDeducted)) } }
                .observeOn(Schedulers.io())
                .flatMap { dataManager.getBaseFiat() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<Rate> {
                    override fun onSuccess(baseFiat: Rate) {
                        transactionz?.let { view.loadTransactions(it, basePrice, baseFiat) }
                        view.hideRefreshing()
                    }

                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable?.add(d)
                    }

                    override fun onError(e: Throwable) {
                        println("onError")
                    }
                })
    }

    override fun detachView() {
        compositeDisposable?.dispose()
    }

    companion object {
        val TAG = "TransactionsPresenter"
    }
}