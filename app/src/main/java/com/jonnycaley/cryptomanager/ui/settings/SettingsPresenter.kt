package com.jonnycaley.cryptomanager.ui.settings

import com.jonnycaley.cryptomanager.data.model.ExchangeRates.Rate
import io.reactivex.CompletableObserver
import io.reactivex.SingleObserver
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

    /*
    Function saves theme preferences
    */
    override fun saveThemePreference(checked: Boolean) {

        dataManager.saveThemePreference(checked)  //save preferences
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (object : CompletableObserver{
                    override fun onComplete() {
                        view.updateTheme() //update theme
                    }

                    override fun onSubscribe(d: Disposable) { //on subscribe
                        compositeDisposable?.add(d)
                    }

                    override fun onError(e: Throwable) { //on error
                        view.showPortfolioDeletedError()
                    }

                })

    }

    /*
    Function loads settings
    */
    override fun loadSettings() {

        dataManager.getBaseFiat() //get base fiat
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { rate ->
                    view.loadSettings(rate) //load settings
                }
                .subscribe (object : SingleObserver<Unit>{
                    override fun onSuccess(rate: Unit) {
                        view.hideProgressLayout() //hide progress
                    }

                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable?.add(d)
                    }

                    override fun onError(e: Throwable) {
                        view.showPortfolioDeletedError() //show portfolio
                    }

                })

//        view.loadSettings(dataManager.getBaseRate())
//                .map { json ->
//                    Gson().fromJson(json, Rate::class.java) }
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe (object : SingleObserver<Rate>{
//                    override fun onSuccess(fiat: Rate) {
//                        view.loadSettings(fiat)
//                    }
//
//                    override fun onSubscribe(d: Disposable) {
//                        compositeDisposable?.add(d)
//                    }
//
//                    override fun onError(e: Throwable) {
//                        println("onError: ${e.message}")
//                    }
//
//                })
    }

    /*
    Function deletes portfolio
    */
    override fun deletePortfolio() {
        dataManager.deletePortfolio() //delete portfolio
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (object : CompletableObserver{
                    override fun onComplete() {
                        view.showPortfolioDeleted() //show portfolio
                    }

                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable?.add(d)
                    }

                    override fun onError(e: Throwable) {
                        view.showPortfolioDeletedError()
                    }

                })

    }

    /*
    Function deletes all articles
    */
    override fun deleteSavedArticles() {
        dataManager.deleteSavedArticles() //delete saved articles
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (object : CompletableObserver{
                    override fun onComplete() {
                        view.showSavedArticlesDeleted() //show saved articles
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