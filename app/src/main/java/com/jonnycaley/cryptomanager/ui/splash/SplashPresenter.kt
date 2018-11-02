package com.jonnycaley.cryptomanager.ui.splash

import android.util.Log
import com.google.gson.Gson
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.AllCurrencies.Currencies
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.Exchanges.Exchanges
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.ExchangeRates
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.Rate
import com.jonnycaley.cryptomanager.utils.JsonModifiers
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.Observer
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

        checkTheme()
    }

    private fun checkTheme() {

        dataManager.readTheme()
                .subscribeOn(Schedulers.io()) //computation as this is a large data set and therefore will be cpu intensive
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<Boolean> {
                    override fun onSuccess(isDarkTheme: Boolean) {
                        if(isDarkTheme)
                            view.setDarkTheme()
                        checkForStorage()
                    }

                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable?.add(d)
                    }

                    override fun onError(e: Throwable) {
                        println("onError: ${e.message}")
                    }

                })
    }

    private fun checkForStorage() {

        //checking for cryptos is long and if the exchanges is stored it is 99.9% likely the cryptos are as well as they are both done in the same stream

        dataManager.readAllExchanges()
                .doOnError {
                    getCurrencies()
                }
                .subscribeOn(Schedulers.io()) //computation as this is a large data set and therefore will be cpu intensive
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<Exchanges> {
                    override fun onSuccess(t: Exchanges) {
                        view.showUsingStorage()
                        view.toBaseActivity()
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

            val rate = Rate(); rate.fiat = "USD"; rate.rate = 1.toBigDecimal()

            dataManager.getExchangeRateService().getExchangeRates()
                    .flatMapCompletable { json ->
                        dataManager.saveAllRates(Gson().fromJson(JsonModifiers.jsonToCurrencies(json), ExchangeRates::class.java))
                    }
                    .andThen(dataManager.saveBaseRate(rate))
                    .andThen(dataManager.getCryptoCompareService().getAllCrypto())
                    .flatMapCompletable { response ->
                        dataManager.saveAllCryptos(Gson().fromJson(JsonModifiers.jsonToCryptos(response), Currencies::class.java))
                    }
                    .andThen(dataManager.getCryptoCompareService().getAllExchanges())
                    .flatMapCompletable { response ->
                        dataManager.saveAllExchanges(Gson().fromJson(JsonModifiers.jsonToExchanges(response), Exchanges::class.java))
                    }
//                    .flatMap {
//                        dataManager.getCryptoCompareService().getAllCrypto()
//                    }
//                    .flatMap { response ->
//                        dataManager.saveAllCryptos(Gson().fromJson(JsonModifiers.jsonToCryptos(response), Currencies::class.java)).toObservable<Any>()
//                    }
//                    .flatMap {
//                        dataManager.getCryptoCompareService().getAllExchanges()
//                    }
//                    .flatMap { response ->
//                        dataManager.saveAllExchanges(Gson().fromJson(JsonModifiers.jsonToExchanges(response), Exchanges::class.java)).toObservable<Any>()
//                    }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object :  CompletableObserver {

                        override fun onComplete() {
//                            test()
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

    private fun test() {
        dataManager.readBaseRate()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<Rate> {
                    override fun onSuccess(t: Rate) {
                        Log.i(TAG, "test readBaseRate onSuccess")
                        println(t.fiat)
                    }


                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable?.add(d)
                    }

                    override fun onError(e: Throwable) {
                        Log.i(TAG, "test: ${e.message}")
                    }

                })

        dataManager.readAllRates()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<ExchangeRates> {
                    override fun onSuccess(t: ExchangeRates) {
                        Log.i(TAG, "test readAllRates onSuccess")
                        println(t.rates?.size.toString())
                    }


                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable?.add(d)
                    }

                    override fun onError(e: Throwable) {
                        Log.i(TAG, "test: ${e.message}")
                    }

                })

    }

    companion object {
        val TAG = "SplashPresenter"
    }

    override fun detachView() {
        compositeDisposable?.dispose()
    }

}