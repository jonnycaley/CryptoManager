package com.jonnycaley.cryptomanager.ui.news

import io.reactivex.disposables.CompositeDisposable

class NewsPresenter (var dataManager: NewsDataManager, var view: NewsContract.View) : NewsContract.Presenter{

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