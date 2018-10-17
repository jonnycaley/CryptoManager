package com.jonnycaley.cryptomanager.ui.settings.savedArticles

import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.Article
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class SavedArticlesPresenter(var dataManager: SavedArticlesDataManager, var view: SavedArticlesContract.View) : SavedArticlesContract.Presenter{

    var compositeDisposable: CompositeDisposable? = null

    init {
        this.view.setPresenter(this)
    }

    override fun attachView() {
        if (compositeDisposable == null || (compositeDisposable as CompositeDisposable).isDisposed) {
            compositeDisposable = CompositeDisposable()
        }

        loadSavedArticles()
    }

    private fun loadSavedArticles() {
        dataManager.getArticles()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<ArrayList<Article>> {
                    override fun onSuccess(news: ArrayList<Article>) {
                        if(news.isEmpty()){
                            view.showNoArticles()
                        } else {
                            view.hideNoArticles()
                            view.showSavedNews(news)
                        }
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

    override fun removeArticle(savedArticles : ArrayList<Article>?, article: Article) {

        println("Removing article")

        val articles = savedArticles?.filter { it.url != article.url } as ArrayList<Article>

        dataManager.saveArticles(articles)

        loadSavedArticles()
    }

    override fun detachView() {
        compositeDisposable?.dispose()
    }

}