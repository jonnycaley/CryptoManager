package com.jonnycaley.cryptomanager.ui.transactions.fiatTransaction

import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class FiatTransactionPresenter (var dataManager: FiatTransactionDataManager, var view: FiatTransactionContract.View) : FiatTransactionContract.Presenter{

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

            dataManager.getExchangeRateService().getExchangeRates()
                    .map { response ->
                        response.exchanges?.forEach { println(it.name) }
                    }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : SingleObserver<Unit?> {
                        override fun onSuccess(currencies: Unit) {
                        }

                        override fun onSubscribe(d: Disposable) {
                            println("Subscribed")
                            compositeDisposable?.add(d)
                        }

                        override fun onError(e: Throwable) {
                            println("onError: ${e.message}")
                        }
                    })

        } else {

        }
    }


    override fun detachView() {
        compositeDisposable?.dispose()
    }

    companion object {
        val TAG = "FiatTransactionPresenter"
    }

}