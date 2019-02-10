package com.jonnycaley.cryptomanager.ui.markets

import android.util.Log
import android.widget.SearchView
import com.jakewharton.rxbinding2.widget.RxSearchView
import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Currencies
import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.Article
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.Rate
import io.reactivex.CompletableObserver
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit


class MarketsPresenter(var dataManager: MarketsDataManager, var view: MarketsContract.View) : MarketsContract.Presenter {

    var compositeDisposable: CompositeDisposable? = null

//    var news = ArrayList<Article>()

    init {
        this.view.setPresenter(this)
    }

    override fun attachView() {
        if (compositeDisposable == null || (compositeDisposable as CompositeDisposable).isDisposed) {
            compositeDisposable = CompositeDisposable()
        }
//        setCurrencySearchListener(view.getCurrencySearchView())
        getData()
    }

    var top100 : Currencies? = null

    override fun refresh() {
//        if(top100 != null && baseFiat != null){
//            view.showTop100Changes(top100?.data, baseFiat!!)
//        }
        getData()
    }

    override fun onResume() {
        Log.i(TAG, "onResume")
        refresh()
    }

    override fun saveArticle(article: Article) {

        dataManager.getSavedArticles()
                .map { savedArticles ->
                    if (savedArticles.none { it.url == article.url })
                        savedArticles.add(article)
                    return@map savedArticles
                }
                .flatMapCompletable { savedArticles -> dataManager.saveArticles(savedArticles) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {

                    }

                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable?.add(d)
                    }

                    override fun onError(e: Throwable) {
                        println("onError: ${e.message}")
                    }
                })

    }

    override fun removeArticle(article: Article) {

        dataManager.getSavedArticles()
                .map { articles -> articles.filter { it.url != article.url } }
                .flatMapCompletable { savedArticles -> dataManager.saveArticles(ArrayList(savedArticles)) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {

                    }

                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable?.add(d)
                    }

                    override fun onError(e: Throwable) {
                        println("onError: ${e.message}")
                    }
                })
    }

//    private fun setCurrencySearchListener(searchView: SearchView) {
//
//        RxSearchView.queryTextChanges(searchView)
//                .debounce(750, TimeUnit.MILLISECONDS) // stream will go down after 1 second inactivity of user
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe {
//
//                    if (dataManager.checkConnection()) {
//                        println(searchView.query)
//                    } else {
//
//                    }
//
//                }
//
//    }

    var baseFiat : Rate? = null

    private fun getData() {

        if (dataManager.checkConnection()) {

            dataManager.getCoinMarketCapService().getTop100USD()
                    .map { response ->
                        top100 = response
                    }
                    .flatMapSingle {
                        dataManager.getBaseFiat()
                    }
                    .map { baseFiat ->
                        this.baseFiat = baseFiat
                    }
//                    TODO: handle onErrors for each request in chain
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<Unit> {
                        override fun onComplete() {
                            view.showTop100Changes(top100?.data, baseFiat!!)
                            view.hideProgressBarLayout()
                            view.showContentLayout()
                        }

                        override fun onNext(t: Unit) {
                        }

                        override fun onSubscribe(d: Disposable) {
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
        val TAG = "MarketsPresenter"
    }

}