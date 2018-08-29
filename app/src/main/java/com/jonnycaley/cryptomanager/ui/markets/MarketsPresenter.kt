package com.jonnycaley.cryptomanager.ui.markets

import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Currencies
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class MarketsPresenter(var dataManager: MarketsDataManager, var view: MarketsContract.View) : MarketsContract.Presenter{

    var compositeDisposable: CompositeDisposable? = null

    init {
        this.view.setPresenter(this)
    }

    override fun attachView() {
        if (compositeDisposable == null || (compositeDisposable as CompositeDisposable).isDisposed) {
            compositeDisposable = CompositeDisposable()
        }

        if(dataManager.checkConnection()){

            dataManager.getCoinMarketCapService().getTop100("0")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { currencies ->
                        view.showTop100Changes(currencies)
                        return@map currencies
                    }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : SingleObserver<Currencies> {
                        override fun onSuccess(currencies: Currencies) {
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

}