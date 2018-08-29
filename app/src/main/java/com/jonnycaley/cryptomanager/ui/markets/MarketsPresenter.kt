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

            dataManager.getCoinMarketCapService().getTop100()
                    .map { view.showTop100Changes(it.data)
                    }
                    .flatMap { dataManager.getCryptoControlService().getTopNews("10")
                    }
                    .map {
                        view.showLatestArticles(it)
                    }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : SingleObserver<Unit> {
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

}