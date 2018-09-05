package com.jonnycaley.cryptomanager.ui.search

import io.reactivex.disposables.CompositeDisposable

class SearchPresenter (var dataManager: SearchDataManager, var view: SearchContract.View) : SearchContract.Presenter{

    var compositeDisposable: CompositeDisposable? = null

    init {
        this.view.setPresenter(this)
    }

    override fun attachView() {
        if (compositeDisposable == null || (compositeDisposable as CompositeDisposable).isDisposed) {
            compositeDisposable = CompositeDisposable()
        }

    }

    override fun detachView() {
        compositeDisposable?.dispose()
    }

    companion object {
        val TAG = "SearchPresenter"
    }

}