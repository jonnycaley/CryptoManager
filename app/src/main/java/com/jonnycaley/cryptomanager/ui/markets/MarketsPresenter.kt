package com.jonnycaley.cryptomanager.ui.markets

import android.util.Log
import android.widget.SearchView
import com.jakewharton.rxbinding2.widget.RxSearchView
import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Currencies
import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Currency
import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.News.Article
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.Rate
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlin.collections.ArrayList
import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Market.Market
import io.reactivex.CompletableObserver
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.SingleObserver
import io.reactivex.functions.Function3
import java.util.concurrent.TimeUnit

class MarketsPresenter(var dataManager: MarketsDataManager, var view: MarketsContract.View) : MarketsContract.Presenter {

    var compositeDisposable: CompositeDisposable? = null

    var moreItemsDisposable: CompositeDisposable? = null

    var searchCompositeDisposable: CompositeDisposable? = null

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

        if (moreItemsDisposable == null || (moreItemsDisposable as CompositeDisposable).isDisposed) {
            moreItemsDisposable = CompositeDisposable()
        }

        getOnlineData()
    }

    var topCurrencies : ArrayList<Currency>? = null
    var marketData : Market? = null

    /*
    Function executes when on refresh
    */
    override fun refresh() {
//        if(topcurrencies != null && baseRate != null){
//            view.showTop8Changes(topcurrencies?.data, baseRate!!)
//        }
        getOfflineData()
    }

    /*
    Function executes on resume
    */
    override fun onResume() {
        Log.i(TAG, "onResume")
        dataManager.getBaseFiat()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<Rate> {
                    override fun onSuccess(rate: Rate) {
                        if(baseFiat != rate)
                            baseFiat = rate

                        marketData?.let { marketData -> baseFiat?.let { baseFiat -> view.showMarketData(marketData, baseFiat)  }} //show market data

                        topCurrencies?.let { topCurrencies -> baseFiat?.let { baseFiat -> view.showTop100Changes(topCurrencies, baseFiat, resultsCount) } } //show top 100 data
                    }

                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable?.add(d) //add disposable
                    }

                    override fun onError(e: Throwable) {
                        println("onError: ${e.message}")
                    }
                })
//        refresh()
    }

    /*
    Function gets online markets data, shows it and saves it to storage
    */
    override fun getOnlineData() {

        if (dataManager.checkConnection()) {

            Observable.zip(GETtopCryptos, GETmarketInfo, GETbaseFiat, Function3<Currencies, Market, Rate, Currencies> { res1, res2, res3 ->
                //TODO: THINK I NEED TO PUT THIS BACK IN SOMEHOW BUT INITIALLY LOADS ALL 5000 AS resultsCount IS SET TO IT AT THE START
                this.topCurrencies = ArrayList(sort(res1.data?.filter { (it.name?.toLowerCase()?.contains(view.getCurrencySearchView().query.toString().toLowerCase().trim()) ?: false) || (it.symbol?.toLowerCase()?.contains(view.getCurrencySearchView().query.toString().toLowerCase().trim()) ?: false) }?.take(view.getCurrenciesAdapterCount()))) //?.subList(0, 100)
                this.marketData = res2
                this.baseFiat = res3
                res1
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { res1 ->
                        view.showTop100Changes(this.topCurrencies ?: ArrayList(), this.baseFiat ?: Rate(), resultsCount) //show top 100
                        marketData?.let { baseFiat?.let { it1 -> view.showMarketData(it, it1) } }
                        view.hideProgressBarLayout()
                        view.showContentLayout()
                        res1
                    }
                    .observeOn(Schedulers.io())
                    .flatMapCompletable { allCurrencies ->
                        dataManager.saveCurrencies(ArrayList(allCurrencies.data ?: ArrayList())) //save currencies
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : CompletableObserver {
                        override fun onComplete() {

                            saveMarketInfo()
//                            println("FinallyLMAO")
//                            setCurrencySearchListener(view.getCurrencySearchView())
//                            view.hideNoInternetLayout()
                        }

                        override fun onSubscribe(d: Disposable) {
                            Log.i(TAG, "Loading online data subscribed")
                            compositeDisposable?.add(d)
                        }

                        override fun onError(e: Throwable) {
                            println("onError1: ${e.message}")
                            view.hideNoInternetLayout()
                        }
                    })
        } else {
            getOfflineData()
        }
    }

    /*
    Function saves the market data info
    */
    private fun saveMarketInfo() {
        this.marketData?.let {
            dataManager.saveMarketInfo(it)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        setCurrencySearchListener(view.getCurrencySearchView()) //set currencies listener
                        view.hideNoInternetLayout()
                    }

                    override fun onSubscribe(d: Disposable) {
                        Log.i(TAG, "Loading online data subscribed")
                        compositeDisposable?.add(d)
                    }

                    override fun onError(e: Throwable) {
                        println("onError1: ${e.message}")
                        view.hideNoInternetLayout()
                    }
                })
        }
    }

    /*
    Function loads more items of the list when it reaches the bottom
    */
    override fun loadMoreItems(currencies: ArrayList<Currency>?, moreItemsCount: Int, searchString: CharSequence) {

        if (dataManager.checkConnection()) {

            val newCount = ((currencies?.size ?: 0) + 100)

            dataManager.getCurrencies()
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.computation())
                    .map { allCurrencies ->
                        val temp = allCurrencies.filter { (it.name?.toLowerCase()?.contains(searchString) ?: false) || (it.symbol?.toLowerCase()?.contains(searchString) ?: false) } //get filtered currencies
                        this.topCurrencies = sort(temp.take(newCount))
                        this.resultsCount = temp.size
                        return@map this.topCurrencies
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : SingleObserver<ArrayList<Currency>?> {
                        override fun onSuccess(t100: ArrayList<Currency>) {
                            Log.i(TAG, t100.size.toString())
                            view.showTop100Changes(t100, baseFiat ?: Rate(), resultsCount) //show top 100
                            view.stopRefreshing()
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
//                        dataManager.getBaseRate()
//                    }
//                    .observeOn(Schedulers.computation())
//                    .map { baseRate ->
//                        this.baseRate = baseRate
//                    }
////                    TODO: handle onErrors for each request in chain
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(object : Observer<Unit> {
//                        override fun onComplete() {
//                            view.showTop8Changes(topCurrencies, baseRate!!)
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
            getOfflineData()
        }
    }

    var resultsCount = 100

    /*
    Function sets the search view listener with a 0.75 second delay listener
    */
    private fun setCurrencySearchListener(searchView: SearchView) {

        Log.i(TAG, "setCurrencySearchListener")

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
                    val temp = allCurrencies.filter { (it.name?.toLowerCase()?.contains(searchView.query) ?: false) || (it.symbol?.toLowerCase()?.contains(searchView.query) ?: false) }
                    this.topCurrencies = sort(temp.take(100))
                    this.resultsCount = temp.size
                    return@map this.topCurrencies
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<ArrayList<Currency>?> {
                    override fun onNext(currencies: ArrayList<Currency>) {
                        view.showTop100Changes(currencies, baseFiat ?: Rate(), resultsCount)
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
//                        view.showTop8Changes(t, baseRate!!, resultsCount)
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


    val GETtopCryptos: Observable<Currencies> = dataManager.getCoinMarketCapService().getTopUSD("5000")
    val GETmarketInfo: Observable<Market> = dataManager.getCoinMarketCapService().getMarketData()
    val GETbaseFiat: Observable<Rate> = dataManager.getBaseFiat().toObservable()

    var baseFiat: Rate? = null

    /*
    Function gets offline data for display
    */
    private fun getOfflineData() {
//        view.showTop8Changes(topCurrencies, baseRate, resultsCount)

//        if (topCurrencies != null && baseRate != null) {
//            Log.i(TAG, "Se3")
//            view.showTop8Changes(topCurrencies, baseRate!!, resultsCount)
//            Log.i(TAG, "offline loaded from activity")

//        } else {

//            getOnlineData()
            Observable.zip(dataManager.getCurrencies().toObservable(), dataManager.getMarketInfo().toObservable(), GETbaseFiat, Function3<List<Currency>, Market, Rate, List<Currency>> { res1, res2, res3 ->
                this.marketData = res2
                this.baseFiat = res3
                Log.i("ThreadShouldBe io", Thread.currentThread().name)
                res1
            })
                    .map { res ->
                        this.topCurrencies = sort(res.filter { (it.name?.toLowerCase()?.contains(view.getCurrencySearchView().query.toString().toLowerCase().trim()) ?: false) || (it.symbol?.toLowerCase()?.contains(view.getCurrencySearchView().query.toString().toLowerCase().trim()) ?: false) }.take(view.getCurrenciesAdapterCount()))//?.subList(0, 100)
                    }
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<Unit> {
                        override fun onComplete() {
                            Log.i(TAG, "offline loaded from storage")
                            view.stopRefreshing()
//                            getOnlineData()
                        }

                        override fun onNext(currencies: Unit) {
                            if (topCurrencies?.isNotEmpty() == true) {
                                Log.i(TAG, "Se4")
                                view.showTop100Changes(topCurrencies ?: ArrayList(), baseFiat ?: Rate(), resultsCount)
                                marketData?.let { baseFiat?.let { it1 -> view.showMarketData(it, it1) } }
                                view.hideProgressBarLayout()
                                view.showContentLayout()
                                setCurrencySearchListener(view.getCurrencySearchView())
                            } else {
                                view.showNoInternetLayout()
                            }
                        }

                        override fun onSubscribe(d: Disposable) {
                            println("onSubscribe")
                            compositeDisposable?.add(d)
                        }

                        override fun onError(e: Throwable) {
                            println("onError2: ${e.message}")
                            view.hideProgressBarLayout()
                        }
                    })
//        }

    }

    /*
    Function handles the sorting
    */
    private fun sort(topCurrencies: List<Currency>?): ArrayList<Currency> {
        when (view.getSort()) {
            MarketsFragment.FILTER_RANK_DOWN -> {
                return ArrayList(topCurrencies?.sortedBy { it.cmcRank } ?: ArrayList())
            }
            MarketsFragment.FILTER_RANK_UP -> {
                return ArrayList(topCurrencies?.sortedBy { it.cmcRank }?.asReversed() ?: ArrayList())
            }
            MarketsFragment.FILTER_NAME_DOWN -> {
                return ArrayList(topCurrencies?.sortedBy { it.name } ?: ArrayList())
            }
            MarketsFragment.FILTER_NAME_UP -> {
                return ArrayList(topCurrencies?.sortedBy { it.name }?.asReversed() ?: ArrayList())
            }
            MarketsFragment.FILTER_PRICE_DOWN -> {
                return ArrayList(topCurrencies?.sortedBy { it.quote?.uSD?.price }?.asReversed() ?: ArrayList())
            }
            MarketsFragment.FILTER_PRICE_UP -> {
                return ArrayList(topCurrencies?.sortedBy { it.quote?.uSD?.price } ?: ArrayList())
            }
            MarketsFragment.FILTER_CHANGE_DOWN -> {
                return ArrayList(topCurrencies?.sortedBy { it.quote?.uSD?.percentChange24h }?.asReversed() ?: ArrayList())
            }
            MarketsFragment.FILTER_CHANGE_UP -> {
                return ArrayList(topCurrencies?.sortedBy { it.quote?.uSD?.percentChange24h } ?: ArrayList())
            }
            else -> {
                return ArrayList(topCurrencies?.sortedBy { it.cmcRank } ?: ArrayList())
            }
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
//                    dataManager.getBaseRate()
//                }
//                .observeOn(Schedulers.computation())
//                .map { baseRate ->
//                    this.baseRate = baseRate
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
//                        view.showTop8Changes(topCurrencies?.data, baseRate!!)
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

    /*
    Function saves an article to storage
    */
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

    /*
    Function removes an article from storage
    */
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

    //TODO: CLEAN THIS UP - THE ONQUERY LISTENER FIRES IMMEDIATELY WHEN SET WTF IS THAT MAN LMAO

    override fun detachView() {
        compositeDisposable?.dispose()
    }

    companion object {
        val TAG = "MarketsPresenter"
    }

}