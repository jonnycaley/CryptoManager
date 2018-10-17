package com.jonnycaley.cryptomanager.ui.home

import android.os.StrictMode
import com.google.gson.Gson
import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Currencies
import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.Article
import com.jonnycaley.cryptomanager.utils.Constants
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class HomePresenter(var dataManager: HomeDataManager, var view: HomeContract.View) : HomeContract.Presenter {

    var compositeDisposable: CompositeDisposable? = null

    var news = ArrayList<Article>()

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

    override fun getNews() {

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val savedArticles = ArrayList<Article>()

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
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { news ->
                        this.news.clear()
                        news.forEach { this.news.add(it) }
                        dataManager.writeToStorage(Constants.PAPER_HOME_TOP_NEWS, Gson().toJson(this.news))
                        view.showNews(this.news, savedArticles)
                    }
                    .flatMap {
                        dataManager.getCoinMarketCapService().getTop100()
                    }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { currencies ->
                        view.showTop100Changes(currencies.data?.sortedBy { it.quote?.uSD?.percentChange24h }?.asReversed())
                        dataManager.writeToStorage(Constants.PAPER_HOME_TOP_100, Gson().toJson(currencies))
                    }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : SingleObserver<Unit> {
                        override fun onSuccess(currencies: Unit) {
                            view.hideProgressBar()
                            view.showScrollLayout()
                        }

                        override fun onSubscribe(d: Disposable) {
                            println("onSubscribe")
                            view.showProgressBar()
                            compositeDisposable?.add(d)
                        }

                        override fun onError(e: Throwable) {
                            println("onError: ${e.message}")
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

            dataManager.readStorage(Constants.PAPER_HOME_TOP_NEWS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { json ->
                        if (json == "") {
                            view.showNoInternet()
                        } else {
                            val news = Gson().fromJson(json, ArrayList<Article>()::class.java)
                            this.news = news
                            view.showNews(this.news, savedArticles)
                        }
                    }
                    .flatMap {
                        dataManager.readStorage(Constants.PAPER_HOME_TOP_100)
                    }
                    .map { json ->
                        if (json == "") {
                            view.showNoInternet()
                        } else {
                            val currencies = Gson().fromJson(json, Currencies::class.java)
                            view.showTop100Changes(currencies.data?.sortedBy { it.quote?.uSD?.percentChange24h }?.asReversed())
                        }
                    }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : SingleObserver<Unit> {
                        override fun onSuccess(t: Unit) {
                            view.hideProgressBar()
                            view.showScrollLayout()
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

    override fun onResume() {

        if(news.isEmpty()) {
            println("Getting news")
            getNews()
        } else {
            println("Loading old news")
            dataManager.getSavedArticles()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : SingleObserver<ArrayList<Article>> {
                        override fun onSuccess(savedArticles: ArrayList<Article>) {
                            view.showNews(news, savedArticles)
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