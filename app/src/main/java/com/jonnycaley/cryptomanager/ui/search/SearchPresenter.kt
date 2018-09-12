package com.jonnycaley.cryptomanager.ui.search

import com.google.gson.Gson
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.AllCurrencies.Currencies
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.ExchangeRates
import com.jonnycaley.cryptomanager.ui.portfolio.PortfolioFragment
import com.jonnycaley.cryptomanager.utils.Constants
import com.jonnycaley.cryptomanager.utils.JsonModifiers
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class SearchPresenter (var dataManager: SearchDataManager, var view: SearchContract.View) : SearchContract.Presenter{

    var compositeDisposable: CompositeDisposable? = null

    var allCurrencies : Currencies? = null

    init {
        this.view.setPresenter(this)
    }

    override fun attachView() {
        if (compositeDisposable == null || (compositeDisposable as CompositeDisposable).isDisposed) {
            compositeDisposable = CompositeDisposable()
        }

        when(view.getSearchType()){
            PortfolioFragment.CURRENCY_STRING -> {
                getAllCurrencies()
            }
            PortfolioFragment.FIAT_STRING -> {
                getAllFiats()
            }
        }
    }

    private fun getAllFiats() {
        if(dataManager.checkConnection()){

            dataManager.getExchangeRateService().getExchangeRates()
                    .map { fiats ->
                        return@map Gson().fromJson(JsonModifiers.jsonToCurrencies(fiats), ExchangeRates::class.java)
                    }
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
                            println("onError: ${e.message}")
                        }
                    })

        } else {

        }
    }

    private fun getAllCurrencies() {

        allCurrencies = Gson().fromJson(dataManager.readStorage(Constants.PAPER_ALL_CRYPTOS), Currencies::class.java)

        when(allCurrencies){
            null -> {
                //TODO: show something/download automatically
            }
            else -> {
                view.showCurrencies(allCurrencies!!.data, allCurrencies!!.baseImageUrl, allCurrencies!!.baseLinkUrl)
            }
        }

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