package com.jonnycaley.cryptomanager.ui.pickers.exchange

import com.google.gson.Gson
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.Exchanges.Exchanges
import com.jonnycaley.cryptomanager.utils.Constants
import io.reactivex.disposables.CompositeDisposable

class PickerExchangePresenter (var dataManager: PickerExchangeDataManager, var view: PickerExchangeContract.View) : PickerExchangeContract.Presenter{

    var compositeDisposable: CompositeDisposable? = null

    init {
        this.view.setPresenter(this)
    }

    override fun attachView() {
        if (compositeDisposable == null || (compositeDisposable as CompositeDisposable).isDisposed) {
            compositeDisposable = CompositeDisposable()
        }

        getExchanges()
    }

    private fun getExchanges() {
        val json = dataManager.readExchanges(Constants.PAPER_ALL_EXCHANGES)

        val gson = Gson().fromJson(json, Exchanges::class.java)

        view.showExchanges(gson)
    }

    override fun detachView() {
        compositeDisposable?.dispose()
    }

    companion object {
        val TAG = "PickerExchangePresenter"
    }

}