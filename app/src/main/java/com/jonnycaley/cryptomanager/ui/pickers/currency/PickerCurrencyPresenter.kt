package com.jonnycaley.cryptomanager.ui.pickers.currency

import com.google.gson.Gson
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.ExchangeRates
import com.jonnycaley.cryptomanager.utils.JsonModifiers
import io.reactivex.Observer
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class PickerCurrencyPresenter (var dataManager: PickerCurrencyDataManager, var view: PickerCurrencyContract.View) : PickerCurrencyContract.Presenter{

    var compositeDisposable: CompositeDisposable? = null

    init {
        this.view.setPresenter(this)
    }

    override fun attachView() {
        if (compositeDisposable == null || (compositeDisposable as CompositeDisposable).isDisposed) {
            compositeDisposable = CompositeDisposable()
        }

        getFiats()
    }

    override fun getFiats() {
        if(dataManager.checkConnection()){

            dataManager.getExchangeRateService().getExchangeRates()
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.computation())
                    .map { fiats ->
                        return@map Gson().fromJson(JsonModifiers.jsonToCurrencies(fiats), ExchangeRates::class.java)
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<ExchangeRates> {
                        override fun onComplete() {

                        }

                        override fun onNext(fiats: ExchangeRates) {
                            view.showFiats(fiats)
                            view.hideProgressBar()
                        }

                        override fun onSubscribe(d: Disposable) {
                            view.hideNoInternetLayout()
                            view.showProgressBar()
                            println("Subscribed")
                            compositeDisposable?.add(d)
                        }

                        override fun onError(e: Throwable) {
                            view.hideNoInternetLayout()
                            view.showError()
                            println("onError: ${e.message}")
                        }
                    })
        } else {
            view.showNoInternetLayout()
        }
    }

    override fun detachView() {
        compositeDisposable?.dispose()
    }

    companion object {
        val TAG = "PickerCurrencyPresenter"
    }

}