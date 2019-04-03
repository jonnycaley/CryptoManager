package com.jonnycaley.cryptomanager.ui.news

import android.util.Log
import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Currency
import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.News.Article
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.ExchangeRates
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class NewsPresenter(var dataManager: NewsDataManager, var view: NewsContract.View) : NewsContract.Presenter {

    var compositeDisposable: CompositeDisposable? = null

    var news = ArrayList<Article>()
    var savedArticles = ArrayList<Article>()
    var top100 = ArrayList<Currency>()
    var exchangeRates: ExchangeRates? = null
    var linkedCryptos = HashMap<Article, Currency?>()

    init {
        this.view.setPresenter(this)
    }

    override fun attachView() {
        if (compositeDisposable == null || (compositeDisposable as CompositeDisposable).isDisposed) {
            compositeDisposable = CompositeDisposable()
        }
//        getNews()
//        TODO: UNCOMMENT BELOW
        getNews(true)
    }


    override fun onRefresh() {
        //TODO: changing theme and then navigating to the home fragment forces a reload because the data held with the activity is overwritten. check storage if activity data is empty too?
        //TODO: clicking too and from quickly breaks at linkCryptoToArticles
        getNews(false)
//        var linkedCryptos = HashMap<Article, Currency?>()

//        Log.i(TAG, "Loading old news")
//        dataManager.getSavedArticles()
//                .subscribeOn(Schedulers.io())
//                .observeOn(Schedulers.computation())
//                .map { savedArticles ->
//                    this.savedArticles = savedArticles
//                }
//                .observeOn(Schedulers.io())
//                .flatMap {
//                    dataManager.readTopNews()
//                }
//                .observeOn(Schedulers.computation())
//                .map{ news ->
//                    this.news = news
//                }
//                .observeOn(Schedulers.io())
//                .flatMap {
//                    dataManager.readTop100()
//                }
//                .observeOn(Schedulers.computation())
//                .map { t100 ->
//                    this.top100 = ArrayList(t100)
//
//                    if(!this.news.isEmpty() && !this.top100.isEmpty())
//                        linkedCryptos = linkCryptoToArticles(this.news, this.top100)
//                }
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(object : SingleObserver<Unit> {
//                    override fun onSuccess(t: Unit) {
//                        if(linkedCryptos.isNotEmpty()) {
//                            view.showNews(linkedCryptos, savedArticles)
//                            view.showTop100Changes(top100, false)
//                            Log.i(TAG, "ShowingNewsssssss")
//                            view.hideProgressBar()
//                            view.showScrollLayout()
//                        }
//                        if (news.isEmpty() || top100.isEmpty()) {
////                            Log.i(TAG, "Getting news")
//                            getNews()
//                        } else {
//                            getTop100()
//                        }
//                    }
//
//                    override fun onSubscribe(d: Disposable) {
//                        compositeDisposable?.add(d)
//                    }
//
//                    override fun onError(e: Throwable) {
//                        Log.i(TAG, "onError: ${e.message}")
//                    }
//
//                })

//            dataManager.readStorage(Constants.PAPER_HOME_TOP_NEWS)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .map { json ->
//                        if (json == "") {
//                            view.showNoInternet()
//                        } else {
//                            val news = Gson().fromJson(json, ArrayList<Article>()::class.java)
//                            this.news = news
//                        }
//                    }
//                    .flatMap { dataManager.getSavedArticles() }
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(object : SingleObserver<ArrayList<Article>> {
//                        override fun onSuccess(savedArticles: ArrayList<Article>) {
//                            view.showNews(news, savedArticles)
//                        }
//
//                        override fun onSubscribe(d: Disposable) {
//                            compositeDisposable?.add(d)
//                        }
//
//                        override fun onError(e: Throwable) {
//                            Log.i(TAG, "onError: ${e.message}")
//                        }
//
//
    }

    override fun getNews(showProgressLayout : Boolean) {

//        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
//        StrictMode.setThreadPolicy(policy)

        if (dataManager.checkConnection()) {
//            dataManager.getSavedArticles()
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .map { articles -> savedArticles = articles }
//                    .map { if(this.news.isNotEmpty())
//                        view.showNews(this.news, savedArticles)
//                    }
            dataManager.getCryptoControlService().getTopNews()
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.computation())
                    .map { news ->
                        this.news.clear()
                        news.filter { it.thumbnail != null }.sortedBy { it.publishedAt }.forEach { this.news.add(it) }
                    }
                    .observeOn(Schedulers.io())
                    .flatMapCompletable {
                        dataManager.saveTopNews(this.news)
                    }
                    .observeOn(Schedulers.computation())
                    .subscribe(object : CompletableObserver {
                        override fun onComplete() {
                            getTop100()
                        }

                        override fun onSubscribe(d: Disposable) {
                            if(showProgressLayout)
                                view.showProgressBar()
                            compositeDisposable?.add(d)
                        }

                        override fun onError(e: Throwable) {
                            Log.i(TAG, "onErrorGetNews: ${e.message}")
                            view.hideProgressBar()
                            view.showError()
                        }
                    })
        } else {
            if(showProgressLayout)
                view.showNoInternetLayout()
            else
                view.hideProgressBar()
//            dataManager.getSavedArticles()
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .map { articles -> savedArticles = articles }
//                    .map {
//                        if (this.news.isNotEmpty())
//                            view.showNews(this.news, savedArticles)
//                    }
//                    .flatMap {

//            dataManager.readTopNews()
//                    .toObservable()
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .map { gsonNews ->
//                        if (gsonNews.isEmpty()) {
//                            view.showNoInternetLayout()
//                        } else {
//                            this.news.clear()
//                            this.news.addAll(gsonNews.sortedBy { it.hotness })
//                        }
//                    }
//                    .observeOn(Schedulers.io())
//                    .flatMapSingle {
//                        dataManager.readTop100()
//                    }
//                    .observeOn(Schedulers.computation())
//                    .map { top100 ->
//                        if (top100.isEmpty() || top100.isEmpty()) {
//                            view.showNoInternetLayout()
//                        } else {
//                            this.top100.clear()
//                            top100.forEach { this.top100.add(it) }
//                            linkedCryptos = linkCryptoToArticles(this.news, this.top100)
////                            view.showNews(linkCryptoToArticles(this.news, this.topcurrencies), savedArticles)
////                            view.showTop100Changes(currencies.data?.sortedBy { it.quote?.uSD?.percentChange24h }?.asReversed(), exchangeRates.rates.filter { it.fiat.toLowerCase() == baseRate.toLowerCase() })
//                        }
//                    }
//                    .filter { this.top100.isEmpty() }
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(object : Observer<Any> {
//                        override fun onComplete() {
//                            if(linkedCryptos.isNotEmpty()) {
//                                view.showNews(linkedCryptos, savedArticles)
//                                view.hideNoInternetLayout()
//                            }
//                            view.hideProgressBar()
//                            view.showScrollLayout()
//                        }
//
//                        override fun onNext(t: Any) {
//                        }
//
//                        override fun onSubscribe(d: Disposable) {
//                            compositeDisposable?.add(d)
//                        }
//
//                        override fun onError(e: Throwable) {
//                            Log.i(TAG, "onError: ${e.message}")
//                            view.hideNoInternetLayout()
//                            view.showError()
//                        }
//                    })
        }
    }

    override fun onResume() {
//        if(top100.isNotEmpty() && news.isNotEmpty())
        Log.i(TAG, "onResume")
        dataManager.getSavedArticles()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map { savedArticles ->
                    Log.i(TAG, "gotSavedArticles")
                    this.savedArticles = savedArticles
                }
                .observeOn(AndroidSchedulers.mainThread())
                .map {
                    if(linkedCryptos.isNotEmpty()) {
                        Log.i(TAG, "Showing new saved articles")
                        view.showNews(linkedCryptos, savedArticles)
                    }
                }
                .subscribe(object : SingleObserver<Unit> {
                    override fun onSuccess(t: Unit) {
//                        if(linkedCryptos?.isNotEmpty()!!) {
//                            view.showNews(linkedCryptos, savedArticles)
//                            view.showTop100Changes(top100, false)
//                            Log.i(TAG, "ShowingNewsssssss")
//                            view.hideProgressBar()
//                            view.showScrollLayout()
//                        }
//                        if (news.isEmpty() || top100.isEmpty()) {
////                            Log.i(TAG, "Getting news")
//                            getNews()
//                        } else {
//                            getTop100()
//                        }
                    }

                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable?.add(d)
                    }

                    override fun onError(e: Throwable) {
                        Log.i(TAG, "onError: ${e.message}")
                    }
                })
    }

    fun getTop100() {

        if (dataManager.checkConnection()) {

            dataManager.getCoinMarketCapService().getTop100()
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.computation())
                    .map { currencies ->
                        this.top100.clear()
                        currencies.data?.forEach { this.top100.add(it) }
                        linkedCryptos = linkCryptoToArticles(this.news, top100)
                        return@map currencies
                    }
                    .observeOn(Schedulers.io())
                    .flatMapCompletable { currencies ->
                        dataManager.saveTop100(currencies.data)
                    }
//                    .observeOn(Schedulers.computation())
//                    .andThen { articles = linkCryptoToArticles(news, topcurrencies) }
//                    .flatMapCompletable { currencies ->
//                        savedCurrencies = currencies
//                        dataManager.saveTop100(currencies)
//                    }
//                    .andThen(
//                            dataManager.getExchangeRateService().getExchangeRates()
//                    )
//                    .map { json ->
//                        exchangeRates = Gson().fromJson(JsonModifiers.jsonToCurrencies(json), ExchangeRates::class.java)
//                    }
//                    .flatMapSingle {
//                        dataManager.getBaseRate()
//                    }
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .map { baseRate ->
//                        view.showTop100Changes(savedCurrencies?.data, baseRate) //?.sortedBy { it.quote?.uSD?.percentChange24h }?.asReversed()
//                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : CompletableObserver {
                        override fun onComplete() {
                            Log.i(TAG, "onComplete1")
                            view.hideNoInternetLayout()
                            view.showNews(linkedCryptos, savedArticles)
                            view.showTop100Changes(top100, true)
                            view.showScrollLayout()
                            view.hideProgressBar()
                        }

                        override fun onSubscribe(d: Disposable) {
                            compositeDisposable?.add(d)
                        }

                        override fun onError(e: Throwable) {
                            Log.i(TAG, "onError: getTopcurrencies: ${e.message}")
                            view.showError()
                            view.hideNoInternetLayout()
                            view.hideProgressBar()
                        }
                    })
        } else {
            view.showNoInternetLayout()
        }
    }

    override fun getMoreArticles(size: Int) {

//        if(dataManager.checkConnection()){
//
//            val moreNews = size+8
//            Log.i(TAG, "newsSizerequest: $moreNews")
//            dataManager.getCryptoControlService().getTopNews()
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(Schedulers.computation())
//                    .map { news ->
//                        this.news.clear()
//                        news.sortedBy { it.hotness }.forEach { this.news.add(it) }
//                        linkedCryptos = linkCryptoToArticles(this.news, top100)
//                    }
//                    .observeOn(Schedulers.io())
//                    .flatMapCompletable {
//                        dataManager.saveTopNews(this.news)
//                    }
//                    .observeOn(Schedulers.computation())
//                    .subscribe(object : CompletableObserver {
//                        override fun onComplete() {
//                            Log.i(TAG, "linkedCrypto: ${linkedCryptos.size}")
//                            Log.i(TAG, "savedArticles: ${savedArticles.size}")
//                            view.showNews(linkedCryptos, savedArticles)
//                            view.setIsLoading(false)
//                        }
//                        override fun onSubscribe(d: Disposable) {
//                            compositeDisposable?.add(d)
//                        }
//                        override fun onError(e: Throwable) {
//                            Log.i(TAG, "onErrorGetNews: ${e.message}")
//                        }
//                    })
//        } else {
//
//        }
    }

    override fun saveArticle(topArticle: Article) {

        dataManager.getSavedArticles()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map { savedArticles ->
                    if (savedArticles.none { it.url == topArticle.url })
                        savedArticles.add(topArticle)
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
                        Log.i(TAG, "onError: ${e.message}")
                    }
                })
    }

    override fun removeArticle(topArticle: Article) {

        dataManager.getSavedArticles()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map { articles -> return@map articles.filter { it.url != topArticle.url } }
                .observeOn(Schedulers.io())
                .flatMapCompletable { savedArticles -> dataManager.saveArticles(ArrayList(savedArticles)) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                    }

                    override fun onSubscribe(d: Disposable) {
                        Log.i(TAG, "onSubscribe")
                        compositeDisposable?.add(d)
                    }

                    override fun onError(e: Throwable) {
                        Log.i(TAG, "onError: ${e.message}")
                    }
                })
    }

    private fun linkCryptoToArticles(newsItems: ArrayList<Article>, top100: ArrayList<Currency>): HashMap<Article, Currency?> {

        val map = HashMap<Article, Currency?>() //put in it

        for (i in 0 until newsItems.size) {

            val item = newsItems[i]
            val position = i
            var cryptoOrNull: Currency? = null
            top100.forEach { crypto ->
                if ((item.title?.toUpperCase()?.contains(crypto.name?.toUpperCase() ?: "") == true || item.title?.toUpperCase()?.contains(crypto.symbol?.toUpperCase()?: "") == true)
                        && (item.coins?.any { it.tradingSymbol?.toUpperCase() == crypto.symbol?.toUpperCase() }) == true) {
                    if (position != 0) {
                        if ((!((newsItems.get(position - 1).title?.toUpperCase()?.contains(crypto.name?.toUpperCase() ?: "") == true || newsItems.get(position - 1).title?.toUpperCase()?.contains(crypto.symbol?.toUpperCase() ?: "") == true)
                                        && (newsItems[position - 1].coins?.any { it.tradingSymbol?.toUpperCase() == crypto.symbol?.toUpperCase() } == true)))) {
                            cryptoOrNull = crypto

                        }

                    } else {

                        cryptoOrNull = crypto
                    }
                }
            }
            map[item] = cryptoOrNull
        }
        return map
    }

    override fun detachView() {
        compositeDisposable?.dispose()
    }

    companion object {
        val TAG = "NewsPresenter"
    }
}

//TODO: THINGS TO CONSIDER
//TODO: CAN WE LOAD DATA BEFORE SAVING IT TO SAVE TIME
//TODO: CAN WE MAKE REQUESTS AT THE SAME TIME INSTEAD OF IN A CHAIN