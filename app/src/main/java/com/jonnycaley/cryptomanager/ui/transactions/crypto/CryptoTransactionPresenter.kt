package com.jonnycaley.cryptomanager.ui.transactions.crypto

import android.util.Log
import com.google.gson.Gson
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.AllCurrencies.Currencies
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.PriceAtTimestampForReal.Price
import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import com.jonnycaley.cryptomanager.utils.JsonModifiers
import io.reactivex.CompletableObserver
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function6
import io.reactivex.schedulers.Schedulers
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList

class CryptoTransactionPresenter(var dataManager: CryptoTransactionDataManager, var view: CryptoTransactionContract.View) : CryptoTransactionContract.Presenter {

    var compositeDisposable: CompositeDisposable? = null

    init {
        this.view.setPresenter(this)
    }

    override fun attachView() {
        if (compositeDisposable == null || (compositeDisposable as CompositeDisposable).isDisposed) {
            compositeDisposable = CompositeDisposable()
        }
    }

    override fun detachView() {
        compositeDisposable?.dispose()
    }


    /*
    Function updates a crypot transaciont
    */
    override fun updateCryptoTransaction(transaction: Transaction, isBuy: Boolean, exchange: String, pair: String, price: Float, quantity: Float, date: Date, isDeducted: Boolean, notes: String) {

        var correctQuantity = quantity.toBigDecimal()
        var priceUsd = 1.toBigDecimal()
        var isDeductedPriceUsd = 1.toBigDecimal()
        var allCryptos: Currencies? = null
        var btcPrice = 1.toBigDecimal()
        var ethPrice = 1.toBigDecimal()

        if (!isBuy)
            correctQuantity *= (-1).toBigDecimal()

        if (dataManager.checkConnection()) {

            val getBtcPrice : Observable<String> = dataManager.getCryptoCompareServiceWithScalars().getPriceAtTimestamp("BTC", "USD", date.time.toString().substring(0, date.time.toString().length - 3))
            val getEthPrice : Observable<String> = dataManager.getCryptoCompareServiceWithScalars().getPriceAtTimestamp("ETH", "USD", date.time.toString().substring(0, date.time.toString().length - 3))
            val getIsDeductedPrice : Observable<String> = dataManager.getCryptoCompareServiceWithScalars().getPriceAtTimestamp(pair, "USD", date.time.toString().substring(0, date.time.toString().length - 3))
            val getPriceUsd : Observable<String> = dataManager.getCryptoCompareServiceWithScalars().getPriceAtTimestamp(view.getSymbol(), "USD", date.time.toString().substring(0, date.time.toString().length - 3))

            val getAllCrypto : Observable<Currencies> = dataManager.getAllCryptos().toObservable()
            val getTransactions : Observable<ArrayList<Transaction>> = dataManager.getTransactions().toObservable()

            Observable.zip(getBtcPrice, getEthPrice, getIsDeductedPrice, getPriceUsd, getAllCrypto, getTransactions, Function6<String, String, String, String, Currencies, ArrayList<Transaction>, ArrayList<Transaction>> { res1, res2, res3, res4, res5, transactions ->
                println("BTCPRICE $res1")
                val gson1= Gson().fromJson(JsonModifiers.jsonToTimeStampPrice(res1), Price::class.java)
                gson1.uSD?.let { btcPrice = it }

                val gson2 = Gson().fromJson(JsonModifiers.jsonToTimeStampPrice(res2), Price::class.java)
                gson2.uSD?.let { ethPrice = it }

                val gson3 = Gson().fromJson(JsonModifiers.jsonToTimeStampPrice(res3), Price::class.java)
                gson3.uSD?.let { isDeductedPriceUsd = it }

                val gson4 = Gson().fromJson(JsonModifiers.jsonToTimeStampPrice(res4), Price::class.java)
                gson4.uSD?.let { priceUsd = it }

                allCryptos = res5

                transactions
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.computation())
                    .map { transactions ->

                        transactions.forEach { Log.i(TAG, it.date.time.toString()) }
                        val newTransaction = Transaction(exchange, view.getSymbol(), transaction.name, pair, correctQuantity, price.toBigDecimal(), priceUsd, date, notes, isDeducted, isDeductedPriceUsd, allCryptos?.baseImageUrl + allCryptos?.data?.firstOrNull { it.symbol == view.getSymbol() }?.imageUrl, allCryptos?.baseImageUrl + allCryptos?.data?.firstOrNull { it.symbol == pair }?.imageUrl, btcPrice, ethPrice)
                        transactions.remove(transaction)
                        transactions.add(newTransaction)
                        transactions
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<java.util.ArrayList<Transaction>> {

                        override fun onComplete() {
                        }

                        override fun onNext(transactions: java.util.ArrayList<Transaction>) {
                            saveTransactions(transactions)
                        }

                        override fun onSubscribe(d: Disposable) {
                            if(isBuy){
                                view.startUpdateBuyProgress()
                            } else {
                                view.startUpdateSellProgress()
                            }
                            view.disableTouchEvents()
                            compositeDisposable?.add(d)
                        }

                        override fun onError(e: Throwable) {
                            if(isBuy){
                                view.stopUpdateBuyProgress()
                            } else {
                                view.stopUpdateSellProgress()
                            }
                            view.enableTouchEvents()
                            view.showError()
                            println("onError: ${e.message}")
                        }
                    })
        } else {
            view.showNoInternet()
        }
    }

    /*
    Function gets the current price
    */
    override fun getCurrentPrice(transactionSymbol: String, pair: String) {

        if(dataManager.checkConnection()){

            dataManager.getCryptoCompareServiceWithScalars().getCurrentPriceScalar(transactionSymbol, pair)
                    .subscribeOn(Schedulers.io())
                    .map { json ->
                        JsonModifiers.jsonToSinglePrice(json)
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<String> {

                        override fun onComplete() {
                        }

                        override fun onNext(price: String) {

                            if(price.toDouble() > 1)
                                view.showCurrentPrice(com.jonnycaley.cryptomanager.utils.Utils.toDecimals(price.toBigDecimal(), 2))
                            else
                                view.showCurrentPrice(com.jonnycaley.cryptomanager.utils.Utils.toDecimals(price.toBigDecimal(), 6))
                        }

                        override fun onSubscribe(d: Disposable) {
                            compositeDisposable?.add(d)
                        }

                        override fun onError(e: Throwable) {
                            println("onError: ${e.message}")
                        }
                    })

        } else {

        }
    }

    /*
    Function deletes a transaction
    */
    override fun deleteTransaction(transaction: Transaction) {

        dataManager.getTransactions()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<ArrayList<Transaction>> {
                    override fun onSuccess(transactions: ArrayList<Transaction>) {
                        saveTransactions(ArrayList(transactions.filter { it != transaction }))
                    }

                    override fun onSubscribe(d: Disposable) {
                        view.disableTouchEvents()
                        compositeDisposable?.add(d)
                    }

                    override fun onError(e: Throwable) {
                        view.disableTouchEvents()
                        view.showError()
                        println("onError: ${e.message}")
                    }
                })
    }

    override fun createCryptoTransaction(isBuy: Boolean, exchange: String, pair: String, price: Float, quantity: Float, date: Date, isDeducted: Boolean, notes: String) {

        var correctQuantity = quantity
        var priceUsd = 1.toBigDecimal()
        var isDeductedPriceUsd = 1.toBigDecimal()
        var btcPrice = 1.toBigDecimal()
        var ethPrice = 1.toBigDecimal()

        var allCryptos: Currencies? = null

        if (!isBuy)
            correctQuantity *= -1

        if (dataManager.checkConnection()) {


            val getBtcPrice : Observable<String> = dataManager.getCryptoCompareServiceWithScalars().getPriceAtTimestamp("BTC", "USD", date.time.toString().substring(0, date.time.toString().length - 3))
            val getEthPrice : Observable<String> = dataManager.getCryptoCompareServiceWithScalars().getPriceAtTimestamp("ETH", "USD", date.time.toString().substring(0, date.time.toString().length - 3))
            val getIsDeductedPrice : Observable<String> = dataManager.getCryptoCompareServiceWithScalars().getPriceAtTimestamp(pair, "USD", date.time.toString().substring(0, date.time.toString().length - 3))
            val getPriceUsd : Observable<String> = dataManager.getCryptoCompareServiceWithScalars().getPriceAtTimestamp(view.getSymbol(), "USD", date.time.toString().substring(0, date.time.toString().length - 3))

            val getAllCrypto : Observable<Currencies> = dataManager.getAllCryptos().toObservable()
            val getTransactions : Observable<ArrayList<Transaction>> = dataManager.getTransactions().toObservable()

            Observable.zip(getBtcPrice, getEthPrice, getIsDeductedPrice, getPriceUsd, getAllCrypto, getTransactions, Function6<String, String, String, String, Currencies, ArrayList<Transaction>, ArrayList<Transaction>> { res1, res2, res3, res4, res5, transactions ->
                val gson1= Gson().fromJson(JsonModifiers.jsonToTimeStampPrice(res1), Price::class.java)
                gson1.uSD?.let { btcPrice = it }

                val gson2 = Gson().fromJson(JsonModifiers.jsonToTimeStampPrice(res2), Price::class.java)
                gson2.uSD?.let { ethPrice = it }

                val gson3 = Gson().fromJson(JsonModifiers.jsonToTimeStampPrice(res3), Price::class.java)
                gson3.uSD?.let { isDeductedPriceUsd = it }

                val gson4 = Gson().fromJson(JsonModifiers.jsonToTimeStampPrice(res4), Price::class.java)
                gson4.uSD?.let { priceUsd = it }

                allCryptos = res5

                transactions
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.computation())
                    .map { transactions ->

                        transactions.forEach { Log.i(TAG, it.date.time.toString()) }

                        val newTransaction = Transaction(exchange, view.getSymbol(), view.getName(), pair, correctQuantity.toBigDecimal(), price.toBigDecimal(), priceUsd, date, notes, isDeducted, isDeductedPriceUsd, allCryptos?.baseImageUrl + allCryptos?.data?.firstOrNull { it.symbol == view.getSymbol() }?.imageUrl, allCryptos?.baseImageUrl + allCryptos?.data?.firstOrNull { it.symbol == pair }?.imageUrl, btcPrice, ethPrice)
                        transactions.add(newTransaction)
                        return@map transactions
                    }
                    .observeOn(Schedulers.io())
                    .flatMapCompletable { transactions -> dataManager.saveTransactions(transactions) }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : CompletableObserver {

                        override fun onComplete() {
                            view.onTransactionComplete()
                        }

                        override fun onSubscribe(d: Disposable) {
                            if(isBuy){
                                view.startCreateBuyProgress()
                            } else {
                                view.startCreateSellProgress()
                            }
                            view.disableTouchEvents()
                            compositeDisposable?.add(d)
                        }

                        override fun onError(e: Throwable) {
                            if(isBuy){
                                view.stopCreateBuyProgress()
                            } else {
                                view.stopCreateSellProgress()
                            }
                            view.enableTouchEvents()
                            view.showError()
                            println("onError: ${e.message}")
                        }
                    })
        } else {
            view.showNoInternet()
        }
    }

    private fun saveTransactions(transactions: ArrayList<Transaction>) {
        dataManager.saveTransactions(transactions)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        view.onTransactionComplete()
                    }

                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable?.add(d)
                    }

                    override fun onError(e: Throwable) {
                        view.enableTouchEvents()
                        view.showError()
                        println("onError: ${e.message}")
                    }
                })
    }

    override fun getAllHoldings(symbol: String) {

        val transactionz : List<Transaction>? = null

        dataManager.getTransactions()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map { transactions -> transactions.filter { it.symbol == symbol || ((it.pairSymbol == symbol) && (it.isDeducted)) } }
                .map { trans ->
                    getAvailableCryptoCount(trans, symbol)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<BigDecimal> {
                    override fun onSuccess(amount: BigDecimal) {
                        view.showSellAllAmount(amount)
                    }

                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable?.add(d)
                    }

                    override fun onError(e: Throwable) {
                        println("onError")
                    }
                })
    }


    private fun getAvailableCryptoCount(transactions: List<Transaction>, symbol : String): BigDecimal {
        var availableFiat = 0.toBigDecimal()

        transactions.filter { it.symbol == symbol }.forEach { availableFiat += it.quantity }
        transactions.filter { (it.pairSymbol == symbol) && (it.isDeducted) }.forEach{ availableFiat -= (it.price * it.quantity) }

        return availableFiat
    }


    companion object {
        val TAG = "UpdateCryptoTransPres"
    }
}