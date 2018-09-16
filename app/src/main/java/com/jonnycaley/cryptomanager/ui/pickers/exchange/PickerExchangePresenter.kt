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

    private fun getExchanges(crypto: String?) {

        dataManager.readExchanges(Constants.PAPER_ALL_EXCHANGES)
                ?.map { json ->
                    println("SeeHere$json")
                    Gson().fromJson(json, Exchanges::class.java) }
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(object : SingleObserver<Exchanges?> {
                    override fun onSuccess(gson: Exchanges) {
                        if(crypto != null){
                            val newExchanges = ArrayList<Exchange>()
                            gson.exchanges?.forEach { exchange -> exchange.symbols?.forEach { symbol -> if (symbol.symbol == crypto){ newExchanges.add(exchange) } } }

                            view.showExchanges(newExchanges)
                        } else {
                            view.showExchanges(gson.exchanges)
                        }
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                        println("onError: ${e.message}")
                    }

                })
    }

    override fun detachView() {
        compositeDisposable?.dispose()
    }

    companion object {
        val TAG = "PickerExchangePresenter"
    }

}