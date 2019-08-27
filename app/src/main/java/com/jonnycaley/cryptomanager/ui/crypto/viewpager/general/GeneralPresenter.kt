package com.jonnycaley.cryptomanager.ui.crypto.viewpager.general

import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Currencies
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.GeneralData.Data
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.HistoricalData.HistoricalData
import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.News.Article
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.Rate
import com.jonnycaley.cryptomanager.data.model.Utils.Chart
import com.jonnycaley.cryptomanager.utils.JsonModifiers
import io.reactivex.CompletableObserver
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers


class GeneralPresenter(var dataManager: GeneralDataManager, var view: GeneralContract.View) : GeneralContract.Presenter {

    var compositeDisposable: CompositeDisposable? = null

    var chartDisposable: CompositeDisposable? = null

    var isLatestPrice = true

    init {
        this.view.setPresenter(this)
    }

    override fun attachView() {
        if (compositeDisposable == null || (compositeDisposable as CompositeDisposable).isDisposed) {
            compositeDisposable = CompositeDisposable()
        }

        if (chartDisposable == null || (chartDisposable as CompositeDisposable).isDisposed) {
            chartDisposable = CompositeDisposable()
        }
        getData()
    }

    /*
    Function starts the get data process if there is internet, if not it shows a no internet layout
    */
    override fun getData() {
        clearChartDisposable()
        clearDisposable()
//      these two could be done together with map/flatmap. HOWEVER, due to the fact i need the disposable to be separate so i can dispose of the chart if a new time frame is clicked im keeping the seperate

        if (dataManager.checkConnection()) {
            view.notifyActivityOfInternet()
            getCurrencyChart(view.getSelectedChartTimeFrame(), view.getSymbol(), conversionUSD)
            getCurrencyGeneralData(view.getSymbol())
            getCurrencyNews(view.getSymbol())
        } else {
            view.hideRefreshing()
            view.showNoInternet()
        }
    }

    /*
    Function gets the currency general data and displays it
    */
    private fun getCurrencyGeneralData(symbol: String) {

        var data: Data? = null

        if (dataManager.checkConnection()) {
            dataManager.getCryptoCompareServiceWithScalars().getGeneralData(symbol, "USD")
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.computation())
                    .map { json ->
                        JsonModifiers.jsonToGeneral(json)
                    }
                    .map { json -> data = Gson().fromJson(json, Data::class.java) }
                    .observeOn(Schedulers.io())
                    .flatMapSingle { dataManager.getBaseFiat() }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<Rate> {
                        override fun onComplete() {
                            view.hideRefreshing()
                        }

                        override fun onNext(baseFiat: Rate) {
                            view.showGlobalData(data, baseFiat)
                        }

                        override fun onSubscribe(d: Disposable) {
                            chartDisposable?.add(d)
                        }

                        override fun onError(e: Throwable) {
                            view.hideRefreshing()
                            Log.i(TAG, "onError1: ${e.message}")
                        }
                    })
        } else {
            view.hideRefreshing()
            view.showNoInternet()
        }
    }

    /*
    Function runs when the onresume of the activity is fired
    Updates the saved articles
    */
    override fun onResume() {

        dataManager.getSavedArticles()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<ArrayList<Article>> {
                    override fun onSuccess(articles: ArrayList<Article>) {
                        view.updateSavedArticles(articles)
                    }

                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable?.add(d)
                    }

                    override fun onError(e: Throwable) {
                        Log.i(TAG, "onError: ${e.message}")
                    }

                })
    }

    /*
    Function gets the currency chart data and loads it
    */
    override fun getCurrencyChart(chart: Chart, symbol: String, conversion: String) {

        if (dataManager.checkConnection()) {

            val getGraph: Observable<HistoricalData> = dataManager.getCryptoCompareService().getCurrencyGraph(chart.measure, symbol, conversion, chart.limit.toString(), chart.aggregate.toString())
            val getBaseFiat: Observable<Rate> = dataManager.getBaseFiat().toObservable()

            var mBaseFiat: Rate? = null

            Observable.zip(getGraph, getBaseFiat, BiFunction<HistoricalData, Rate, HistoricalData> { graphData, baseFiat ->
                mBaseFiat = baseFiat
                graphData
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<HistoricalData> {
                        override fun onComplete() {
                        }

                        override fun onNext(graphData: HistoricalData) {
                            if (graphData.data?.isEmpty() == true) {

                            } else {

                                graphData.data?.first()?.open?.let { open -> graphData.data?.last()?.close?.let { close -> mBaseFiat?.let { baseFiat -> view.showPriceChange(open, close, baseFiat) } } }

                                if (isLatestPrice)
                                    mBaseFiat?.let { baseFiat -> view.showCurrentPrice(graphData.data?.last()?.close, baseFiat) }
                                isLatestPrice = false
                                graphData.let { graphData -> mBaseFiat?.let { baseFiat -> view.loadCandlestickChart(graphData, chart, chart.aggregate, baseFiat) } }
                            }
                        }

                        override fun onSubscribe(d: Disposable) {
                            chartDisposable?.add(d)
                        }

                        override fun onError(e: Throwable) {
                            view.hideRefreshing()
                            println("onError: ${e.message}")
                        }
                    })
        } else {
            view.hideRefreshing()
            view.showNoInternet()
        }
    }

    /*
    Function gets the currency news and displays it
    */
    private fun getCurrencyNews(symbol: String) {

        if (dataManager.checkConnection()) {

            val getSavedArticles: Observable<ArrayList<Article>> = dataManager.getSavedArticles().toObservable()
            val getAllCrypto: Observable<com.jonnycaley.cryptomanager.data.model.CryptoCompare.AllCurrencies.Currencies> = dataManager.getAllCryptos().toObservable()

            var savedArticles = ArrayList<Article>()

            Observable.zip(getSavedArticles, getAllCrypto, BiFunction<ArrayList<Article>, com.jonnycaley.cryptomanager.data.model.CryptoCompare.AllCurrencies.Currencies, String> { articles, allCrypto ->
                savedArticles = articles
                allCrypto.data?.filter { it.symbol?.toLowerCase() == symbol.toLowerCase() }?.get(0)?.coinName?.toLowerCase()?.replace(" ", "-").toString()
            })
                    .flatMap { cryptoName ->
                        if(cryptoName == "xrp"){
                            dataManager.getCryptoControlNewsService().getCurrencyNews("ripple")
                        } else
                            dataManager.getCryptoControlNewsService().getCurrencyNews(cryptoName) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<Array<Article>> {
                        override fun onComplete() {
                        }

                        override fun onNext(articles: Array<Article>) {
                            if(articles.isEmpty())
                                view.showNoNews()
                            else
                                view.loadCurrencyNews(articles.filter { it.originalImageUrl != null }.toTypedArray(), savedArticles)
                        }

                        override fun onSubscribe(d: Disposable) {
                            chartDisposable?.add(d)
                        }

                        override fun onError(e: Throwable) {
                            view.showNoNews()
                            view.hideRefreshing()
                            println("onError: ${e.message}") //dont show an error cause this can happen and is therefore not worth notifying 'no news obtained'
                        }
                    })
        } else {
            view.hideRefreshing()
            view.showNoInternet()
        }
    }

    /*
    Function saves an article to storage
    */
    override fun saveArticle(topArticle: Article) {
        dataManager.getSavedArticles()
                .map { savedArticles ->
                    savedArticles.add(topArticle)
                    return@map savedArticles
                }
                .flatMapCompletable { savedArticles -> dataManager.saveArticles(savedArticles) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
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

    /*
    Function removes an article from storage
    */
    override fun removeArticle(topArticle: Article) {

        dataManager.getSavedArticles()
                .map { articles -> return@map articles.filter { it.url != topArticle.url } }
                .flatMapCompletable { savedArticles -> dataManager.saveArticles(ArrayList(savedArticles)) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
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

    /*
    Function clears the disposable
    */
    private fun clearDisposable() {
        compositeDisposable?.clear()
    }

    /*
    Function clears the chart disposable
    */
    override fun clearChartDisposable() {
        chartDisposable?.clear()
    }

    /*
    Function disposes the disposables when the activity finishes to prevent memory leaks
    */
    override fun detachView() {
        compositeDisposable?.dispose()
        chartDisposable?.dispose()
    }

    companion object {
        val TAG = "GeneralPresenter"

        val conversionUSD = "USD"
    }
}