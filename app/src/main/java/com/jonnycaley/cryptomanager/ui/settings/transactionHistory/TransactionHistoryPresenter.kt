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

    /*
    The getTransactions method reads all transactions from internal storage.
    The transactions are then displayed to the user.
    If there are no transactions then a 'no transactions' layout is displayed instead
     */
    override fun getTransactions() {
        dataManager.getTransactions()                           //read the transactions from storage
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<ArrayList<Transaction>>{
                    override fun onSuccess(transactions: ArrayList<Transaction>) {
                        if(transactions.isNotEmpty()) {         //if the transactions list is NOT empty...
                            view.hideNoTransactionsLayout()     //hide the no transactions layout
                            view.showTransactions(transactions) //show the transactions
                        } else                                  //if the transactions list is empty...
                            view.showNoTransactionsLayout()     //show the no transactions layout
                    }

                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable?.add(d)             //assign the disposable to prevent memory leaks
                    }

                    override fun onError(e: Throwable) {
                        println("onError: ${e.message}")        //print the error to console for debugging
                    }
                })
    }

    override fun detachView() {
        compositeDisposable?.dispose()
    }

    val TAG = "TransactionHistoryPres"

}