package com.jonnycaley.cryptomanager.ui.splash

import android.util.Log
import com.jonnycaley.cryptomanager.utils.Constants
import com.jonnycaley.cryptomanager.utils.JsonModifiers
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class SplashPresenter(var dataManager: SplashDataManager, var view: SplashContract.View) : SplashContract.Presenter{

    var compositeDisposable: CompositeDisposable? = null

    init {
        this.view.setPresenter(this)
    }

    override fun attachView() {
        if (compositeDisposable == null || (compositeDisposable as CompositeDisposable).isDisposed) {
            compositeDisposable = CompositeDisposable()
        }

        checkForStorage()
    }

    private fun checkForStorage() {

        var isStoragePresent = true

        //checking for cryptos is long and if the exchanges is stored it is 99.9% likely the cryptos are as well as they are both done in the same stream

        dataManager.readStorage(Constants.PAPER_ALL_EXCHANGES)
                .map { storage ->
                    if((storage == "null") || (storage == ""))
                        isStoragePresent = false
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<Unit?> {
                    override fun onSuccess(t: Unit) {
                        if(isStoragePresent){
                            view.showUsingStorage()
                            view.toBaseActivity()
                        }
                        else {
                            getCurrencies()
                        }
                    }

                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable?.add(d)
                    }

                    override fun onError(e: Throwable) {
                        println("onError: ${e.message}")
                    }

                })

    }

    override fun getCurrencies() {

        if(dataManager.checkConnection()){

            dataManager.getCryptoCompareService().getAllCurrencies()
                    .map { response ->
                        dataManager.writeToStorage(Constants.PAPER_ALL_CRYPTOS, JsonModifiers.jsonToCryptos(response))
                    }
                    .flatMap {
                        dataManager.getCryptoCompareService().getAllExchanges()
                    }
                    .map { response ->
                        dataManager.writeToStorage(Constants.PAPER_ALL_EXCHANGES, JsonModifiers.jsonToExchanges(response))
                    }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : SingleObserver<Unit?> {
                        override fun onSuccess(t: Unit) {
                            view.toBaseActivity()
                        }

                        override fun onSubscribe(d: Disposable) {
                            compositeDisposable?.add(d)
                        }

                        override fun onError(e: Throwable) {
                            println("onError: ${e.message}")
                        }

                    })
        } else {
            view.showInternetRequired()
        }
    }

    companion object {
        val TAG = "SplashPresenter"
    }

    override fun detachView() {
        compositeDisposable?.dispose()
    }

}