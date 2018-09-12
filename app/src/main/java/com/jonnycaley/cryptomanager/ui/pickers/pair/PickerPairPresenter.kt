package com.jonnycaley.cryptomanager.ui.pickers.pair

import io.reactivex.disposables.CompositeDisposable

class PickerPairPresenter (var dataManager: PickerPairDataManager, var view: PickerPairContract.View) : PickerPairContract.Presenter{

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
        val TAG = "PickerPairPresenter"
    }

}