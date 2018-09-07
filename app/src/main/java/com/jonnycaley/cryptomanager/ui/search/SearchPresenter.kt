package com.jonnycaley.cryptomanager.ui.search

import com.google.gson.Gson
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.AllCurrencies.Currencies
import com.jonnycaley.cryptomanager.utils.Constants
import io.reactivex.disposables.CompositeDisposable

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

        getAllCurrencies()
    }

    private fun getAllCurrencies() {

        allCurrencies = Gson().fromJson(dataManager.readStorage(Constants.PAPER_ALL_CURRENCIES), Currencies::class.java)

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