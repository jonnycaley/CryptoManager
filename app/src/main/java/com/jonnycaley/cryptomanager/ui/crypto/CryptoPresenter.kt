package com.jonnycaley.cryptomanager.ui.crypto

import io.reactivex.disposables.CompositeDisposable


class CryptoPresenter (var dataManager: CryptoDataManager, var view: CryptoContract.View) : CryptoContract.Presenter{

    var compositeDisposable: CompositeDisposable? = null

    init {
        this.view.setPresenter(this)
    }

    override fun attachView() {
        if (compositeDisposable == null || (compositeDisposable as CompositeDisposable).isDisposed) {
            compositeDisposable = CompositeDisposable()
        }

        getCoinDetails()
    }

    private fun getCoinDetails() {
        if(dataManager.checkConnection()){



        } else {

        }
    }


    override fun detachView() {
        compositeDisposable?.dispose()
    }

    companion object {
        val TAG = "CryptoPresenter"
    }

}