package com.jonnycaley.cryptomanager.ui.transaction

import io.reactivex.disposables.CompositeDisposable

class TransactionPresenter (var dataManager: TransactionDataManager, var view: TransactionContract.View) : TransactionContract.Presenter{

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
        val TAG = "TransactionPresenter"
    }

}