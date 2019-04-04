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
                            view.loadTheme(info)
                        }

                        override fun onSubscribe(d: Disposable) {
                            compositeDisposable?.add(d)
                        }

                        override fun onError(e: Throwable) {
                            view.showNoDataAvailable()
                            view.onBackPressed()
                            println("onError11: ${e.message}")
                        }
                    })

        } else {
//            view.showNoInternet()
            //TODO: GET IMAGE FROM STORAGE SLOOOOOOOOWER
        }
    }

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