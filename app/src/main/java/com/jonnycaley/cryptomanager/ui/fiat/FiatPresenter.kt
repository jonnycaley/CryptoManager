package com.jonnycaley.cryptomanager.ui.fiat

import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import com.jonnycaley.cryptomanager.utils.Utils
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.math.BigDecimal


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

        val filteredTrans = ArrayList<Transaction>()
        var symbol = ""
        var withdrawnFiatCount = 0.toBigDecimal()
        var depositedFiatCount = 0.toBigDecimal()
        var availableFiatCount = 0.toBigDecimal()

        dataManager.getTransactions()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map { transactions -> filteredTrans.addAll(transactions.filter { it.symbol == fiatSymbol || (it.pairSymbol == fiatSymbol && it.isDeducted) }.sortedBy { it.date }.asReversed()) }
                .map { symbol = Utils.getFiatSymbol(view.getFiatCode()) }
                .map { withdrawnFiatCount = getWithdrawnFiatCount(filteredTrans, fiatSymbol) }
                .map { depositedFiatCount = getDepositedFiatCount(filteredTrans, fiatSymbol) }
                .map { availableFiatCount = getAvailableFiatCount(filteredTrans, fiatSymbol) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<Unit> {

                    override fun onSuccess(transactions: Unit) {
                        view.showAvailableFiat(symbol, availableFiatCount)
                        view.showDepositedFiat(symbol, depositedFiatCount)
                        view.showWithdrawnFiat(symbol, withdrawnFiatCount)
                        view.showTransactions(symbol, filteredTrans)
                    }

                    override fun onSubscribe(d: Disposable) {
                        view.showProgressBar()
                    }

                    override fun onError(e: Throwable) {
                        println("onError")
                    }
                })
    }

    private fun getWithdrawnFiatCount(transactions: List<Transaction>, fiatSymbol : String): BigDecimal {
        var depositedFiat = 0.toBigDecimal()
        transactions.forEach { println(it.symbol+"/"+it.pairSymbol+" - Price: "+ it.price + " - Quantity: " + it.quantity) }

        transactions.filter { it.symbol == fiatSymbol && it.quantity < 0.toBigDecimal() }.forEach { depositedFiat += it.quantity }
        transactions.filter { (it.pairSymbol == fiatSymbol) && (it.isDeducted) && (it.quantity > 0.toBigDecimal()) }.forEach{ depositedFiat -= (it.price * it.quantity) }

        return depositedFiat
    }

    private fun getDepositedFiatCount(transactions: List<Transaction>, fiatSymbol : String): BigDecimal {
        var depositedFiat = 0.toBigDecimal()

        transactions.filter { it.symbol == fiatSymbol && it.quantity > 0.toBigDecimal() }.forEach { depositedFiat += it.quantity }
        transactions.filter { (it.pairSymbol == fiatSymbol) && (it.isDeducted) && (it.quantity < 0.toBigDecimal())}.forEach{ depositedFiat -= (it.price * it.quantity) }

        return depositedFiat
    }

    private fun getAvailableFiatCount(transactions: List<Transaction>, fiatSymbol : String): BigDecimal {
        var availableFiat = 0.toBigDecimal()

        transactions.filter { it.symbol == fiatSymbol }.forEach { availableFiat += it.quantity }
        transactions.filter { (it.pairSymbol == fiatSymbol) && (it.isDeducted) }.forEach{ availableFiat -= (it.price * it.quantity) }

        return availableFiat
    }

    override fun detachView() {
        compositeDisposable?.dispose()
    }

}