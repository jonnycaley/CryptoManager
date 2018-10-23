package com.jonnycaley.cryptomanager.ui.home

import android.os.StrictMode
import android.util.Log
import com.google.gson.Gson
import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Currencies
import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.Article
import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.ExchangeRates
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.Rate
import com.jonnycaley.cryptomanager.utils.Constants
import com.jonnycaley.cryptomanager.utils.JsonModifiers
import com.pacoworks.rxpaper2.RxPaperBook
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Subscription

class HomePresenter(var dataManager: HomeDataManager, var view: HomeContract.View) : HomeContract.Presenter {

    var compositeDisposable: CompositeDisposable? = null

    var news = ArrayList<Article>()
    val savedArticles = ArrayList<Article>()
    var savedCurrencies : Currencies? = null
    var exchangeRates : ExchangeRates? = null

    init {
        this.view.setPresenter(this)
    }

    override fun attachView() {
        if (compositeDisposable == null || (compositeDisposable as CompositeDisposable).isDisposed) {
            compositeDisposable = CompositeDisposable()
        }
        getNews()
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
            dataManager.getCryptoControlService().getTopNews("10")
                    .subscribeOn(Schedulers.io())
                    .map { news ->
                        this.news.clear()
                        news.forEach { this.news.add(it) }
                    }
                    .flatMapCompletable {
                        dataManager.saveTopNews(this.news)
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete {
                        view.showNews(this.news, savedArticles)
                    }
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
                            view.showNews(this.news, savedArticles)
                        }
                    }
                    .observeOn(Schedulers.io())
                    .flatMapSingle {
                        dataManager.readTop100()
                    }
                    .map { currencies ->
                        if (currencies.data?.isEmpty()!!) {
                            view.showNoInternet()
                        } else {
                            currencies
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

        if(dataManager.checkConnection()) {

            dataManager.getCoinMarketCapService().getTop100()
                    .subscribeOn(Schedulers.io())
                    .flatMapCompletable { currencies ->
                        savedCurrencies = currencies
                        dataManager.saveTop100(currencies)
                    }
                    .andThen(
                            dataManager.getExchangeRateService().getExchangeRates()
                    )
                    .map { json ->
                        exchangeRates = Gson().fromJson(JsonModifiers.jsonToCurrencies(json), ExchangeRates::class.java)
                    }
                    .flatMapSingle {
                        dataManager.getBaseFiat()
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { baseFiat ->
                        view.showTop100Changes(savedCurrencies?.data?.sortedBy { it.quote?.uSD?.percentChange24h }?.asReversed(), baseFiat)
                    }
                    .subscribe(object : Observer<Unit> {
                        override fun onComplete() {
                            view.hideProgressBar()
                            view.showScrollLayout()
                        }

                        override fun onNext(t: Unit) {
                            println("onNext")
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

        if(news.isEmpty()) {
            println("Getting news")
            getNews()
        } else {
            println("Loading old news")
            dataManager.getSavedArticles()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { savedArticles -> view.showNews(news, savedArticles) }
                    .subscribe(object : SingleObserver<Unit> {
                        override fun onSuccess(savedArticles: Unit) {
                            getTop100()
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
//                    })
        }

    }

    override fun detachView() {
        compositeDisposable?.dispose()
    }

    companion object {
        val TAG = "HomePresenter"
    }

}