package com.jonnycaley.cryptomanager.ui.home

import io.reactivex.disposables.CompositeDisposable
import android.os.StrictMode
import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Currencies
import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Currency
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class HomePresenter (var dataManager: HomeDataManager, var view: HomeContract.View) : HomeContract.Presenter{

    var compositeDisposable: CompositeDisposable? = null

    init {
        this.view.setPresenter(this)
    }

    override fun attachView() {
        if (compositeDisposable == null || (compositeDisposable as CompositeDisposable).isDisposed) {
            compositeDisposable = CompositeDisposable()
        }
        getNews()
    }


    private fun getNews() {

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        if(dataManager.checkConnection()){
            dataManager.getCryptoControlService().getTopNews("10")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { news ->
                        view.showNews(news)
                    }
                    .flatMap {
                        dataManager.getCoinMarketCapService().getTop100()
                    }
                    .map { currencies ->
                        view.showTop100Changes(currencies.data?.sortedBy { it.quote?.uSD?.percentChange24h }?.asReversed())
                    }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : SingleObserver<Unit> {
                        override fun onSuccess(currencies: Unit) {
                            view.hideProgressBar()
                            view.showScrollLayout()
                        }

                        override fun onSubscribe(d: Disposable) {
                            println("Subscribed")
                            view.showProgressBar()
                            compositeDisposable?.add(d)
                        }

                        override fun onError(e: Throwable) {
                            println("onError: ${e.message}")
                        }
                    })

        } else {
//            view.showNoInternet()
        }
    }

    override fun detachView() {
        compositeDisposable?.dispose()
    }

    companion object {
        val TAG = "HomePresenter"
    }

}