package com.jonnycaley.cryptomanager.ui.settings.bookmarkedArticles

import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.News.Article
import io.reactivex.CompletableObserver
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class BookmarkedArticlesPresenter(var dataManager: BookmarkedArticlesDataManager, var view: BookmarkedArticlesContract.View) : BookmarkedArticlesContract.Presenter{

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

    /*
    Function loads saved articles
    */
    override fun loadSavedArticles() {
        dataManager.getArticles()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<ArrayList<Article>> {
                    override fun onSuccess(news: ArrayList<Article>) {
                        if(news.isEmpty()){
                            view.showNoArticles() //show no articles
                        } else {
                            view.hideNoArticles() //hide no articles
                            view.showSavedNews(news) //show saved articles
                        }
                        view.hideProgressLayout()
                    }

                    override fun onSubscribe(d: Disposable) { //on subscribe
                        view.showProgressLayout()
                        println("onSubscribe")
                        compositeDisposable?.add(d)
                    }

                    override fun onError(e: Throwable) { //on error
                        println("onError: ${e.message}")
                        view.hideProgressLayout()
                        view.hideNoArticles()
                        view.showError()
                    }
                })
    }

    /*
    Function removes article
    */
    override fun removeArticle(savedArticles : ArrayList<Article>?, article: Article) {

        dataManager.saveArticles(savedArticles?.filter { it.url != article.url } as ArrayList<Article>) //save articles without article
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        loadSavedArticles()
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

    override fun detachView() {
        compositeDisposable?.dispose()
    }

}