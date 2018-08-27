package com.jonnycaley.cryptomanager.ui.home

import io.reactivex.disposables.CompositeDisposable
import android.os.StrictMode
import com.jonnycaley.cryptomanager.data.model.news.News
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class HomePresenter (var dataManager: HomeDataManager, var view: HomeContract.View) : HomeContract.Presenter{

    var compositeDisposable: CompositeDisposable? = null

    init {
        this.view.setPresenter(this)
    }

    override fun attachView() {
        if (compositeDisposable == null || (compositeDisposable as CompositeDisposable).isDisposed) {
            compositeDisposable = CompositeDisposable()
        }
        //start stuff here

        getNews()
    }


    private fun getNews() {

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        if(dataManager.checkConnection()){
            dataManager.getCryptoControlService().getTopNews()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : SingleObserver<Array<News>> {
                        override fun onSuccess(news: Array<News>) {
                            view.showNews(news)
                        }

                        override fun onSubscribe(d: Disposable) {
//                            view.showNewsLoading()
                            println("Subscribed")
                            compositeDisposable?.add(d)
                        }

                        override fun onError(e: Throwable) {
//                            view.showNewsError()
                            println("onError: ${e.message}")
                        }
                    })

        } else {
//            view.showNoInternet()
        }
    }

    override fun detachView() {
        compositeDisposable?.dispose()
    }

    companion object {
        val TAG = "HomePresenter"
    }

}