package com.jonnycaley.cryptomanager.ui.currency.viewpager.general

import com.jonnycaley.cryptomanager.data.model.CryptoCompare.HistoricalData.Data
import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.News
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class GeneralPresenter(var dataManager: GeneralDataManager, var view: GeneralContract.View) : GeneralContract.Presenter {

    var compositeDisposable: CompositeDisposable? = null

    var chartDisposable: CompositeDisposable? = null

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

    override fun getData() {
        clearChartDisposable()
        clearDisposable()

//      these two could be done together with map/flatmap. HOWEVER, due to the fact i need the disposable to be seperate so i can dispose of the chart if a new time frame is clicked im keeping the seperate
        getCurrencyChart(minuteString, view.getSymbol(), conversionUSD, numOfCandlesticks, aggregate1H)
        getCurrencyNews(view.getName())
    }

    override fun getCurrencyChart(timeString : String, symbol: String, conversion : String, limit : Int, aggregate : Int) {

        if (dataManager.checkConnection()) {

            dataManager.getCryptoCompareService().getCurrencyGraph(timeString, symbol, conversion, limit.toString(), aggregate.toString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : SingleObserver<Data> {
                        override fun onSuccess(response: Data) {
                            view.loadCandlestickChart(response, timeString, aggregate)
                        }

                        override fun onSubscribe(d: Disposable) {
                            chartDisposable?.add(d)
                        }

                        override fun onError(e: Throwable) {

                        }
                    })
        } else {

        }
    }

    private fun getCurrencyNews(name: String) {

        if (dataManager.checkConnection()) {

            dataManager.getCryptoControlNewsService().getCurrencyNews(name)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : SingleObserver<Array<News>> {
                        override fun onSuccess(response: Array<News>) {
                            view.loadCurrencyNews(response)
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