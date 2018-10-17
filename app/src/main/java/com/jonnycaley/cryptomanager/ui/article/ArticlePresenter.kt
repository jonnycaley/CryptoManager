package com.jonnycaley.cryptomanager.ui.article

import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.Article
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ArticlePresenter (var dataManager: ArticleDataManager, var view: ArticleContract.View) : ArticleContract.Presenter{

    var compositeDisposable: CompositeDisposable? = null

    init {
        this.view.setPresenter(this)
    }

    override fun attachView() {
        if (compositeDisposable == null || (compositeDisposable as CompositeDisposable).isDisposed) {
            compositeDisposable = CompositeDisposable()
        }

        getSavedArticles()

    }

    private fun getSavedArticles() {
        dataManager.getSavedArticles()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<ArrayList<Article>> {
                    override fun onSuccess(articles: ArrayList<Article>) {
                        view.setLikeButton(articles.any { it.url == view.getArticleUrl() })
                    }

                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable?.add(d)
                    }

                    override fun onError(e: Throwable) {
                        println("onError: ${e.message}")
                    }
                })
    }


    override fun saveArticle(topArticle: Article) {
        dataManager.getSavedArticles()
                .map { savedArticles ->
                    if(savedArticles.none { it.url == topArticle.url })
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

    override fun detachView() {
        compositeDisposable?.dispose()
    }

    companion object {
        val TAG = "ArticlePresenter"
    }

}