package com.jonnycaley.cryptomanager.ui.settings.savedArticles

import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.News.Article
import io.reactivex.CompletableObserver
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

    override fun loadSavedArticles() {
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
                        view.hideProgressLayout()
                    }

                    override fun onSubscribe(d: Disposable) {
                        view.showProgressLayout()
                        println("onSubscribe")
                        compositeDisposable?.add(d)
                    }

                    override fun onError(e: Throwable) {
                        println("onError: ${e.message}")
                        view.hideProgressLayout()
                        view.hideNoArticles()
                        view.showError()
                    }
                })
    }

    override fun removeArticle(savedArticles : ArrayList<Article>?, article: Article) {

        dataManager.saveArticles(savedArticles?.filter { it.url != article.url } as ArrayList<Article>)
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