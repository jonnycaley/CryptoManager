package com.jonnycaley.cryptomanager.ui.search

import com.jonnycaley.cryptomanager.data.model.CryptoCompare.AllCurrencies.Currencies
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.ExchangeRates
import com.jonnycaley.cryptomanager.ui.portfolio.PortfolioFragment
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class SearchPresenter(var dataManager: SearchDataManager, var view: SearchContract.View) : SearchContract.Presenter {

    var compositeDisposable: CompositeDisposable? = null

    var allCurrencies: Currencies? = null

    init {
        this.view.setPresenter(this)
    }

    override fun attachView() {
        if (compositeDisposable == null || (compositeDisposable as CompositeDisposable).isDisposed) {
            compositeDisposable = CompositeDisposable()
        }

        when (view.getSearchType()) {
            PortfolioFragment.CURRENCY_STRING -> {
                getAllCurrencies()
            }
            PortfolioFragment.FIAT_STRING -> {
                getAllFiats()
            }
        }
    }

    private fun getAllFiats() {

        dataManager.getFiats()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<ExchangeRates> {
                    override fun onSuccess(fiats: ExchangeRates) {
                        view.showFiats(fiats)
                    }

                    override fun onSubscribe(d: Disposable) {
                        println("Subscribed")
                        compositeDisposable?.add(d)
                    }

                    override fun onError(e: Throwable) {
                        println("onErrorFiats: ${e.message}")
                    }
                })

    }

    private fun getAllCurrencies() {

        dataManager.getAllCrypto()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map { json -> allCurrencies = json }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<Unit> {
                    override fun onSuccess(na: Unit) {
                        when (allCurrencies) {
                            null -> {
                                //TODO: show something/download automatically
                            }
                            else -> {
                                view.showCurrencies(allCurrencies!!.data, allCurrencies!!.baseImageUrl, allCurrencies!!.baseLinkUrl)
                            }
                        }
                    }

                    override fun onSubscribe(d: Disposable) {
                        println("onSubscribe")
                        compositeDisposable?.add(d)
                    }

                    override fun onError(e: Throwable) {
                        println("onError: ${e.message}")
                    }
                })

    }

    override fun showCurrencies(filter: String?) {
        view.showCurrencies(allCurrencies!!.data?.filter { it.coinName?.toLowerCase()!!.contains(filter?.toLowerCase()!!) || it.symbol?.toLowerCase()!!.contains(filter?.toLowerCase()!!) }, allCurrencies!!.baseImageUrl, allCurrencies!!.baseLinkUrl)
    }

    override fun detachView() {
        compositeDisposable?.dispose()
    }

    companion object {
        val TAG = "SearchPresenter"
    }

}