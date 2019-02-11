package com.jonnycaley.cryptomanager.ui.markets

import android.util.Log
import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Currencies
import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Currency
import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.Article
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.Rate
import io.reactivex.CompletableObserver
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlin.collections.ArrayList
import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Market.Market
import io.reactivex.functions.Function3


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
//        getData()
    }

    var topCurrencies: Currencies? = null
    var marketData: Market? = null

    override fun refresh() {
//        if(topcurrencies != null && baseFiat != null){
//            view.showTop100Changes(topcurrencies?.data, baseFiat!!)
//        }
        getData()
    }

    override fun onResume() {
        Log.i(TAG, "onResume")
        refresh()
    }

    override fun saveArticle(article: Article) {

        dataManager.getSavedArticles()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map { savedArticles ->
                    if (savedArticles.none { it.url == article.url })
                        savedArticles.add(article)
                    return@map savedArticles
                }
                .observeOn(Schedulers.io())
                .flatMapCompletable { savedArticles -> dataManager.saveArticles(savedArticles) }
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
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map { articles -> articles.filter { it.url != article.url } }
                .observeOn(Schedulers.io())
                .flatMapCompletable { savedArticles -> dataManager.saveArticles(ArrayList(savedArticles)) }
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

    override fun loadMoreItems(currencies: ArrayList<Currency>?) {

        if (dataManager.checkConnection()) {

            val newCount = ((currencies?.size ?: 0) + 100).toString()

            Log.i(TAG, newCount)

            dataManager.getCoinMarketCapService().getTopUSD(newCount)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.computation())
                    .map { response ->
                        topCurrencies = response
                    }
                    .observeOn(Schedulers.io())
                    .flatMapSingle {
                        dataManager.getBaseFiat()
                    }
                    .observeOn(Schedulers.computation())
                    .map { baseFiat ->
                        this.baseFiat = baseFiat
                    }
//                    TODO: handle onErrors for each request in chain
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<Unit> {
                        override fun onComplete() {
                            view.showTop100Changes(topCurrencies?.data, baseFiat!!)
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

    var baseFiat: Rate? = null

    private fun getData() {

        if (dataManager.checkConnection()) {

            if (topCurrencies != null && baseFiat != null) {
                view.showTop100Changes(topCurrencies?.data, baseFiat!!)
            }

            //THIS IS A LOT QUICKER WO

            val topCryptos: Observable<Currencies> = dataManager.getCoinMarketCapService().getTopUSD(topCurrencies?.data?.size?.toString() ?: 100.toString())
            val marketInfo: Observable<Market> = dataManager.getCoinMarketCapService().getMarketData()
            val baseFiat: Observable<Rate> = dataManager.getBaseFiat().toObservable()

            // if you use Retrofit

            Observable.zip(topCryptos, marketInfo, baseFiat, Function3<Currencies, Market, Rate, Unit> { res1, res2, res3 ->
                this.topCurrencies = res1
                this.marketData = res2
                this.baseFiat = res3
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map {
                        view.showTop100Changes(topCurrencies?.data, this@MarketsPresenter.baseFiat!!)
                        view.showMarketData(marketData)
                    }
                    .subscribe(object : Observer<Unit> {
                        override fun onComplete() {
                            view.hideProgressBarLayout()
                            view.showContentLayout()
//                            next()
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

    private fun old() {  //CHAINING WHEN YOU DONT HAVE TO WILL SLOW DOWN OVERALL REQUEST SPEED
//        dataManager.getCoinMarketCapService().getTopUSD(topCurrencies?.data?.size?.toString() ?: 100.toString())
//                .subscribeOn(Schedulers.io())
//                .observeOn(Schedulers.computation())
//                .map { response ->
//                    topCurrencies = response
//                }
//                .observeOn(Schedulers.io())
//                .flatMapSingle {
//                    dataManager.getBaseFiat()
//                }
//                .observeOn(Schedulers.computation())
//                .map { baseFiat ->
//                    this.baseFiat = baseFiat
//                }
//                .observeOn(Schedulers.io())
//                .flatMap {
//                    dataManager.getCoinMarketCapService().getMarketData()
//                }
//                .observeOn(Schedulers.computation())
//                .map { data ->
//                    this.marketData = data
//                }
////                    TODO: handle onErrors for each request in chain
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(object : Observer<Unit> {
//                    override fun onComplete() {
//                        view.showTop100Changes(topCurrencies?.data, baseFiat!!)
//                        view.hideProgressBarLayout()
//                        view.showContentLayout()
//                        Log.i(TAG, "TimeDone2")
//                    }
//
//                    override fun onNext(t: Unit) {
//                    }
//
//                    override fun onSubscribe(d: Disposable) {
//                        compositeDisposable?.add(d)
//                        Log.i(TAG, "TimeNow2")
//                    }
//
//                    override fun onError(e: Throwable) {
//                        println("onError: ${e.message}")
//                    }
//                })
    }


    //TODO: CLEAN THIS UP

    override fun detachView() {
        compositeDisposable?.dispose()
    }

    companion object {
        val TAG = "MarketsPresenter"
    }

}