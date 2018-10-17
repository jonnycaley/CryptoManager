package com.jonnycaley.cryptomanager.ui.settings

import io.reactivex.CompletableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class SettingsPresenter(var dataManager: SettingsDataManager, var view: SettingsContract.View) : SettingsContract.Presenter{

    var compositeDisposable: CompositeDisposable? = null

    init {
        this.view.setPresenter(this)
    }

    override fun attachView() {
        if (compositeDisposable == null || (compositeDisposable as CompositeDisposable).isDisposed) {
            compositeDisposable = CompositeDisposable()
        }
        //start stuff here

        loadSettings()
    }

    private fun loadSettings() {
        view.loadSettings()
    }

    override fun deletePortfolio() {
        dataManager.deletePortfolio()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (object : CompletableObserver{
                    override fun onComplete() {
                        view.showPortfolioDeleted()
                    }

                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable?.add(d)
                    }

                    override fun onError(e: Throwable) {
                        view.showPortfolioDeletedError()
                    }

                })

    }

    override fun deleteSavedArticles() {
        dataManager.deleteSavedArticles()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (object : CompletableObserver{
                    override fun onComplete() {
                        view.showSavedArticlesDeleted()
                    }

                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable?.add(d)
                    }

                    override fun onError(e: Throwable) {
                        view.showSavedArticlesDeletedError()
                    }

                })
    }

    override fun detachView() {
        compositeDisposable?.dispose()
    }

}