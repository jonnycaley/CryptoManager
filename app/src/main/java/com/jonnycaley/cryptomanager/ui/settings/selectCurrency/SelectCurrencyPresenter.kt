package com.jonnycaley.cryptomanager.ui.settings.selectCurrency

import com.google.gson.Gson
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.ExchangeRates
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.Rate
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class SelectCurrencyPresenter(var dataManager: SelectCurrencyDataManager, var view: SelectCurrencyContract.View) : SelectCurrencyContract.Presenter {

    var compositeDisposable: CompositeDisposable? = null

    init {
        this.view.setPresenter(this)
    }

    override fun attachView() {
        if (compositeDisposable == null || (compositeDisposable as CompositeDisposable).isDisposed) {
            compositeDisposable = CompositeDisposable()
        }

        getAllFiats()
    }

    private fun getAllFiats() {

        var baseFiat = Rate()

        dataManager.getBaseFiat()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map { fiat -> baseFiat = fiat }
                .observeOn(Schedulers.io())
                .flatMap { dataManager.getFiats() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<ExchangeRates> {
                    override fun onSuccess(fiats: ExchangeRates) {
                        view.showFiats(fiats.rates?.sortedBy { it.fiat }, baseFiat)
                    }

                    override fun onSubscribe(d: Disposable) {
                        println("Subscribed")
                        compositeDisposable?.add(d)
                    }

                    override fun onError(e: Throwable) {
                        println("onErrorFiats: ${e.message}")
                    }
                })
    }

    override fun saveBaseCurrency(symbol: Rate) {

        dataManager.saveBaseCurrency(symbol)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        view.onCurrencySaved()
                    }

                    override fun onSubscribe(d: Disposable) {
                        println("Subscribed")
                        compositeDisposable?.add(d)
                    }

                    override fun onError(e: Throwable) {
                        println("onErrorFiats: ${e.message}")
                    }
                })
//        dataManager.saveBaseCurrency(symbol)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(object : CompletableObserver {
//                    override fun onComplete() {
//                        view.onCurrencySaved()
//                    }
//
//                    override fun onSubscribe(d: Disposable) {
//                        println("Subscribed")
//                        compositeDisposable?.add(d)
//                    }
//
//                    override fun onError(e: Throwable) {
//                        println("onErrorFiats: ${e.message}")
//                    }
//                })
    }

    override fun detachView() {
        compositeDisposable?.dispose()
    }

}