package com.jonnycaley.cryptomanager.ui.markets

import android.widget.SearchView
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import com.jakewharton.rxbinding2.widget.RxSearchView
import io.reactivex.annotations.NonNull
import io.reactivex.functions.Consumer
import java.util.concurrent.TimeUnit


class MarketsPresenter(var dataManager: MarketsDataManager, var view: MarketsContract.View) : MarketsContract.Presenter {

    var compositeDisposable: CompositeDisposable? = null

    init {
        this.view.setPresenter(this)
    }

    override fun attachView() {
        if (compositeDisposable == null || (compositeDisposable as CompositeDisposable).isDisposed) {
            compositeDisposable = CompositeDisposable()
        }
        setCurrencySearchListener(view.getCurrencySearchView())
        getData()
    }

    private fun setCurrencySearchListener(searchView: SearchView) {

        RxSearchView.queryTextChanges(searchView)
                .debounce(750, TimeUnit.MILLISECONDS) // stream will go down after 1 second inactivity of user
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {

                    if (dataManager.checkConnection()) {
                        println(searchView.query)
                    } else {

                    }

                }

    }

    private fun getData() {

        if (dataManager.checkConnection()) {

            dataManager.getCoinMarketCapService().getTop100USD()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map {
                        view.showTop100Changes(it.data)
                    }
                    .flatMap {
                        dataManager.getCryptoControlService().getLatestNews("10")
                    }
                    .map {
                        view.showLatestArticles(it)
                    }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : SingleObserver<Unit> {
                        override fun onSuccess(currencies: Unit) {
                            view.hideProgressBarLayout()
                            view.showContentLayout()
                        }

                        override fun onSubscribe(d: Disposable) {
                            view.showProgressBarLayout()
                            view.hideContentLayout()
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