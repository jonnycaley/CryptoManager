package com.jonnycaley.cryptomanager.ui.fiat

import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
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
                .map { transactions -> transactions.filter { it.symbol == fiatSymbol } }
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

    private fun getWithdrawnFiatCount(transactions: List<Transaction>): Float {
        var depositedFiat = 0.toFloat()
        transactions.filter { it.quantity < 0  }.forEach { depositedFiat += it.quantity }
        return depositedFiat
    }

    private fun getDepositedFiatCount(transactions: List<Transaction>): Float {
        var depositedFiat = 0.toFloat()
        transactions.filter { it.quantity > 0 }.forEach { depositedFiat += it.quantity }
        return depositedFiat
    }

    private fun getAvailableFiatCount(transactions: List<Transaction>): Float {
        var availableFiat = 0.toFloat()
        transactions.forEach { availableFiat += it.quantity }
        return availableFiat
    }

    override fun detachView() {
        compositeDisposable?.dispose()
    }

}