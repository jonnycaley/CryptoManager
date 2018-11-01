package com.jonnycaley.cryptomanager.ui.home

import android.util.Log
import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Currency
import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.Article
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.ExchangeRates
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class HomePresenter(var dataManager: HomeDataManager, var view: HomeContract.View) : HomeContract.Presenter {

    var compositeDisposable: CompositeDisposable? = null

    var news = ArrayList<Article>()
    var savedArticles = ArrayList<Article>()
    var top100 = ArrayList<Currency>()
    var exchangeRates: ExchangeRates? = null

    init {
        this.view.setPresenter(this)
    }

    override fun attachView() {
        if (compositeDisposable == null || (compositeDisposable as CompositeDisposable).isDisposed) {
            compositeDisposable = CompositeDisposable()
        }
//        getNews()
    }

    override fun saveArticle(topArticle: Article) {

        dataManager.getSavedArticles()
                .map { savedArticles ->
                    if (savedArticles.none { it.url == topArticle.url })
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

    override fun getNews() {

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
            dataManager.getCryptoControlService().getTopNews(newsCount)
                    .subscribeOn(Schedulers.io())
                    .map { news ->
                        this.news.clear()
                        news.forEach { this.news.add(it) }
                    }
                    .flatMapCompletable {
                        dataManager.saveTopNews(this.news)
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : CompletableObserver {
                        override fun onComplete() {
                            getTop100()
                        }

                        override fun onSubscribe(d: Disposable) {
                            view.showProgressBar()
                            compositeDisposable?.add(d)
                        }

                        override fun onError(e: Throwable) {
                            println("onErrorGetNews: ${e.message}")
                        }
                    })

        } else {
//            dataManager.getSavedArticles()
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .map { articles -> savedArticles = articles }
//                    .map {
//                        if (this.news.isNotEmpty())
//                            view.showNews(this.news, savedArticles)
//                    }
//                    .flatMap {

            dataManager.readTopNews()
                    .toObservable()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { gsonNews ->
                        if (gsonNews.isEmpty()) {
                            view.showNoInternet()
                        } else {
                            this.news = gsonNews
                        }
                    }
                    .observeOn(Schedulers.io())
                    .flatMapSingle {
                        dataManager.readTop100()
                    }
                    .map { top100 ->
                        if (top100.data == null || top100.data?.isEmpty()!!) {
                            view.showNoInternet()
                        } else {
                            this.top100.clear()
                            top100.data!!.forEach { this.top100.add(it) }
                            view.showNews(linkCryptoToArticles(this.news, this.top100), savedArticles)
//                            view.showTop100Changes(currencies.data?.sortedBy { it.quote?.uSD?.percentChange24h }?.asReversed(), exchangeRates.rates.filter { it.fiat.toLowerCase() == baseFiat.toLowerCase() })
                        }
                    }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<Any> {
                        override fun onComplete() {
                            view.hideProgressBar()
                            view.showScrollLayout()
                        }

                        override fun onNext(t: Any) {
                        }

                        override fun onSubscribe(d: Disposable) {
                            compositeDisposable?.add(d)
                        }

                        override fun onError(e: Throwable) {
                            println("onError: ${e.message}")
//                            view.showError()
                        }

                    })
        }
    }

    fun getTop100() {

        if (dataManager.checkConnection()) {

            var articles = HashMap<Article, Currency?>()

            dataManager.getCoinMarketCapService().getTop100()
                    .subscribeOn(Schedulers.io())
                    .flatMapCompletable { currencies ->
                        this.top100.clear()
                        currencies.data?.forEach { this.top100.add(it) }
                        dataManager.saveTop100(currencies)
                    }
//                    .observeOn(Schedulers.computation())
//                    .andThen { articles = linkCryptoToArticles(news, top100) }
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
//                        dataManager.getBaseFiat()
//                    }
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .map { baseFiat ->
//                        view.showTop100Changes(savedCurrencies?.data, baseFiat) //?.sortedBy { it.quote?.uSD?.percentChange24h }?.asReversed()
//                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : CompletableObserver {
                        override fun onComplete() {
                            Log.i(TAG, "onComplete1")
                            view.showNews(linkCryptoToArticles(news, top100), savedArticles)
                            view.showTop100Changes(top100)
                            view.hideProgressBar()
                            view.showScrollLayout()
                        }

                        override fun onSubscribe(d: Disposable) {
                            compositeDisposable?.add(d)
                        }

                        override fun onError(e: Throwable) {
                            println("onError: getTop100: ${e.message}")
//                            view.showError()
                        }

                    })
        } else {

        }

    }

    override fun onResume() {

        //TODO: changing theme and then navigating to the home fragment forces a reload because the data held with the activity is overwritten. check storage if activity data is empty too?
        //TODO: clicking too and from quickly breaks at linkCryptoToArticles

        println("Loading old news")
        dataManager.getSavedArticles()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { savedArticles ->
                    this.savedArticles = savedArticles
                }
                .subscribe(object : SingleObserver<Unit> {
                    override fun onSuccess(savedArticles: Unit) {
                        if (news.isEmpty() || top100.isEmpty()) {
                            println("Getting news")
                            getNews()
                        } else {
                            view.showNews(linkCryptoToArticles(news, top100), this@HomePresenter.savedArticles)
                            getTop100()
                        }
                    }

                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable?.add(d)
                    }

                    override fun onError(e: Throwable) {
                        println("onError: ${e.message}")
                    }

                })

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
//                            println("onError: ${e.message}")
//                        }
//
//
    }

    private fun linkCryptoToArticles(newsItems: ArrayList<Article>, top100: ArrayList<Currency>): HashMap<Article, Currency?> {

        val map = HashMap<Article, Currency?>()//put in it

        Log.i(TAG, "linking crypto's")

        for (i in 0 until newsItems.size) {

            val item = newsItems[i]
            val position = i
            var cryptoOrNull: Currency? = null
            top100.forEach { crypto ->
                if ((item.title?.toUpperCase()?.contains(crypto.name!!.toUpperCase())!! || item.title?.toUpperCase()?.contains(crypto.symbol!!.toUpperCase())!!)
                        && (item.coins!!.any { it.tradingSymbol?.toUpperCase() == crypto.symbol?.toUpperCase() })) {
                    if (position != 0) {
                        if ((!((newsItems.get(position - 1).title?.toUpperCase()?.contains(crypto.name!!.toUpperCase())!! || newsItems.get(position - 1).title?.toUpperCase()?.contains(crypto.symbol!!.toUpperCase())!!)
                                        && (newsItems.get(position - 1).coins!!.any { it.tradingSymbol?.toUpperCase() == crypto.symbol?.toUpperCase() })))) {
                            cryptoOrNull = crypto

                        }

                    } else {

                        cryptoOrNull = crypto
                    }
                }
            }
            map[item] = cryptoOrNull
        }
        Log.i(TAG, "crypto's linked!")
        return map
    }


    override fun detachView() {
        compositeDisposable?.dispose()
    }

    companion object {
        val TAG = "HomePresenter"

        var newsCount = "10"
    }

}