package com.jonnycaley.cryptomanager.ui.currency

import io.reactivex.disposables.CompositeDisposable


class CurrencyPresenter (var dataManager: CurrencyDataManager, var view: CurrencyContract.View) : CurrencyContract.Presenter{

    var compositeDisposable: CompositeDisposable? = null

    init {
        this.view.setPresenter(this)
    }

    override fun attachView() {
        if (compositeDisposable == null || (compositeDisposable as CompositeDisposable).isDisposed) {
            compositeDisposable = CompositeDisposable()
        }
        //start stuff here
//        getCoinDetails()
    }


    override fun detachView() {
        compositeDisposable?.dispose()
    }

    companion object {
        val TAG = "CurrencyPresenter"
    }

}