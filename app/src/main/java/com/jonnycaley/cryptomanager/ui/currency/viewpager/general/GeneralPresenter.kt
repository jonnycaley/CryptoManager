package com.jonnycaley.cryptomanager.ui.currency.viewpager.general

import com.jonnycaley.cryptomanager.data.model.CryptoCompare.HistoricalData.Data
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class GeneralPresenter (var dataManager: GeneralDataManager, var view: GeneralContract.View) : GeneralContract.Presenter{

    var compositeDisposable: CompositeDisposable? = null

    init {
        this.view.setPresenter(this)
    }

    override fun attachView() {
        if (compositeDisposable == null || (compositeDisposable as CompositeDisposable).isDisposed) {
            compositeDisposable = CompositeDisposable()
        }

        getCurrencyData(view.getSymbol())
    }

    private fun getCurrencyData(symbol : String) {
        if(dataManager.checkConnection()){
            dataManager.getCryptoCompareService().getCurrencyData(dayString, symbol, "USD", "100")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : SingleObserver<Data> {
                        override fun onSuccess(response: Data) {
                            view.showCandlestickChart(response)
                        }

                        override fun onSubscribe(d: Disposable) {
                            println("onSubscribed")
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

    companion object {
        val TAG = "GeneralPresenter"

        val dayString = "day"
        val hourString = "hour"
        val minuteString = "minute"
    }

}