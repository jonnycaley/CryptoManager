package com.jonnycaley.cryptomanager.ui.splash

import com.google.gson.Gson
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.Exchanges.Exchanges
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
                    }
                    .flatMap {
                        dataManager.getCryptoCompareService().getAllExchanges()
                    }
                    .map { response ->
                        dataManager.writeToStorage(Constants.PAPER_ALL_EXCHANGES, responseToArraysExchanges(response))
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

    private fun responseToArraysExchanges(response: String): String {

        val replacedNulls = response.replace("{}", "{\"null\":[\"null\"]}")

        val responseStepOne = replacedNulls.replace(":[", ",\"converters\":[")
        val responseStepTwo = responseStepOne.replace("]","]}")
        val responseStepThree = responseStepTwo.replace(":{",",\"symbols\":[{\"symbol\":")
        val responseStepFour = responseStepThree.replace("]},","]},{\"symbol\":")
        val responseStepFive = responseStepFour.replace("]}},","]}},{\"name\":")
        val responseStepSix = responseStepFive.replace("]}}","]}]}")

        val startString = responseStepSix.substring(0,responseStepSix.length - 1)
        val endString = responseStepSix[responseStepSix.length - 1]

        val responseStepSeven = "$startString]$endString"
        val responseStepEight = "{\"exchanges\":[{\"name\":${responseStepSeven.substring(1)}"

        return responseStepEight.trim()

    }

    private fun responseToArrays(response: String): String {
        val responseNew = response.replace("Data\":{\"","Data\":[\"")

        val responseInArray = responseNew.replace("}},\"BaseImage","}],\"BaseImage")

        val responseFirstIds = responseInArray.replaceFirst(("\\[\""+".*?"+"\":\\{\"Id\"").toRegex(), "[{\"Id\"")

        val responseRemainderIds = responseFirstIds.replace((",\"IsTrading\":"+".*?"+"\\},\""+".*?"+"\":\\{\"Id\"").toRegex(), "\\},{\"Id\"")

        return responseRemainderIds
    }

    override fun detachView() {
        compositeDisposable?.dispose()
    }

}