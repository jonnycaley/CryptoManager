package com.jonnycaley.cryptomanager.ui.fiat

import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import com.jonnycaley.cryptomanager.data.model.DataBase.Variables
import com.jonnycaley.cryptomanager.utils.Utils
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class FiatPresenter(var dataManager: FiatDataManager, var view: FiatContract.View) : FiatContract.Presenter{

    var compositeDisposable: CompositeDisposable? = null

    init {
        this.view.setPresenter(this)
    }

    override fun attachView() {
        if (compositeDisposable == null || (compositeDisposable as CompositeDisposable).isDisposed) {
            compositeDisposable = CompositeDisposable()
        }

        getTransactions(view.getFiatCode())
    }

    override fun getTransactions(fiatSymbol: String) {

        dataManager.getTransactions()
                .map { transactions -> transactions.filter { it.currency == fiatSymbol } }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<List<Transaction>> {

                    override fun onSuccess(transactions: List<Transaction>) {

                        view.showAvailableFiat(Utils.getFiatSymbol(view.getFiatCode()), getAvailableFiatCount(transactions))
                        view.showDepositedFiat(Utils.getFiatSymbol(view.getFiatCode()), getDepositedFiatCount(transactions))
                        view.showWithdrawnFiat(Utils.getFiatSymbol(view.getFiatCode()), getWithdrawnFiatCount(transactions))
                        view.showTransactions(Utils.getFiatSymbol(view.getFiatCode()), transactions.sortedBy { it.date }.asReversed())
                    }

                    override fun onSubscribe(d: Disposable) {
                        view.showProgressBar()
                    }

                    override fun onError(e: Throwable) {
                        println("onError")
                    }

                })
    }

    private fun getWithdrawnFiatCount(transactions: List<Transaction>): Long {
        var depositedFiat = 0.toLong()
        transactions.filter { it.type == Variables.Transaction.FiatType.widthdrawl }.forEach { depositedFiat -= it.quantity }
        return depositedFiat
    }

    private fun getDepositedFiatCount(transactions: List<Transaction>): Long {
        var depositedFiat = 0.toLong()
        transactions.filter { it.type == Variables.Transaction.FiatType.deposit }.forEach { depositedFiat += it.quantity }
        return depositedFiat
    }

    private fun getAvailableFiatCount(transactions: List<Transaction>): Long {
        var availableFiat = 0.toLong()
        transactions.filter { it.type == Variables.Transaction.FiatType.deposit }.forEach { availableFiat += it.quantity }
        transactions.filter { it.type == Variables.Transaction.FiatType.widthdrawl }.forEach { availableFiat -= it.quantity }
        return availableFiat
    }

    override fun detachView() {
        compositeDisposable?.dispose()
    }

}