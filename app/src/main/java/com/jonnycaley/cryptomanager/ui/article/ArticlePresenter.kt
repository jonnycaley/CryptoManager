package com.jonnycaley.cryptomanager.ui.article

import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.News.Article
import io.reactivex.CompletableObserver
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

    /*
    Function gets the saved articles and sets the like button if the article is saved
    */
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
                        println("onError: ${e.message}") //no on error handling as it is not worth it
                    }
                })
    }

    /*
    Function saves the article to internal storage
    */
    override fun saveArticle(topArticle: Article) {

        dataManager.getSavedArticles()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map { savedArticles ->
                    if(savedArticles.none { it.url == topArticle.url })
                        savedArticles.add(topArticle) //add top article to saved articles
                    return@map savedArticles
                }
                .observeOn(Schedulers.io())
                .flatMapCompletable { savedArticles -> dataManager.saveArticles(savedArticles) }
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

    /*
    Function removes the article from saved articles in internal storage
    */
    override fun removeArticle(topArticle: Article) {

        dataManager.getSavedArticles()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map { articles -> return@map articles.filter { it.url != topArticle.url } } //remove the selected article and return array
                .observeOn(Schedulers.io())
                .flatMapCompletable { savedArticles -> dataManager.saveArticles(ArrayList(savedArticles)) } //save the array
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

    override fun detachView() {
        compositeDisposable?.dispose()
    }

    companion object {
        val TAG = "ArticlePresenter"
    }
}