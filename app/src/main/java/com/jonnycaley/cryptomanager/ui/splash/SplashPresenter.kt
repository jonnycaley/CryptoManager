package com.jonnycaley.cryptomanager.ui.splash

import com.jonnycaley.cryptomanager.utils.Constants
import io.paperdb.Paper
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class SplashPresenter(var dataManager: SplashDataManager, var view: SplashContract.View) : SplashContract.Presenter{

    var compositeDisposable: CompositeDisposable? = null

    init {
        this.view.setPresenter(this)
    }

    override fun attachView() {
        if (compositeDisposable == null || (compositeDisposable as CompositeDisposable).isDisposed) {
            compositeDisposable = CompositeDisposable()
        }
        //start stuff here

        if(dataManager.checkConnection()){

            dataManager.getCryptoCompareService().getAllCurrencies()
                    .map { response ->

                        dataManager.writeToStorage(Constants.PAPER_ALL_CURRENCIES, responseToArrays(response))

                        return@map true
                    }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : SingleObserver<Boolean?> {
                        override fun onSuccess(t: Boolean) {
                            view.toBaseActivity()
                        }

                        override fun onSubscribe(d: Disposable) {

                        }

                        override fun onError(e: Throwable) {
                            println("onError: ${e.message}")
                        }

                    })
        } else {

        }
    }

    private fun responseToArrays(response: String): String {
        var responseNew = response.replace("Data\":{\"","Data\":[\"")
        var responseInArray = responseNew.replace("}},\"BaseImage","}],\"BaseImage")

//        var tester = "helloahahahahhahme{"
//        println(tester.replace(("\""+".*?"+"\":\\{\"Id\"").toRegex(), "{\"Id\""))
//
//        var test = "\"42\":{\"Id\""
//
//        println(test.replace((".*?\").toRegex()}:{\"Id\"", "{\"Id\""))

        println(responseInArray)

        var responseFirstIds = responseInArray.replaceFirst(("\\[\""+".*?"+"\":\\{\"Id\"").toRegex(), "[{\"Id\"")

//        ",\"IsTrading\":"+".*?"+"\\},"

        var responseRemainderIds = responseFirstIds.replace((",\"IsTrading\":"+".*?"+"\\},\""+".*?"+"\":\\{\"Id\"").toRegex(), "\\},{\"Id\"")

//        ".....":{"Id"

        return responseRemainderIds
    }

    override fun detachView() {
        compositeDisposable?.dispose()
    }

}