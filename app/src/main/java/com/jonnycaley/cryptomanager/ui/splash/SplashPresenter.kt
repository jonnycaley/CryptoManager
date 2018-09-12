package com.jonnycaley.cryptomanager.ui.splash

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
        //start stuff here

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
                        return@map true
                    }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : SingleObserver<Boolean?> {
                        override fun onSuccess(t: Boolean) {
                            view.toBaseActivity()
                        }

                        override fun onSubscribe(d: Disposable) {

                        }

                        override fun onError(e: Throwable) {
                            println("onError: ${e.message}")
                        }

                    })
        } else {

        }
    }

    override fun detachView() {
        compositeDisposable?.dispose()
    }

}