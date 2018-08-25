package com.jonnycaley.cryptomanager.ui.settings

import io.reactivex.disposables.CompositeDisposable

class SettingsPresenter(var dataManager: SettingsDataManager, var view: SettingsContract.View) : SettingsContract.Presenter{

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