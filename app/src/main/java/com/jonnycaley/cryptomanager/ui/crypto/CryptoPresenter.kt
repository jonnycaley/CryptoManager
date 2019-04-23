package com.jonnycaley.cryptomanager.ui.crypto

import android.util.Log
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.GeneralInfo.GeneralInfo
import io.reactivex.Observer
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class CryptoPresenter (var dataManager: CryptoDataManager, var view: CryptoContract.View) : CryptoContract.Presenter{

    var compositeDisposable: CompositeDisposable? = null

    init {
        this.view.setPresenter(this)
    }

    override fun attachView() {
        if (compositeDisposable == null || (compositeDisposable as CompositeDisposable).isDisposed) {
            compositeDisposable = CompositeDisposable()
        }

        getCoinColorScheme()
    }

    /*
    Function gets the coin color scheme and loads the theme with the response
    */
    override fun getCoinColorScheme() {

        if(dataManager.checkConnection()){
            dataManager.getCryptoCompareService().getGeneralInfo(view.getSymbol(), "USD")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<GeneralInfo> {
                        override fun onComplete() {
                        }

                        override fun onNext(info: GeneralInfo) {
//                            Log.i(TAG, "Loading Theme: ${info.data?.first()?.coinInfo?.imageUrl}")
                            view.loadTheme(info) //load the theme
                        }

                        override fun onSubscribe(d: Disposable) {
                            compositeDisposable?.add(d)
                        }

                        override fun onError(e: Throwable) {
                            view.showNoDataAvailable()
                            view.onBackPressed() //backpressed to previous activity
                            println("onError11: ${e.message}")
                        }
                    })

        } else {
//            view.showNoInternet()
        }
    }

    /*
    Function returns the internet connection status
    */
    override fun checkInternet(): Boolean {
        return dataManager.checkConnection()
    }

    override fun detachView() {
        compositeDisposable?.dispose()
    }

    companion object {
        val TAG = "CryptoPresenter"
    }

}