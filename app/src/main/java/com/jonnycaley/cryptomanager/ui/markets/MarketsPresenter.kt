package com.jonnycaley.cryptomanager.ui.markets

import android.util.Log
import android.widget.SearchView
import com.jakewharton.rxbinding2.widget.RxSearchView
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
import io.reactivex.SingleObserver
import io.reactivex.functions.Function3
import java.util.concurrent.TimeUnit

class MarketsPresenter(var dataManager: MarketsDataManager, var view: MarketsContract.View) : MarketsContract.Presenter {

    var compositeDisposable: CompositeDisposable? = null

    var searchCompositeDisposable: CompositeDisposable? = null

//    var news = ArrayList<Article>()

    init {
        this.view.setPresenter(this)
    }

    override fun attachView() {
        if (compositeDisposable == null || (compositeDisposable as CompositeDisposable).isDisposed) {
            compositeDisposable = CompositeDisposable()
        }

        if (searchCompositeDisposable == null || (searchCompositeDisposable as CompositeDisposable).isDisposed) {
            searchCompositeDisposable = CompositeDisposable()
        }

//        getData()
    }

    var topCurrencies: List<Currency>? = null
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

    override fun loadMoreItems(currencies: ArrayList<Currency>?, moreItemsCount: Int, searchString: CharSequence) {

        if (dataManager.checkConnection()) {

            val newCount = ((currencies?.size ?: 0) + 100)

            dataManager.getCurrencies()
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.computation())
                    .map { allCurrencies ->
                        val temp = allCurrencies.data?.filter { (it.name?.toLowerCase()?.contains(searchString)!!) || (it.symbol?.toLowerCase()?.contains(searchString)!!) }
                        this.topCurrencies = temp?.take(newCount)
                        this.resultsCount = temp?.size!!
                        return@map temp.take(newCount)
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : SingleObserver<List<Currency>?> {
                        override fun onSuccess(t: List<Currency>) {
                            view.showTop100Changes(t, baseFiat!!, resultsCount)
                        }

                        override fun onSubscribe(d: Disposable) {
                            compositeDisposable?.add(d)
                        }

                        override fun onError(e: Throwable) {
                            println("onError: ${e.message}")
                        }
                    })



//            Log.i(TAG, newCount)
//
//            dataManager.getCoinMarketCapService().getTopUSD(newCount)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(Schedulers.computation())
//                    .map { response ->
//                        topCurrencies = response.data
//                    }
//                    .observeOn(Schedulers.io())
//                    .flatMapSingle {
//                        dataManager.getBaseFiat()
//                    }
//                    .observeOn(Schedulers.computation())
//                    .map { baseFiat ->
//                        this.baseFiat = baseFiat
//                    }
////                    TODO: handle onErrors for each request in chain
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(object : Observer<Unit> {
//                        override fun onComplete() {
//                            view.showTop100Changes(topCurrencies, baseFiat!!)
//                            view.hideProgressBarLayout()
//                            view.showContentLayout()
//                        }
//
//                        override fun onNext(t: Unit) {
//                        }
//
//                        override fun onSubscribe(d: Disposable) {
//                            compositeDisposable?.add(d)
//                        }
//
//                        override fun onError(e: Throwable) {
//                            println("onError: ${e.message}")
//                        }
//                    })

        } else {

        }

    }

    var query = ""
    var resultsCount = 100

    private fun setCurrencySearchListener(searchView: SearchView) {

        searchCompositeDisposable?.clear() //clear any previous searches that haven't completed yet

        RxSearchView.queryTextChanges(searchView)
                .subscribeOn(AndroidSchedulers.mainThread())
                .debounce(750, TimeUnit.MILLISECONDS) // stream will go down after 1 second inactivity of user
                .observeOn(Schedulers.io())
                .flatMapSingle {
                    dataManager.getCurrencies()
                }
                .observeOn(Schedulers.computation())
                .map { allCurrencies ->
                    val temp = allCurrencies.data?.filter { (it.name?.toLowerCase()?.contains(searchView.query)!!) || (it.symbol?.toLowerCase()?.contains(searchView.query)!!) }
                    this.topCurrencies = temp?.take(100)
                    this.resultsCount = temp?.size!!
                    return@map temp.take(100)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( object : Observer<List<Currency>?> {
                    override fun onNext(currencies: List<Currency>) {
                        view.showTop100Changes(currencies, baseFiat!!, resultsCount)
                    }

                    override fun onComplete() {

                    }

                    override fun onSubscribe(d: Disposable) {
                        searchCompositeDisposable?.add(d)
                    }

                    override fun onError(e: Throwable) {

                    }


                })

//        dataManager.getCurrencies()
//                .subscribeOn(Schedulers.io())
//                .observeOn(Schedulers.computation())
//                .map { allCurrencies ->
//                    allCurrencies.data?.filter { it.name.toLowerCase().contains() }
//                }
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(object : SingleObserver<List<Currency>?> {
//                    override fun onSuccess(t: List<Currency>) {
//                        view.showTop100Changes(t, baseFiat!!, resultsCount)
//                    }
//
//                    override fun onSubscribe(d: Disposable) {
//                        compositeDisposable?.add(d)
//                    }
//
//                    override fun onError(e: Throwable) {
//                        println("onError: ${e.message}")
//                    }
//                })


    }

    override fun getResultsCounter(): Int {
        return resultsCount
    }

    var baseFiat: Rate? = null

    private fun getData() {

        if (dataManager.checkConnection()) {

            //handle if there is already a string in the search query

            if (topCurrencies != null && baseFiat != null) {
                view.showTop100Changes(topCurrencies, baseFiat!!, resultsCount)
            }

            val topCryptos: Observable<Currencies> = dataManager.getCoinMarketCapService().getTopUSD("5000")
            val marketInfo: Observable<Market> = dataManager.getCoinMarketCapService().getMarketData()
            val baseFiat: Observable<Rate> = dataManager.getBaseFiat().toObservable()

            // if you use Retrofit

            Observable.zip(topCryptos, marketInfo, baseFiat, Function3<Currencies, Market, Rate, Currencies> { res1, res2, res3 ->

                //TODO: THINK I NEED TO PUT THIS BACK IN SOMEHOW BUT INITIALLY LOADS ALL 5000 AS resultsCount IS SET TO IT AT THE START
//                if(resultsCount > 100){
//                    this.topCurrencies = res1.data?.filter { (it.name?.toLowerCase()?.contains(view.getCurrencySearchView().query.toString().toLowerCase().trim())!!) || (it.symbol?.toLowerCase()?.contains(view.getCurrencySearchView().query.toString().toLowerCase().trim())!!) }?.take(resultsCount) //?.subList(0, 100)
//                } else {
                    this.topCurrencies = res1.data?.filter { (it.name?.toLowerCase()?.contains(view.getCurrencySearchView().query.toString().toLowerCase().trim())!!) || (it.symbol?.toLowerCase()?.contains(view.getCurrencySearchView().query.toString().toLowerCase().trim())!!) }?.take(resultsCount) //?.subList(0, 100)
//                }
                this.marketData = res2
                this.baseFiat = res3
                res1
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { res1 ->
                        view.showTop100Changes(topCurrencies, this@MarketsPresenter.baseFiat!!, resultsCount)
                        view.showMarketData(marketData)
                        view.hideProgressBarLayout()
                        view.showContentLayout()
                        setCurrencySearchListener(view.getCurrencySearchView())
                        res1
                    }
                    .observeOn(Schedulers.computation())
                    .flatMapCompletable {
                        allCurrencies ->
                        dataManager.saveCurrencies(allCurrencies)

                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : CompletableObserver {
                        override fun onComplete() {
//                            next()
                        }

                        override fun onSubscribe(d: Disposable) {
                            println("onSubscribe")
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