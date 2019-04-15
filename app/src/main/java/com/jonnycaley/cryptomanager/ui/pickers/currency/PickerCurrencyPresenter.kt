package com.jonnycaley.cryptomanager.ui.pickers.currency

import com.google.gson.Gson
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.ExchangeRates
import com.jonnycaley.cryptomanager.utils.JsonModifiers
import com.jonnycaley.cryptomanager.utils.Utils
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

    var fiatz : ExchangeRates? = null

    /*
    Function returns the fiats from storage
    */
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
                            fiatz = fiats
                            view.showFiats(fiats.rates)
                            view.showSearchBar()
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

    override fun filterFiats(trim: String) {
        fiatz?.let { fiats ->
            view.showFiats(fiats.rates?.filter { it.fiat?.toLowerCase().toString().contains(trim.toLowerCase()) || Utils.getFiatName(it.fiat).toLowerCase().contains(trim.toLowerCase()) })
        }
    }

    override fun detachView() {
        compositeDisposable?.dispose()
    }

    companion object {
        val TAG = "PickerCurrencyPresenter"
    }

}