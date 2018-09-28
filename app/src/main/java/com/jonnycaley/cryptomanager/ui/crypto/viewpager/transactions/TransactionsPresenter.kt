package com.jonnycaley.cryptomanager.ui.crypto.viewpager.transactions

import com.jonnycaley.cryptomanager.data.model.CryptoCompare.CurrentPrice.Price
import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class TransactionsPresenter(var dataManager: TransactionsDataManager, var view: TransactionsContract.View) : TransactionsContract.Presenter {

    var compositeDisposable: CompositeDisposable? = null

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
        if(dataManager.checkConnection()){

            dataManager.getCryptoCompareService().getCurrentPrice(view.getSymbol()!!, "USD")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : SingleObserver<Price> {
                        override fun onSuccess(response: Price) {
                            println("Price in usd" + response.uSD)
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

    private fun getTransactions(basePrice : Double?) {

        val symbol = view.getSymbol()

        dataManager.getTransactions()
                .map { transactions -> transactions.filter { it.symbol == symbol || ((it.pairSymbol == symbol) && (it.isDeductedPrice != null))} }
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
}