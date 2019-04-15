package com.jonnycaley.cryptomanager.ui.pickers.pair

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

    val converters = ArrayList<String>()

    /*
    Function gets the pairs
    */
    override fun getPairs(exchange: String?, crytpoSymbol: String?) {

        dataManager.getExchanges() //get exchanges
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map { exchanges ->
                    if(exchange != "" && exchange != null) {
                        exchanges.exchanges?.filter { it.name?.toLowerCase() == exchange.toLowerCase() } //get correct exchanges
                    }
                    else {
                        exchanges.exchanges //show all
                    }
                }
                .map { exchanges ->

                    exchanges.forEach { it.symbols?.filter { it.symbol?.toLowerCase() == crytpoSymbol?.toLowerCase() }?.forEach { converterz -> ArrayList<String>(converterz.converters).forEach { converters.add(it) } } } //filter exchanges

                    val hashSet = HashSet<String>()
                    hashSet.addAll(converters)
                    converters.clear()
                    converters.addAll(hashSet) //add all exchanges

                    return@map converters
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<ArrayList<String>> {

                    override fun onSuccess(converters: ArrayList<String>) {
                        view.showPairs(converters)
                        view.hideProgressBar() //hide progress
                        view.showSearchbar() //show search bar
                    }

                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable?.add(d)
                        view.showProgressBar()
                    }

                    override fun onError(e: Throwable) {
                        println("onError: ${e.message}")
                        view.showError()
                        view.hideProgressBar()
                    }
                })
    }

    /*
    Function filters the pairs
    */
    override fun filterPairs(trim: String) {
        view.showPairs(converters.filter { it.toLowerCase().contains(trim.toLowerCase()) })
    }

    override fun detachView() {
        compositeDisposable?.dispose()
    }

    companion object {
        val TAG = "PickerPairPresenter"
    }
}