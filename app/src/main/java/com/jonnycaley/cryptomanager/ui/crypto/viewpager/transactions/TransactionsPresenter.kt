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

        getCryptoPrice()
    }

    private fun getCryptoPrice() {
        if (dataManager.checkConnection()) {

            dataManager.getCryptoCompareService().getCurrentPrice(view.getSymbol()!!, "USD")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : SingleObserver<Price> {
                        override fun onSuccess(response: Price) {
                            price = response.uSD
                            getTransactions(response.uSD)
                        }

                        override fun onSubscribe(d: Disposable) {
                            compositeDisposable?.add(d)
                        }

                        override fun onError(e: Throwable) {
                            println("onError: ${e.message}")
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
            getCryptoPrice()
    }

    override fun getAllCurrencies() {

        dataManager.readStorage(Constants.PAPER_ALL_CRYPTOS)
                .map { json -> Gson().fromJson(json, Currencies::class.java) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<Currencies> {
                    override fun onSuccess(allCurrencies: Currencies) {
                        view.startTransaction(allCurrencies.data?.filter { it.symbol == view.getSymbol() }?.get(0), allCurrencies.baseImageUrl, allCurrencies.baseLinkUrl)
                    }

                    override fun onSubscribe(d: Disposable) {
                        println("Subscribed")
                        compositeDisposable?.add(d)
                    }

                    override fun onError(e: Throwable) {
                        println("onError: ${e.message}")
                    }
                })

    }

    private fun getTransactions(basePrice: Double?) {

        val symbol = view.getSymbol()

        dataManager.getTransactions()
                .map { transactions -> transactions.filter { it.symbol == symbol || ((it.pairSymbol == symbol) && (it.isDeducted)) } }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<List<Transaction>> {
                    override fun onSuccess(transactions: List<Transaction>) {
                        view.loadTransactions(transactions, basePrice)
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