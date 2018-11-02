package com.jonnycaley.cryptomanager.ui.crypto.viewpager.general

import android.util.Log
import com.google.gson.Gson
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.GeneralData.Data
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.HistoricalData.HistoricalData
import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.Article
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.Rate
import com.jonnycaley.cryptomanager.utils.JsonModifiers
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.Observer
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
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

//        getCryptoData(view.getSymbol())
        getData()
    }

    override fun getData() {
        clearChartDisposable()
        clearDisposable()
//      these two could be done together with map/flatmap. HOWEVER, due to the fact i need the disposable to be seperate so i can dispose of the chart if a new time frame is clicked im keeping the seperate
        getCurrencyChart(minuteString, view.getSymbol(), conversionUSD, numOfCandlesticks, aggregate1H)
        getCurrencyGeneralData(view.getSymbol())
        getCurrencyNews(view.getSymbol())
    }

    private fun getCurrencyGeneralData(symbol: String) {

        var data : Data? = null

        if(dataManager.checkConnection()){
            dataManager.getCryptoCompareServiceWithScalars().getGeneralData(symbol, "USD")
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { json -> JsonModifiers.jsonToGeneral(json) }
                    .map { json -> data = Gson().fromJson(json, Data::class.java) }
                    .observeOn(Schedulers.io())
                    .flatMapSingle { dataManager.getBaseFiat() }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<Rate> {
                        override fun onComplete() {
                            view.hideRefreshing()
                        }

                        override fun onNext(baseFiat: Rate) {
                            view.showMarketCap(data?.uSD?.mKTCAP, baseFiat)
                            view.showDaysRange(data?.uSD?.lOW24HOUR, data?.uSD?.hIGH24HOUR, data?.uSD?.pRICE, baseFiat)
                            view.showCirculatingSupply(data?.uSD?.sUPPLY)
                            view.show24High(data?.uSD?.hIGH24HOUR, baseFiat)
                            view.show24Low(data?.uSD?.lOW24HOUR, baseFiat)
                            view.show24Change(data?.uSD?.cHANGEPCT24HOUR)
                        }

                        override fun onSubscribe(d: Disposable) {
                            chartDisposable?.add(d)
                        }

                        override fun onError(e: Throwable) {
                            view.showGeneralDataError()
                            Log.i(TAG, "onError: ${e.message}")
                        }
                    })
        } else {

        }
    }

    override fun getCurrencyChart(timeString : String, symbol: String, conversion : String, limit : Int, aggregate : Int) {

        if (dataManager.checkConnection()) {

            var graphData : HistoricalData? = null

            dataManager.getCryptoCompareService().getCurrencyGraph(timeString, symbol, conversion, limit.toString(), aggregate.toString())
                    .map { response -> graphData = response }
                    .flatMapSingle { dataManager.getBaseFiat() }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<Rate> {
                        override fun onComplete() {
                        }

                        override fun onNext(baseFiat: Rate) {

                            if(graphData?.data?.isEmpty()!!) {
                                //TODO: IF THE DATA COMES BACK EMPTY (DGTX) REQUEST
                            } else {

                                view.showPriceChange(graphData?.data?.first()?.open, graphData?.data?.last()?.close, baseFiat)

                                if (isLatestPrice)
                                    view.showCurrentPrice(graphData?.data?.last()?.close, baseFiat)
                                isLatestPrice = false

                                graphData!!.data?.forEach { println(it.close) }

                                view.loadCandlestickChart(graphData!!, timeString, aggregate, baseFiat)
                            }
                        }

                        override fun onSubscribe(d: Disposable) {
                            chartDisposable?.add(d)
                        }

                        override fun onError(e: Throwable) {
                            println("onError: ${e.message}")
                        }
                    })
        } else {

        }
    }

    private fun getCurrencyNews(symbol: String) {

        if (dataManager.checkConnection()) {
            var savedArticles = ArrayList<Article>()

            dataManager.getSavedArticles()
                    .map { articles -> savedArticles = articles }
                    .flatMap { dataManager.getAllCryptos() }
                    .map { currencies -> currencies.data?.filter { it.symbol?.toLowerCase() == symbol.toLowerCase()}?.get(0) }
                    .toObservable()
                    .flatMap { currency -> dataManager.getCryptoControlNewsService().getCurrencyNews(currency.coinName!!.toLowerCase().replace(" ", "-")) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<Array<Article>> {
                        override fun onComplete() {

                        }

                        override fun onNext(articles: Array<Article>) {
                            view.loadCurrencyNews(articles, savedArticles)
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

    override fun saveArticle(topArticle: Article) {
        dataManager.getSavedArticles()
                .map { savedArticles -> savedArticles.add(topArticle)
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

    private fun clearDisposable() {
        compositeDisposable?.clear()
    }

    override fun clearChartDisposable() {
        chartDisposable?.clear()
    }

    override fun detachView() {
        compositeDisposable?.dispose()
        chartDisposable?.dispose()
    }

    companion object {
        val TAG = "GeneralPresenter"

        val minuteString = "minute"
        val hourString = "hour"
        val dayString = "day"

        val conversionUSD = "USD"
        val conversionGBP = "GBP"
        val conversionBTC = "BTC"



        val limit1H = 30
        val aggregate1H = 2
        val timeMeasure1H = minuteString

        val limit1D = 30
        val aggregate1D = 1
        val timeMeasure1D = hourString

        val limit3D = 30
        val aggregate3D = 3
        val timeMeasure3D = hourString

        val limit1W = 30
        val aggregate1W = 6
        val timeMeasure1W = hourString

        val limit1M = 30
        val aggregate1M = 1
        val timeMeasure1M = dayString

        val limit3M = 30
        val aggregate3M = 3
        val timeMeasur3M = dayString

        val limit6M = 30
        val aggregate6M = 6
        val timeMeasure6M = dayString

        val limit1Y = 30
        val aggregate1Y = 12
        val timeMeasure1Y = dayString

        val limitAll = 30
        val aggregateAll = "?"
        val timeMeasureAll = "?"


        val numOfCandlesticks = 30

    }

}