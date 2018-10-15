package com.jonnycaley.cryptomanager.ui.settings.savedArticles

import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.News
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
                .subscribe(object : SingleObserver<ArrayList<News>> {
                    override fun onSuccess(news: ArrayList<News>) {
                        view.showSavedNews(news)
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