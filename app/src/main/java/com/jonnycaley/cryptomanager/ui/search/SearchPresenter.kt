package com.jonnycaley.cryptomanager.ui.search

import com.jonnycaley.cryptomanager.data.model.CryptoCompare.AllCurrencies.Currencies
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.ExchangeRates
import com.jonnycaley.cryptomanager.ui.home.HomeFragment
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class SearchPresenter(var dataManager: SearchDataManager, var view: SearchContract.View) : SearchContract.Presenter {

    var compositeDisposable: CompositeDisposable? = null

    var allCurrencies: Currencies? = null
    var allFiats: ExchangeRates? = null

    init {
        this.view.setPresenter(this)
    }

    override fun attachView() {
        if (compositeDisposable == null || (compositeDisposable as CompositeDisposable).isDisposed) {
            compositeDisposable = CompositeDisposable()
        }

        when (view.getSearchType()) {
            HomeFragment.CURRENCY_STRING -> {
                getAllCurrencies()
            }
            HomeFragment.FIAT_STRING -> {
                getAllFiats()
            }
        }
    }

    override fun getAllFiats() {

        dataManager.getFiats()
                .map { fiats -> allFiats = fiats}
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<Unit> {
                    override fun onSuccess(na: Unit) {
                        allFiats?.rates?.let { view.showFiats(it, true) }
                        view.showSearchBar()
                        view.hideProgressLayout()
                    }

                    override fun onSubscribe(d: Disposable) {
                        view.showProgressLayout()
                        println("Subscribed")
                        compositeDisposable?.add(d)
                    }

                    override fun onError(e: Throwable) {
                        view.hideProgressLayout()
                        view.showError()
                        println("onErrorFiats: ${e.message}")
                    }
                })

    }

    override fun getAllCurrencies() {

        dataManager.getAllCrypto()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map { json -> allCurrencies = json }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<Unit> {
                    override fun onSuccess(na: Unit) {
                        view.showCurrencies(allCurrencies?.data, allCurrencies?.baseImageUrl, allCurrencies?.baseLinkUrl)
                        view.showSearchBar()
                        view.hideProgressLayout()
                    }

                    override fun onSubscribe(d: Disposable) {
                        view.showProgressLayout()
                        println("onSubscribe")
                        compositeDisposable?.add(d)
                    }

                    override fun onError(e: Throwable) {
                        view.hideProgressLayout()
                        view.showError()
                        println("onError: ${e.message}")
                    }
                })
    }

    override fun showCurrencies(filter: String?) {
        allCurrencies?.let { currencies ->
            view.showCurrencies(currencies.data?.filter { it.coinName?.toLowerCase()!!.contains(filter?.toLowerCase()!!) || it.symbol?.toLowerCase()!!.contains(filter.toLowerCase()) }, currencies.baseImageUrl, currencies.baseLinkUrl)
        }
    }

    override fun showFiats(filter: String?) {
        if(filter?.trim() == ""){
            allFiats?.rates?.let { view.showFiats(it, true) }
        }
        else {
            allFiats?.rates?.let { fiats ->
                view.showFiats(fiats.filter { it.fiat?.toLowerCase()?.trim()?.contains(filter?.toLowerCase().toString())!! }, false)
            }
        }
    }

    override fun detachView() {
        compositeDisposable?.dispose()
    }

    companion object {
        val TAG = "SearchPresenter"
    }

}