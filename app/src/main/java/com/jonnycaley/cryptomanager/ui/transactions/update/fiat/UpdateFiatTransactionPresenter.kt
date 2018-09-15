package com.jonnycaley.cryptomanager.ui.transactions.update.fiat

import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import io.reactivex.CompletableObserver
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList

class UpdateFiatTransactionPresenter(var dataManager: UpdateFiatTransactionDataManager, var view: UpdateFiatTransactionContract.View) : UpdateFiatTransactionContract.Presenter {

    var compositeDisposable: CompositeDisposable? = null

    lateinit var otherTransactions: ArrayList<Transaction>

    init {
        this.view.setPresenter(this)
    }

    override fun attachView() {
        if (compositeDisposable == null || (compositeDisposable as CompositeDisposable).isDisposed) {
            compositeDisposable = CompositeDisposable()
        }

        getAllTransactions()
    }

    private fun getAllTransactions() {

        val transaction = view.getTransaction()

        dataManager.getTransactions()
                .map { transactions -> transactions.filter { it == transaction } }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<List<Transaction>> {

                    override fun onSuccess(transactions: List<Transaction>) {
                        otherTransactions = ArrayList(transactions.filter { it != transaction })
                        println(transactions.size)
                        println(otherTransactions.size)

                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                        println("onError")
                    }
                })
    }

    override fun updateFiatTransaction(type: String, exchange: String, currency: String, quantity: Long, date: Date, notes: String) {

        val newTransaction = Transaction(type, exchange, currency, quantity, 1, date, notes)

        otherTransactions.add(newTransaction)

        dataManager.saveTransactions(otherTransactions)
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
                        println("onError: ${e.message}")
                    }
                })
    }


    override fun detachView() {
        compositeDisposable?.dispose()
    }

    companion object {
        val TAG = "UpdateCryptoTransactionPres"
    }

}