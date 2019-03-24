package com.jonnycaley.cryptomanager.ui.pickers.exchange

import com.google.gson.Gson
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.Exchanges.Exchange
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.Exchanges.Exchanges
import com.jonnycaley.cryptomanager.utils.Constants
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class PickerExchangePresenter(var dataManager: PickerExchangeDataManager, var view: PickerExchangeContract.View) : PickerExchangeContract.Presenter {

    var compositeDisposable: CompositeDisposable? = null

    init {
        this.view.setPresenter(this)
    }

    override fun attachView() {
        if (compositeDisposable == null || (compositeDisposable as CompositeDisposable).isDisposed) {
            compositeDisposable = CompositeDisposable()
        }

        getExchanges(view.getCrypto())
    }

    var exchanges = ArrayList<Exchange>()

    override fun getExchanges(crypto: String?) {

        dataManager.readAllExchanges()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map { gson ->
                    exchanges.clear()
                    if(crypto != null) {
                        gson.exchanges?.forEach { exchange -> exchange.symbols?.forEach { symbol -> if (symbol.symbol == crypto){ exchanges?.add(exchange) } } }
                    } else {
                        println("2")
                        exchanges = gson.exchanges as ArrayList<Exchange>
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<Unit?> {
                    override fun onSuccess(gson: Unit) {
                        view.showExchanges(exchanges)
                        view.showSearchBar()
                        view.hideProgressBar()
                    }

                    override fun onSubscribe(d: Disposable) {
                        view.showProgressBar()
                    }

                    override fun onError(e: Throwable) {
                        view.hideProgressBar()
                        view.showError()
                        println("onError: ${e.message}")

                    }
                })
    }

    override fun filterExchanges(trim: String) {
        view.showExchanges(exchanges?.filter { it.name?.toLowerCase().toString().contains(trim.toLowerCase()) })
    }

    override fun detachView() {
        compositeDisposable?.dispose()
    }

    companion object {
        val TAG = "PickerExchangePresenter"
    }

}