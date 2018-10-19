package com.jonnycaley.cryptomanager.ui.markets

import android.widget.SearchView
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import com.jakewharton.rxbinding2.widget.RxSearchView
import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Currency
import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.Article
import io.reactivex.MaybeSource
import java.util.*
import java.util.concurrent.TimeUnit


class MarketsPresenter(var dataManager: MarketsDataManager, var view: MarketsContract.View) : MarketsContract.Presenter {

    var compositeDisposable: CompositeDisposable? = null

    var news = ArrayList<Article>()

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

    override fun refresh() {
        if(news.isEmpty()){
            getData()
        } else {
            dataManager.getSavedArticles()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { savedArticles ->
                        view.showLatestArticles(news, savedArticles)
                    }
//                    .filter { dataManager.checkConnection() }
                    .flatMap {
                        dataManager.getCoinMarketCapService().getTop100USD()
                    }
                    .map { t100 ->
                        view.showTop100Changes(t100.data, dataManager.getBaseFiat())
                    }
                    //TODO: check connection and threading needing and skips frames :(
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : SingleObserver<Unit> {
                        override fun onSuccess(savedArticles: Unit) {

                        }

                        override fun onSubscribe(d: Disposable) {
                            compositeDisposable?.add(d)
                        }

                        override fun onError(e: Throwable) {
                            println("onError: ${e.message}")
                        }

                    })
        }

    }

    override fun onResume() {
        refresh()
    }

    override fun saveArticle(topArticle: Article) {

        dataManager.getSavedArticles()
                .map { savedArticles ->
                    if (savedArticles.none { it.url == topArticle.url })
                        savedArticles.add(topArticle)
                    return@map savedArticles
                }
                .map { savedArticles -> dataManager.saveArticles(savedArticles) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<Unit> {
                    override fun onSuccess(currencies: Unit) {
                    }

                    override fun onSubscribe(d: Disposable) {
                        println("onSubscribe")
                        compositeDisposable?.add(d)
                    }

                    override fun onError(e: Throwable) {
                        println("onError: ${e.message}")
                    }
                })

    }

    override fun removeArticle(topArticle: Article) {

        dataManager.getSavedArticles()
                .map { articles -> return@map articles.filter { it.url != topArticle.url } }
                .map { savedArticles -> dataManager.saveArticles(ArrayList(savedArticles)) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<Unit> {
                    override fun onSuccess(currencies: Unit) {
                    }

                    override fun onSubscribe(d: Disposable) {
                        println("onSubscribe")
                        compositeDisposable?.add(d)
                    }

                    override fun onError(e: Throwable) {
                        println("onError: ${e.message}")
                    }
                })
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
                    .map { response ->
                        view.showTop100Changes(response.data, dataManager.getBaseFiat())
                    }
                    .map {
                        dataManager.getBaseFiat()
                    }
                    .flatMap {
                        dataManager.getCryptoControlService().getLatestNews("10")
                    }
                    .map { news ->
                        news.forEach { this.news.add(it) }
                    }
                    .flatMap {
                        dataManager.getSavedArticles()
                    }
                    .map { savedArticles ->
                        view.showLatestArticles(this.news, savedArticles)
                    }
                    //TODO: handle onErrors for each request in chain
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