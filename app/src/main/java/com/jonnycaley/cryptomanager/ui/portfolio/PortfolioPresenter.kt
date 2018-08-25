package com.jonnycaley.cryptomanager.ui.portfolio

import io.reactivex.disposables.CompositeDisposable

class PortfolioPresenter(var dataManager: PortfolioDataManager, var view: PortfolioContract.View) : PortfolioContract.Presenter{

    var compositeDisposable: CompositeDisposable? = null

    init {
        this.view.setPresenter(this)
    }

    override fun attachView() {
        if (compositeDisposable == null || (compositeDisposable as CompositeDisposable).isDisposed) {
            compositeDisposable = CompositeDisposable()
        }
        //start stuff here
    }

    override fun detachView() {
        compositeDisposable?.dispose()
    }

}