package com.jonnycaley.cryptomanager.ui.settings.transactionHistory

import android.util.Log
import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class TransactionHistoryPresenter(var dataManager: TransactionHistoryDataManager, var view: TransactionHistoryContract.View) : TransactionHistoryContract.Presenter {

    var compositeDisposable: CompositeDisposable? = null

    init {
        this.view.setPresenter(this)
    }

    override fun attachView() {
        if (compositeDisposable == null || (compositeDisposable as CompositeDisposable).isDisposed) {
            compositeDisposable = CompositeDisposable()
        }
        getTransactions()
    }

    override fun getTransactions() {
        dataManager.getTransactions()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<ArrayList<Transaction>>{
                    override fun onSuccess(transactions: ArrayList<Transaction>) {
                        if(transactions.isNotEmpty()) {
                            view.hideNoTransactionsLayout()
                            Log.i(TAG, transactions.size.toString())
                            view.showTransactions(transactions)
                        } else
                            view.showNoTransactionsLayout()
                    }

                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable?.add(d)
                    }

                    override fun onError(e: Throwable) {
                        println("onError: ${e.message}")
                    }

                })
    }

    override fun detachView() {
        compositeDisposable?.dispose()
    }

    val TAG = "TransactionHistoryPres"

}