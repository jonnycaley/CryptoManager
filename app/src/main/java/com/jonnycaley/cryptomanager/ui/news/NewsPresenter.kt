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
        getNews(true)
    }

    /*
    Function executes on refresh
    */
    override fun onRefresh() {
        getNews(false) //get the news
    }

    /*
    Function shows the top 8 changes cards
    */
    override fun getNews(showProgressLayout : Boolean) {

        if (dataManager.checkConnection()) {
            dataManager.getCryptoControlService().getTopNews() //get top news
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.computation())
                    .map { news ->
                        this.news.clear()
                        news.filter { it.thumbnail != null }.sortedBy { it.publishedAt }.forEach { this.news.add(it) } //filter for ones with an image
                    }
                    .observeOn(Schedulers.io())
                    .flatMapCompletable {
                        dataManager.saveTopNews(this.news) //save the news
                    }
                    .observeOn(Schedulers.computation())
                    .subscribe(object : CompletableObserver {
                        override fun onComplete() {
                            getTop100() //get top 100
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
        }
    }

    /*
    Function executes on resume of fragment
    */
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
    Function gets the top 100 cryptos
    */
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
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : CompletableObserver {
                        override fun onComplete() {
                            Log.i(TAG, "onComplete1")
                            view.hideNoInternetLayout()
                            view.showNews(linkedCryptos, savedArticles)
                            view.showTop8Changes(top100, true)
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

    /*
    Function gets more articles
    */
    override fun getMoreArticles(size: Int) {
    }

    /*
    Function saves article to internal storage
    */
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

    /*
    Function removes article from internal storage
    */
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

    /*
    Function links the crypto to articles for crypto icon if possible
    */
    private fun linkCryptoToArticles(newsItems: ArrayList<Article>, top100: ArrayList<Currency>): HashMap<Article, Currency?> {

        val map = HashMap<Article, Currency?>() //put in it

        for (i in 0 until newsItems.size) {

            val item = newsItems[i]
            val position = i
            var cryptoOrNull: Currency? = null
            top100.forEach { crypto ->
                if ((item.title?.toUpperCase()?.contains(crypto.name?.toUpperCase() ?: "") == true || item.title?.toUpperCase()?.contains(crypto.symbol?.toUpperCase()?: "") == true) //if the item contains the crypto
                        && (item.coins?.any { it.tradingSymbol?.toUpperCase() == crypto.symbol?.toUpperCase() }) == true) {
                    if (position != 0) { //if its not the header item
                        if ((!((newsItems.get(position - 1).title?.toUpperCase()?.contains(crypto.name?.toUpperCase() ?: "") == true || newsItems.get(position - 1).title?.toUpperCase()?.contains(crypto.symbol?.toUpperCase() ?: "") == true)
                                        && (newsItems[position - 1].coins?.any { it.tradingSymbol?.toUpperCase() == crypto.symbol?.toUpperCase() } == true)))) {
                            cryptoOrNull = crypto //add the crypto
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