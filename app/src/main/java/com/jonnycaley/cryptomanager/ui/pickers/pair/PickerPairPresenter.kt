package com.jonnycaley.cryptomanager.ui.pickers.pair

import com.google.gson.Gson
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.Exchanges.Exchange
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.Exchanges.Exchanges
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.Exchanges.Symbol
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class PickerPairPresenter (var dataManager: PickerPairDataManager, var view: PickerPairContract.View) : PickerPairContract.Presenter{

    var compositeDisposable: CompositeDisposable? = null

    init {
        this.view.setPresenter(this)
    }

    override fun attachView() {
        if (compositeDisposable == null || (compositeDisposable as CompositeDisposable).isDisposed) {
            compositeDisposable = CompositeDisposable()
        }

        getPairs(view.getExchange(), view.getCryproSymbol())
    }

    private fun getPairs(exchange: String?, crytpoSymbol: String?) {

        dataManager.getExchanges()
                .observeOn(Schedulers.computation())
                .map { exchanges ->
                    if(exchange != "" && exchange != null) {
                        exchanges.exchanges?.filter { it.name?.toLowerCase() == exchange.toLowerCase() }
                    }
                    else {
                        exchanges.exchanges
                    }
                }
                .map { exchanges ->

                    val converters = ArrayList<String>()

                    exchanges.forEach { it.symbols?.filter { it.symbol?.toLowerCase() == crytpoSymbol?.toLowerCase() }?.forEach { converterz -> ArrayList<String>(converterz.converters).forEach { converters.add(it) } } }

                    val hashSet = HashSet<String>()
                    hashSet.addAll(converters)
                    converters.clear()
                    converters.addAll(hashSet)

                    return@map converters
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<ArrayList<String>> {

                    override fun onSuccess(converters: ArrayList<String>) {
                        view.hideProgressBar()
                        view.showPairs(converters)
                    }

                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable?.add(d)
                        view.showProgressBar()
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
        val TAG = "PickerPairPresenter"
    }

}