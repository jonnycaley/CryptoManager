package com.jonnycaley.cryptomanager.ui.home

import android.annotation.SuppressLint
import android.util.Log
import com.google.gson.Gson
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.HistoricalData.HistoricalData
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.MultiPrice.MultiPrices
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.MultiPrice.Price
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.MultiPrice.Prices
import com.jonnycaley.cryptomanager.data.model.DataBase.Holding
import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import com.jonnycaley.cryptomanager.data.model.DataBase.Variables
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.ExchangeRates
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.Rate
import com.jonnycaley.cryptomanager.utils.Constants
import com.jonnycaley.cryptomanager.utils.JsonModifiers
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.*
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.functions.BiFunction
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.collections.ArrayList


class HomePresenter(var dataManager: HomeDataManager, var view: HomeContract.View) : HomeContract.Presenter {

    var compositeDisposable: CompositeDisposable? = null

    val TAG = "HomePresenter"

    var holdings: ArrayList<Holding> = ArrayList()
    var prices: ArrayList<Price> = ArrayList()
    //    var baseRate: Rate = Rate()
    var priceBtc = Price()
    var priceEth = Price()
    var balanceUsd = 0.toBigDecimal()
    var balanceBtc = 0.toBigDecimal()
    var balanceEth = 0.toBigDecimal()
    var changeUsd = 0.toBigDecimal()
    var changeBtc = 0.toBigDecimal()
    var changeEth = 0.toBigDecimal()
    var allFiats: ArrayList<Rate> = ArrayList()

    init {
        this.view.setPresenter(this)
    }

    override fun attachView() {
        if (compositeDisposable == null || (compositeDisposable as CompositeDisposable).isDisposed) {
            compositeDisposable = CompositeDisposable()
        }

        getFiats()
    }

    private fun getFiats() {

        dataManager.readFiats()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<ExchangeRates> {
                    override fun onSuccess(exchangeRates: ExchangeRates) {
                        saveFiats(exchangeRates.rates)
                    }

                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable?.add(d)
                    }

                    override fun onError(e: Throwable) {
                        Log.i(TAG, "onError: ${e.message}")
                    }
                })
    }


    fun saveFiats(rates: List<Rate>?) {
        allFiats.clear()
        rates?.forEach { allFiats.add(it) }
    }

    override fun getTransactions(timePeriod: String) {

        println("getting transactions")

        dataManager.getTransactions()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<ArrayList<Transaction>> {

                    override fun onSuccess(transactions: ArrayList<Transaction>) {
                        if (transactions.isEmpty()) {
                            view.showNoHoldingsLayout()
                            view.hideRefreshing()
                            view.hideInternetRequiredLayout()
                            view.hideProgressLayout()
                        } else {
                            getHistoricalBtcEthPrices(combineTransactions(transactions), transactions, timePeriod)
                        }
                    }

                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable?.add(d)
                        view.showRefreshing()
                    }

                    override fun onError(e: Throwable) {
                        Log.i(TAG, "onError1: ${e.message}")
                        view.stopRefreshing()
                        view.showError()
                        view.hideInternetRequiredLayout()
                    }
                })
    }

    var priceBtcHistorical = Price() //TODO: DO WE NEED TO DO THESE OR CAN WE SAVE THEM TO THE ACTIVITY LIKE WE DO AT THE END AND THEN REFERNENCE THEM THEN?
    var priceEthHistorical = Price() //TODO: DO WE NEED TO DO THESE OR CAN WE SAVE THEM TO THE ACTIVITY LIKE WE DO AT THE END AND THEN REFERNENCE THEM THEN?

    private fun getHistoricalBtcEthPrices(holdings: ArrayList<Holding>, transactions: ArrayList<Transaction>, timePeriod: String) {

        if (dataManager.checkConnection()) {

            var timePeriodStr = ""
            var aggregateStr = ""

            when (timePeriod) {
                HomeFragment.TIME_PERIOD_1H -> {
                    timePeriodStr = HomeFragment.TIME_PERIOD_MINUTE; aggregateStr = HomeFragment.AGGREGATE_1H
                }
                HomeFragment.TIME_PERIOD_1D -> {
                    timePeriodStr = HomeFragment.TIME_PERIOD_HOUR; aggregateStr = HomeFragment.AGGREGATE_1D
                }
                HomeFragment.TIME_PERIOD_1W -> {
                    timePeriodStr = HomeFragment.TIME_PERIOD_HOUR; aggregateStr = HomeFragment.AGGREGATE_1W
                }
                else -> {
                    timePeriodStr = HomeFragment.TIME_PERIOD_DAY; aggregateStr = HomeFragment.AGGREGATE_1M
                }
            }

            val getBtcPrice: Observable<HistoricalData> = dataManager.getCryptoCompareService().getPriceAt(timePeriodStr, "BTC", "USD", aggregateStr)
            val getEthPrice: Observable<HistoricalData> = dataManager.getCryptoCompareService().getPriceAt(timePeriodStr, "ETH", "USD", aggregateStr)

            Observable.zip(getBtcPrice, getEthPrice, BiFunction<HistoricalData, HistoricalData, Unit> { btcResponse, ethResponse ->

                val tempPriceBtc = Price()
                tempPriceBtc.symbol = "BTC"
                val tempPricesBtc = Prices()
                tempPricesBtc.uSD = btcResponse.data?.first()?.close
                tempPriceBtc.prices = tempPricesBtc
                priceBtcHistorical = tempPriceBtc

                val tempPriceEth = Price()
                tempPriceEth.symbol = "ETH"
                val tempPricesEth = Prices()
                tempPricesEth.uSD = ethResponse.data?.first()?.close
                tempPriceEth.prices = tempPricesEth
                priceEthHistorical = tempPriceEth

            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<Unit> {

                        override fun onComplete() {
                            getHistoricalPrices(holdings, transactions, timePeriod)
                        }

                        override fun onNext(cryptoPrices: Unit) {
                        }

                        override fun onSubscribe(d: Disposable) {
                            compositeDisposable?.add(d)
                        }

                        override fun onError(e: Throwable) {
                            Log.i(TAG, "onError4: ${e.message}")
                            view.stopRefreshing()
                            view.hideProgressLayout()
                            view.showError()
                        }

                    })
//            dataManager.getCryptoCompareService().getPriceAt(timePeriodStr, "BTC", "USD", aggregateStr)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(Schedulers.computation())
//                    .map { btcResponse ->
//
//                        val tempPrice = Price()
//                        tempPrice.symbol = "BTC"
//                        val tempPrices = Prices()
//                        tempPrices.uSD = btcResponse.data?.first()?.close
//                        tempPrice.prices = tempPrices
//                        priceBtcHistorical = tempPrice
//
////                        var newHoldingCostBtc = 0.toDouble()
//
////                        holdings.forEach {
////                            Log.i(TAG, "${it.symbol} BTC cost: ${it.costBtc}")
////                            Log.i(TAG, "${it.symbol} ETH cost: ${it.costEth}")
////                        }
//
////                        holdings.forEach { holding ->
//
////                            transactions.filter { it.symbol.toUpperCase() == holding.symbol.toUpperCase() }
////                                    .forEach { transaction ->
////                                        if (transaction.date < getDate(timePeriod) && timePeriod != HomeFragment.TIME_PERIOD_ALL) {
////                                            Log.i(TAG, "quantity: ${transaction.quantity}, price: ${transaction.price}, isDeductedPriceUsd: ${transaction.isDeductedPriceUsd}, btcPrice: ${btcResponse.data?.get(0)?.close!!}, ")
////                                            newHoldingCostBtc += ((transaction.quantity * transaction.priceUSD) / btcResponse.data?.get(0)?.close!!)
////
////                                            Log.i(TAG, "Primary symbol ${transaction.symbol}")
////                                            Log.i(TAG, "Adding: ${((transaction.quantity * transaction.price) / btcResponse.data?.get(0)?.close!!)}")
////                                        } else {
////                                            newHoldingCostBtc += ((transaction.quantity * transaction.priceUSD) / transaction.btcPrice)                                   // transaction.price * transaction.isDeductedPriceUsd  used to be transaction.priceUsd but that gives us the price at the time and not the price we paid for it!!!!!!!!!!
////
////
////                                            Log.i(TAG, "pricePaid: ${(transaction.quantity * transaction.price * transaction.isDeductedPriceUsd)}; btcPrice: ${transaction.btcPrice}; ")
////                                            Log.i(TAG, "New holding from creation price: ${((transaction.quantity * transaction.price * transaction.isDeductedPriceUsd) / transaction.btcPrice) }")
////                                        }
////                                    }
////
////                            transactions.filter { (it.pairSymbol?.toUpperCase() == holding.symbol.toUpperCase()) && it.isDeducted }
////                                    .forEach { transaction ->
////                                        if (transaction.date < getDate(timePeriod) && timePeriod != HomeFragment.TIME_PERIOD_ALL) {
////                                            Log.i(TAG, "Secondary symbol ${transaction.pairSymbol}")
////                                            newHoldingCostBtc -= ((transaction.price * transaction.quantity * transaction.isDeductedPriceUsd) / (btcResponse.data?.get(0)?.close!!))
////                                            Log.i(TAG, "Bought before date -> get priceBtc at time as valid")
////
////                                        } else {
////                                            Log.i(TAG, "Bought before date -> get priceBtc at time of purchase")
//////                                            Log.i(TAG, "Get price from bought")
//////                                            Log.i(TAG, transaction.price)
//////                                            Log.i(TAG, transaction.quantity)
//////                                            Log.i(TAG, transaction.isDeductedPriceUsd)
//////                                            Log.i(TAG, transaction.btcPrice)
////                                            newHoldingCostBtc -= (transaction.price * transaction.quantity * transaction.isDeductedPriceUsd / (transaction.btcPrice))
////                                        }
////                                    }
//
//
////                            Log.i(TAG, "Old costBtc: ${holding.costBtc} for ${holding.symbol}")
////                            Log.i(TAG, "New costBtc: $newHoldingCostBtc for ${holding.symbol}")
//
//
////                            holding.costBtc = newHoldingCostBtc
////                        }
//                    }
//                    .observeOn(Schedulers.io())
//                    .flatMap { dataManager.getCryptoCompareService().getPriceAt(timePeriodStr, "ETH", "USD", aggregateStr) }
//                    .observeOn(Schedulers.computation())
//                    .map { ethResponse ->
//
//                        val tempPrice = Price()
//                        tempPrice.symbol = "ETH"
//                        val tempPrices = Prices()
//                        tempPrices.uSD = ethResponse.data?.first()?.close
//
//                        tempPrice.prices = tempPrices
//                        priceEthHistorical = tempPrice
////                        var newHoldingCostEth = 0.toDouble()
////
////                        holdings.forEach { holding ->
////
////                            transactions.filter { it.symbol.toUpperCase() == holding.symbol.toUpperCase() }
////                                    .forEach { transaction ->
////                                        if (transaction.date < getDate(timePeriod) && timePeriod != HomeFragment.TIME_PERIOD_ALL) {
////                                            newHoldingCostEth += ((transaction.quantity * transaction.price * transaction.isDeductedPriceUsd) / (ethResponse.data?.get(0)?.close!!))
//////                                            Log.i(TAG, "Get price from before")
//////                                            Log.i(TAG, transaction.priceUSD * transaction.quantity)
//////                                            Log.i(TAG, ethResponse.data?.get(0)?.close!!)
////                                        } else {
////                                            newHoldingCostEth += ((transaction.quantity * transaction.price * transaction.isDeductedPriceUsd) / transaction.ethPrice)
//////                                            Log.i(TAG, "Get price from bought")
//////                                            Log.i(TAG, transaction.priceUSD)
//////                                            Log.i(TAG, transaction.ethPrice)
//////                                        Log.i(TAG, transaction.isDeductedPriceUsd)
////                                        }
////                                    }
////
////                            transactions.filter { (it.pairSymbol?.toUpperCase() == holding.symbol.toUpperCase()) && it.isDeducted }
////                                    .forEach { transaction ->
////                                        if (transaction.date < getDate(timePeriod) && timePeriod != HomeFragment.TIME_PERIOD_ALL) {
////                                            newHoldingCostEth -= (transaction.price * transaction.quantity * transaction.isDeductedPriceUsd / (ethResponse.data?.get(0)?.close!!))
//////                                            Log.i(TAG, "Get price from before")
//////                                            print("price: " + transaction.price)
//////                                            print("quantity: " + transaction.quantity)
//////                                            Log.i(TAG, "isDeductedPriceUsd" + transaction.isDeductedPriceUsd)
//////                                            Log.i(TAG, "Price usd time related:" + ethResponse.data?.get(0)?.close!!)
//////                                            Log.i(TAG, "${transaction.isDeductedPriceUsd}")
////
////                                        } else {
//////                                            Log.i(TAG, "Get price from bought")
//////                                            Log.i(TAG, transaction.price)
//////                                            Log.i(TAG, transaction.quantity)
//////                                            Log.i(TAG, transaction.isDeductedPriceUsd)
//////                                            Log.i(TAG, transaction.ethPrice)
////                                            newHoldingCostEth -= (transaction.price * transaction.quantity * transaction.isDeductedPriceUsd / (transaction.ethPrice))
////                                        }
////
////                                    }
////
////
////                            Log.i(TAG, "Old costEth: ${holding.costEth} for ${holding.symbol}")
////                            Log.i(TAG, "New costEth: $newHoldingCostEth for ${holding.symbol}")
////
////                            holding.costEth = newHoldingCostEth
////                        }
//                    }
//                    .map {
//                        getHistoricalPrices(holdings, transactions, timePeriod)
//                    }
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(object : Observer<Unit> {
//
//                        override fun onComplete() {
//                        }
//
//                        override fun onNext(cryptoPrices: Unit) {
//                        }
//
//                        override fun onSubscribe(d: Disposable) {
//                            compositeDisposable?.add(d)
//                        }
//
//                        override fun onError(e: Throwable) {
//                            Log.i(TAG, "onError4: ${e.message}")
//                            view.stopRefreshing()
//                            view.showError()
//                        }
//
//                    })
        } else {
            view.hideRefreshing()
            view.showNoInternet()
        }
    }

    @SuppressLint("CheckResult")
    private fun getHistoricalPrices(holdings: ArrayList<Holding>, transactions: ArrayList<Transaction>, timePeriod: String) {

        var cryptoSymbol: String = ""

        val prices = ArrayList<com.jonnycaley.cryptomanager.data.model.CryptoCompare.PriceAtTimestampForReal.Price>()

        var usdPrice = 1.toBigDecimal()
        var ethPrice = 1.toBigDecimal()
        var btcPrice = 1.toBigDecimal()

        var count = 0

        if (dataManager.checkConnection()) {

            val cal = Calendar.getInstance()
            cal.add(Calendar.HOUR, -1)
            val oneHourBack = cal.time
            val timeStamp = oneHourBack.time / 100

            val observables: ArrayList<Observable<String>> = ArrayList()

            holdings.forEach {
                observables.add(dataManager.getCryptoCompareServiceWithScalars().getPriceAtTimestamp(it.symbol, "USD,BTC,ETH", timeStamp.toString()))
            }

            Observable.zip(observables) { res ->
                println("SeeHere")
                res.forEach { jsonNoString ->

                    var json = jsonNoString.toString()

                    val gson = Gson().fromJson(JsonModifiers.jsonToTimeStampPrice(json), com.jonnycaley.cryptomanager.data.model.CryptoCompare.PriceAtTimestampForReal.Price::class.java)

                    var newHoldingCost = 0.toBigDecimal()

                    println("Here we go")
                    println(json)
                    var str = json.substring(2).indexOf("\"") + 2
                    println(str)

                    cryptoSymbol = json.substring(2, json.substring(2).indexOf("\"") + 2)

                    gson.symbol = cryptoSymbol

                    transactions.filter { it.symbol.toUpperCase() == cryptoSymbol?.toUpperCase() }
                            .forEach { transaction ->
                                if (transaction.date < getDate(timePeriod) && (timePeriod != HomeFragment.TIME_PERIOD_ALL)) {
                                    if (gson.uSD != null) //for fiat prices like usd/usd returns error with empty data
                                        newHoldingCost += (transaction.quantity * (gson.uSD
                                                ?: 0.toBigDecimal()))
                                    else
                                        newHoldingCost += (transaction.quantity)
                                } else {
                                    newHoldingCost += (transaction.quantity * transaction.price * transaction.isDeductedPriceUsd)
                                }
                            }

                    transactions.filter { it.pairSymbol?.toUpperCase() == cryptoSymbol?.toUpperCase() && it.isDeducted }
                            .forEach { transaction ->
                                if (transaction.date < getDate(timePeriod) && (timePeriod != HomeFragment.TIME_PERIOD_ALL)) {
                                    if (gson.uSD != null) //for fiat prices like usd/usd returns error with empty data
                                        newHoldingCost -= (transaction.price * transaction.quantity * (gson.uSD
                                                ?: 0.toBigDecimal()))
                                    else
                                        newHoldingCost -= (transaction.price * transaction.quantity)

                                } else {
                                    newHoldingCost -= (transaction.quantity * transaction.price * transaction.isDeductedPriceUsd)
                                }

                            }

                    Log.i(TAG, "Altering holding price of $cryptoSymbol holding")

                    holdings.first { it.symbol.toUpperCase() == cryptoSymbol?.toUpperCase() }.costUsd = newHoldingCost

                    prices.add(gson)
                }
            }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<Unit> {

                        override fun onComplete() {
                            val time = System.currentTimeMillis()
                            android.util.Log.i("onComplete time end", " Time value in millisecinds $time")

                            setHistoricalBtcEthHoldings(prices, transactions, holdings, timePeriod)
                        }

                        override fun onNext(cryptoPrices: Unit) {
                        }

                        override fun onSubscribe(d: Disposable) {
                            val time = System.currentTimeMillis()
                            android.util.Log.i("onSubscribe time start", " Time value in millisecinds $time")
                            compositeDisposable?.add(d)
                        }

                        override fun onError(e: Throwable) {
                            Log.i(TAG, "onError3: ${e.cause}")
                            Log.i(TAG, "onError3: ${e.message}")
                            Log.i(TAG, "onError3: ${e.localizedMessage}")
                            view.stopRefreshing()
                            view.showError()
                            view.hideProgressLayout()
                        }

                    })

        } else {
            view.hideRefreshing()
            view.showNoInternet()
        }
    }

    private fun setHistoricalBtcEthHoldings(prices: ArrayList<com.jonnycaley.cryptomanager.data.model.CryptoCompare.PriceAtTimestampForReal.Price>, transactions: ArrayList<Transaction>, holdings: ArrayList<Holding>, timePeriod: String) {

        holdings.forEach { holding ->

            var newHoldingCostBtc = 0.toBigDecimal()
            var newHoldingCostEth = 0.toBigDecimal()

            transactions.filter { it.symbol.toUpperCase() == holding.symbol.toUpperCase() }
                    .forEach { transaction ->
                        if (transaction.date < getDate(timePeriod) && timePeriod != HomeFragment.TIME_PERIOD_ALL) {

                            var price = 1.toBigDecimal()

                            if (prices.first { it.symbol?.toUpperCase() == transaction.symbol.toUpperCase() }.uSD != null) {
                                price = prices.first { it.symbol?.toUpperCase() == transaction.symbol.toUpperCase() }.uSD
                                        ?: 0.toBigDecimal()
                                Log.i(TAG, "Got price $price")
                            } else {
                                Log.i(TAG, "Haven't got price")
                            }

                            Log.i(TAG, (((transaction.quantity * price) / (priceBtcHistorical.prices?.uSD
                                    ?: 1.toBigDecimal())).toString()))
                            newHoldingCostBtc += ((transaction.quantity * price) / (priceBtcHistorical.prices?.uSD
                                    ?: 1.toBigDecimal()))
                            newHoldingCostEth += ((transaction.quantity * price) / (priceEthHistorical.prices?.uSD
                                    ?: 1.toBigDecimal()))
                        } else {

                            newHoldingCostBtc += ((transaction.quantity * transaction.price * transaction.isDeductedPriceUsd) / transaction.btcPrice)                                   // transaction.price * transaction.isDeductedPriceUsd  used to be transaction.priceUsd but that gives us the price at the time and not the price we paid for it!!!!!!!!!!
                            newHoldingCostEth += ((transaction.quantity * transaction.price * transaction.isDeductedPriceUsd) / transaction.ethPrice)
                        }
                    }

            transactions.filter { (it.pairSymbol?.toUpperCase() == holding.symbol.toUpperCase()) && it.isDeducted }
                    .forEach { transaction ->

                        if (transaction.date < getDate(timePeriod) && timePeriod != HomeFragment.TIME_PERIOD_ALL) {

                            var price = 1.toBigDecimal()

                            if (prices.first { it.symbol?.toUpperCase() == transaction.pairSymbol?.toUpperCase() }.uSD != null)
                                price = prices.first { it.symbol?.toUpperCase() == transaction.pairSymbol?.toUpperCase() }.uSD
                                        ?: 0.toBigDecimal()

                            newHoldingCostBtc -= ((transaction.price * transaction.quantity * price) / (priceBtcHistorical.prices?.uSD
                                    ?: 1.toBigDecimal()))
                            newHoldingCostEth -= ((transaction.price * transaction.quantity * price) / (priceEthHistorical.prices?.uSD
                                    ?: 1.toBigDecimal()))

                        } else {
                            newHoldingCostBtc -= (transaction.price * transaction.quantity * transaction.isDeductedPriceUsd / (transaction.btcPrice))
                            newHoldingCostEth -= (transaction.price * transaction.quantity * transaction.isDeductedPriceUsd / (transaction.ethPrice))
                        }
                    }

            Log.i(TAG, "Holding: ${holding.symbol} new costBtc: $newHoldingCostBtc new costEth: $newHoldingCostEth")

            holding.costBtc = newHoldingCostBtc
            holding.costEth = newHoldingCostEth

        }
        getLatestPrices(holdings)
    }

    var baseFiat: Rate = Rate()

    private fun getLatestPrices(holdings: ArrayList<Holding>) { //happens for the 'ALL' tab

        if (dataManager.checkConnection()) {

            var fiatPrices = ExchangeRates()

            var newPrices: ArrayList<Price> = ArrayList()

            var holdingsSorted: ArrayList<Holding> = ArrayList()

            var changeUsd = 0.toBigDecimal()

            val getExchangeRates: Observable<String> = dataManager.getExchangeRateService().getExchangeRates()
            val getMultiPrices: Observable<String> = dataManager.getCryptoCompareServiceWithScalars().getMultiPrice(getCryptoQueryString(holdings), "USD")

            Observable.zip(getExchangeRates, getMultiPrices, BiFunction<String, String, String> { fiatsJson, cryptoPricesJson ->
                println("Checkpoint 1")
                fiatPrices = Gson().fromJson(JsonModifiers.jsonToCurrencies(fiatsJson), ExchangeRates::class.java)
                println("Checkpoint 2")
                println(fiatsJson)
                newPrices = getPrices(Gson().fromJson(JsonModifiers.jsonToMultiPrices(cryptoPricesJson), MultiPrices::class.java).prices, fiatPrices)
                println("Checkpoint 3")
                ""
            })
                    .subscribeOn(Schedulers.io())
                    .map {
//                        println("Checkpoint 1")
                        holdingsSorted = ArrayList(holdings.sortedBy { holding -> newPrices.first { it.symbol?.toLowerCase() == holding.symbol.toLowerCase() }.prices?.uSD?.times(holding.quantity) }.asReversed())
//                        println("Checkpoint 2")
                    }
                    .flatMapSingle { dataManager.readBaseFiat() }
                    .observeOn(Schedulers.computation())
                    .map { fiat -> baseFiat = fiat }
                    .map { change ->
//                        println("Checkpoint 3")
                        changeUsd = getChangeUsd(holdingsSorted, newPrices) }
                    .map { getBalanceUsd(holdingsSorted, newPrices, fiatPrices) }
                    .map { balance ->
                        Log.i(TAG, balance.toString())
                        saveData(holdingsSorted, newPrices, baseFiat, newPrices.first { it.symbol?.toUpperCase() == "BTC" }, newPrices.first { it.symbol?.toUpperCase() == "ETH" }, balance, getBalanceBtc(holdingsSorted, newPrices, fiatPrices), getBalanceEth(holdingsSorted, newPrices, fiatPrices), changeUsd, getChangeBtc(holdingsSorted, newPrices), getChangeEth(holdingsSorted, newPrices))
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<Unit> {
                        override fun onComplete() {
                            updateView()
                        }

                        override fun onNext(baseFiat: Unit) {
                            view.hideRefreshing()
                            view.hideInternetRequiredLayout()
                            view.hideProgressLayout()
                        }

                        override fun onSubscribe(d: Disposable) {
                            compositeDisposable?.add(d)
                        }

                        override fun onError(e: Throwable) {
                            Log.i(TAG, "onError2: ${e.message}")
                            view.stopRefreshing()
                            view.showError()
                            view.hideInternetRequiredLayout()
                            view.hideProgressLayout()
                        }
                    })
//            dataManager.getExchangeRateService().getExchangeRates()
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(Schedulers.computation())
//                    .map { fiats -> fiatPrices = Gson().fromJson(JsonModifiers.jsonToCurrencies(fiats), ExchangeRates::class.java) }
//                    .observeOn(Schedulers.io())
//                    .flatMap { dataManager.getCryptoCompareServiceWithScalars().getMultiPrice(getCryptoQueryString(holdings), "USD") } // gets all crypto conversion rates for all time info
//                    .observeOn(Schedulers.computation())
//                    .map { json -> Gson().fromJson(JsonModifiers.jsonToMultiPrices(json), MultiPrices::class.java) }
//                    .map { multiPrices -> newPrices = getPrices(multiPrices.prices, fiatPrices) }
//                    .map { holdingsSorted = ArrayList(holdings.sortedBy { holding -> newPrices.first { it.symbol?.toLowerCase() == holding.symbol.toLowerCase() }.prices?.uSD?.times(holding.quantity) }.asReversed()) }
//                    .observeOn(Schedulers.io())
//                    .flatMapSingle { dataManager.readBaseFiat() }
//                    .observeOn(Schedulers.computation())
//                    .map { fiat -> baseFiat = fiat }
//                    .map { change -> changeUsd = getChangeUsd(holdingsSorted, newPrices) }
//                    .map { getBalanceUsd(holdingsSorted, newPrices) }
//                    .map { balance ->
//                        saveData(holdingsSorted, newPrices, baseFiat, newPrices.first { it.symbol?.toUpperCase() == "BTC" }, newPrices.first { it.symbol?.toUpperCase() == "ETH" }, balance, getBalanceBtc(holdingsSorted, newPrices), getBalanceEth(holdingsSorted, newPrices), changeUsd, getChangeBtc(holdingsSorted, newPrices), getChangeEth(holdingsSorted, newPrices))
//                    }
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(object : Observer<Unit> {
//                        override fun onComplete() {
//                            updateView()
//                        }
//
//                        override fun onNext(baseFiat: Unit) {
//                            view.hideRefreshing()
//                        }
//
//                        override fun onSubscribe(d: Disposable) {
//                            compositeDisposable?.add(d)
//                        }
//
//                        override fun onError(e: Throwable) {
//                            Log.i(TAG, "onError2: ${e.message}")
//                            view.stopRefreshing()
//                            view.showError()
//                        }
//                    })
        } else {
            view.hideRefreshing()
            view.showNoInternet()
        }
    }

    override fun updateView() {
        view.showHoldings(holdings, prices, baseFiat, allFiats)
        view.showBalance(baseFiat, balanceUsd, balanceBtc, balanceEth)
        view.showChange(baseFiat, balanceUsd, balanceBtc, balanceEth, changeUsd, changeBtc, changeEth)
        view.isNotColdStartup()
    }

    fun getDate(timePeriod: String): Date {
        val cal = Calendar.getInstance()

        when (timePeriod) {
            HomeFragment.TIME_PERIOD_1H -> {
                cal.add(Calendar.HOUR, -1)
            }
            HomeFragment.TIME_PERIOD_1D -> {
                cal.add(Calendar.DAY_OF_YEAR, -1)
            }
            HomeFragment.TIME_PERIOD_1W -> {
                cal.add(Calendar.DAY_OF_YEAR, -7)
            }
            HomeFragment.TIME_PERIOD_1M -> {
                cal.add(Calendar.DAY_OF_YEAR, -30)
            }
        }
        return cal.getTime()
    }

    private fun getPrices(prices: List<Price>?, fiatPrices: ExchangeRates): ArrayList<Price> {

        println("Getting prices")

        val combinedPrices = ArrayList(prices)

        fiatPrices.rates?.forEach { fiatRate ->

            println(fiatRate.fiat)

            val tempPrice = Price()
            tempPrice.symbol = fiatRate.fiat

            val tempPrices = Prices()

            println(fiatRate.rate)

            tempPrices.uSD = 1.toBigDecimal() / (fiatRate.rate ?: 1.toBigDecimal())
            tempPrice.prices = tempPrices

            combinedPrices.add(tempPrice)
        }

        println("Prices combined")

        return combinedPrices
    }

    private fun getCryptoQueryString(holdings: ArrayList<Holding>): String {

        var cryptoSymbolsQueryString = ""

        holdings.filter { it.type == Variables.Transaction.Type.crypto }.forEach { cryptoSymbolsQueryString += it.symbol + "," }

        cryptoSymbolsQueryString = if (cryptoSymbolsQueryString.isNotEmpty())
            cryptoSymbolsQueryString.substring(0, cryptoSymbolsQueryString.length - 1)
        else
            "BTC"

        if (!cryptoSymbolsQueryString.contains("BTC"))
            cryptoSymbolsQueryString += ",BTC"
        if (!cryptoSymbolsQueryString.contains("ETH"))
            cryptoSymbolsQueryString += ",ETH"

        return cryptoSymbolsQueryString
    }

    private fun getChangeUsd(holdings: ArrayList<Holding>, combinedPrices: ArrayList<Price>): BigDecimal {

        var change = 0.toBigDecimal()

        holdings.filter { it.type == Variables.Transaction.Type.crypto }.forEach { holding ->

            val price = combinedPrices.filter { it.symbol?.toLowerCase() == holding.symbol.toLowerCase() }[0].prices?.uSD

            val value = price?.times(holding.quantity)

            change += value?.minus(holding.costUsd) ?: 0.toBigDecimal()
        }

        return change
    }


    private fun getChangeBtc(holdings: ArrayList<Holding>, combinedPrices: ArrayList<Price>): BigDecimal {

        var change = 0.toBigDecimal()

        holdings.filter { it.symbol.toUpperCase() != "BTC" && it.type != Variables.Transaction.Type.fiat }.forEach { holding ->

            val costBtcHistorical = holding.costBtc //this is what it was at time stamp
            val costBtcNow = holding.quantity * (combinedPrices.first { it.symbol?.toUpperCase() == holding.symbol.toUpperCase() }.prices?.uSD ?: 0.toBigDecimal()) / (combinedPrices.first { it.symbol?.toUpperCase() == "BTC" }.prices?.uSD?: 0.toBigDecimal())

            change += costBtcNow - costBtcHistorical
//            if(holding.quantity < 0.toBigDecimal() )
//                change += costBtcHistorical?.minus(costBtcNow!!)
//            else
//
//            change += costBtcNow - costBtcHistorical
//            Log.i(TAG, change.toString())
//
//            Log.i(TAG, "Data for holding: ${holding.symbol}")
//
//            val btcPriceNow = combinedPrices.first { it.symbol?.toUpperCase() == "BTC" }.prices?.uSD //get the price of btc now
//            Log.i(TAG, "btcPriceNow: $btcPriceNow")
//
//            val costInBtc =  holding.costBtc
//            Log.i(TAG, "costInBtc: $costInBtc")
//
//            val worthInBtcNow = holding.costUsd / btcPriceNow!!
//            Log.i(TAG, "worthInBtcNow: $worthInBtcNow")
//
//            change += worthInBtcNow.minus(costInBtc)
//            Log.i(TAG, "change: $change")
        }
        return change
//        val btcThen = portfolioHistorical / priceBtcHistorical.prices?.uSD!!
//        val btcNow = portfolioNow / priceBtcNow.prices?.uSD!!
//
//        val btcDifference = btcNow - btcThen
//
//        Log.i(TAG, "priceBtcNow: ${priceBtcNow.prices!!.uSD}")
//        Log.i(TAG, "priceBtcThen: ${priceBtcHistorical.prices!!.uSD}")
//
//        Log.i(TAG, "Portfolio now: $portfolioNow")
//        Log.i(TAG, "Portfolio then: $portfolioHistorical")
//
//        Log.i(TAG, "portfolioBtcNow: $btcNow")
//        Log.i(TAG, "portfolioBtcThen: $btcThen")
//
//        Log.i(TAG, "btcDifference: $btcDifference")
//
//        return btcDifference
    }

    private fun getChangeEth(holdings: ArrayList<Holding>, combinedPrices: ArrayList<Price>): BigDecimal {

        var change = 0.toBigDecimal()

        holdings.filter { it.symbol.toUpperCase() != "ETH" && it.type != Variables.Transaction.Type.fiat }.forEach { holding ->

            val costEthHistorical = holding.costEth //this is what it was at time stamp
            val costEthNow = holding.quantity * (combinedPrices.first { it.symbol?.toUpperCase() == holding.symbol.toUpperCase() }.prices?.uSD ?: 0.toBigDecimal()) / (combinedPrices.first { it.symbol?.toUpperCase() == "ETH" }.prices?.uSD ?: 1.toBigDecimal())

            change += costEthNow - costEthHistorical
//            if(holding.quantity < 0.toBigDecimal() )
//                change += costEthHistorical?.minus(costEthNow!!)
//            else
//            change += costEthNow - costEthHistorical
//            Log.i(TAG, change.toString())
//            Log.i(TAG, "Data for holding: ${holding.symbol}")
//
//            val btcPriceNow = combinedPrices.first { it.symbol?.toUpperCase() == "ETH" }.prices?.uSD //get the price of btc now
//            Log.i(TAG, "btcPriceNow: ${btcPriceNow}")
//
//            val costInBtc =  holding.costEth
//            Log.i(TAG, "costInBtc: ${costInBtc}")
//
//            val worthInBtcNow = holding.costUsd / btcPriceNow!!
//            Log.i(TAG, "worthInBtcNow: ${worthInBtcNow}")
//
//            change += worthInBtcNow.minus(costInBtc)
//            Log.i(TAG, "change: ${change}")
        }
        Log.i(TAG, "Change ETH = $change")
        return change
//        val ethThen = portfolioHistorical / priceEthHistorical.prices?.uSD!!
//        val ethNow = portfolioNow / priceEthNow.prices?.uSD!!
//
//        val ethDifference = ethNow - ethThen
//
//        Log.i(TAG, "priceEthNow: ${priceEthNow.prices!!.uSD}")
//        Log.i(TAG, "priceEthThen: ${priceEthHistorical.prices!!.uSD}")
//
//        Log.i(TAG, "Portfolio now: $portfolioNow")
//        Log.i(TAG, "Portfolio then: $portfolioHistorical")
//
//        Log.i(TAG, "portfolioEthNow: $ethNow")
//        Log.i(TAG, "portfolioEthThen: $ethThen")
//
//        Log.i(TAG, "EthDifference: $ethDifference")
//
//        return ethDifference
    }


    private fun getBalanceUsd(holdings: ArrayList<Holding>, prices: ArrayList<Price>, fiatPrices: ExchangeRates): BigDecimal {

        var balance = 0.toBigDecimal()

        holdings.filter { it.type == Variables.Transaction.Type.crypto }.forEach { holding ->
            Log.i("BalanceAdd", (prices.first { it.symbol?.toLowerCase() == holding.symbol.toLowerCase() }.prices?.uSD?.times(holding.quantity) ?: 0.toBigDecimal()).toString())

            balance += prices.first { it.symbol?.toLowerCase() == holding.symbol.toLowerCase() }.prices?.uSD?.times(holding.quantity) ?: 0.toBigDecimal()
        }

        holdings.filter { it.type == Variables.Transaction.Type.fiat }.forEach { holding ->
            Log.i("BalanceAdd", (holding.quantity.divide(fiatPrices.rates?.first { it.fiat == holding.symbol }?.rate, 8, RoundingMode.HALF_UP)).toString())

            balance += holding.quantity.divide(fiatPrices.rates?.first { it.fiat == holding.symbol }?.rate, 8, RoundingMode.HALF_UP)

//            balance += fiatPrices.rates?.first { it.fiat == holding.symbol }?.rate?.divide(holding.quantity, 8, RoundingMode.HALF_UP)!!
        }

        return balance

    }

    private fun getBalanceBtc(holdings: ArrayList<Holding>, prices: ArrayList<Price>, fiatPrices: ExchangeRates): BigDecimal {

        var balance = 0.toBigDecimal()

        prices.first { it.symbol?.toUpperCase() == "BTC" }.prices?.uSD

        //current usd price
        //times  by quantity
        //times by basefiat

        //divide by current btc price
        ////times by basefiat

        holdings.filter { it.type == Variables.Transaction.Type.crypto }.forEach { holding ->
            balance += (prices.first { it.symbol?.toLowerCase() == holding.symbol.toLowerCase() }.prices?.uSD?.times(holding.quantity))?.div(prices.first { it.symbol?.toUpperCase() == "BTC" }.prices?.uSD ?: 1.toBigDecimal()) ?: 0.toBigDecimal()

        }
        holdings.filter { it.type == Variables.Transaction.Type.fiat }.forEach { holding ->
            balance += holding.quantity.divide(fiatPrices.rates?.first { it.fiat == holding.symbol }?.rate, 8, RoundingMode.HALF_UP).divide(prices.first { it.symbol?.toUpperCase() == "BTC" }.prices?.uSD, 6, RoundingMode.HALF_UP)
        }


        return balance
    }

    private fun getBalanceEth(holdings: ArrayList<Holding>, prices: ArrayList<Price>, fiatPrices: ExchangeRates): BigDecimal {

        var balance = 0.toBigDecimal()

        holdings.filter { it.type == Variables.Transaction.Type.crypto }.forEach { holding ->
            balance += (prices.first { it.symbol?.toLowerCase() == holding.symbol.toLowerCase() }.prices?.uSD?.times(holding.quantity))?.div((prices.first { it.symbol?.toUpperCase() == "ETH" }.prices?.uSD ?: 1.toBigDecimal())) ?: 0.toBigDecimal()
        }

        holdings.filter { it.type == Variables.Transaction.Type.fiat }.forEach { holding ->
            balance += holding.quantity.divide(fiatPrices.rates?.first { it.fiat == holding.symbol }?.rate, 8, RoundingMode.HALF_UP).divide(prices.first { it.symbol?.toUpperCase() == "ETH" }.prices?.uSD?: 0.toBigDecimal(), 6, RoundingMode.HALF_UP)
        }

        return balance
    }

    private fun combineTransactions(transactions: ArrayList<Transaction>): ArrayList<Holding> {

        val holdings = ArrayList<Holding>()

        val transactionKeys = ArrayList<String>()

        transactions.forEach {
            if (!transactionKeys.contains(it.symbol)) {
                transactionKeys.add(it.symbol)
            }
        }

        transactions.forEach {
            if ((it.pairSymbol != null) && (!transactionKeys.contains(it.pairSymbol.toString()) && (it.isDeducted)))
                transactionKeys.add(it.pairSymbol.toString())
        }

        transactionKeys.forEach { key ->

            Log.i(TAG, "For holding $key")

            var getCurrentHoldings = 0.toBigDecimal()

            var getCostUsd = 0.toBigDecimal()
            var getCostBtc = 0.toBigDecimal()
            var getCostEth = 0.toBigDecimal()

            var imageUrl: String? = null

            val getAllTransactionsFor = transactions.filter { it.symbol == key }

            getAllTransactionsFor.forEach {
                imageUrl = it.baseImageUrl
                getCurrentHoldings += it.quantity
                getCostUsd += it.price * it.quantity * it.isDeductedPriceUsd
                getCostBtc += (it.price * it.quantity * it.isDeductedPriceUsd / (it.btcPrice))
                getCostEth += (it.price * it.quantity * it.isDeductedPriceUsd / (it.ethPrice))
            }

            val getAllTransactionsAgainst = transactions.filter { (it.pairSymbol == key) && (it.isDeducted) }

            getAllTransactionsAgainst.forEach {

                imageUrl = it.pairImageUrl
                getCurrentHoldings -= (it.price * it.quantity)
                getCostUsd -= it.price * it.quantity * it.isDeductedPriceUsd
                getCostBtc -= (it.price * it.quantity * it.isDeductedPriceUsd / (it.btcPrice))
                getCostEth -= (it.price * it.quantity * it.isDeductedPriceUsd / (it.ethPrice))
            }

            val getName = transactions.filter { (it.symbol == key) }.firstOrNull()?.name ?: ""

            if ((getAllTransactionsFor.isNotEmpty() && getAllTransactionsFor[0].pairSymbol == null) || ((getAllTransactionsAgainst.isNotEmpty() && getAllTransactionsAgainst[0].pairSymbol == null)))
                holdings.add(Holding(key, getName, getCurrentHoldings, getCostUsd, getCostBtc, getCostEth, Variables.Transaction.Type.fiat, imageUrl))
            else
                holdings.add(Holding(key, getName, getCurrentHoldings, getCostUsd, getCostBtc, getCostEth, Variables.Transaction.Type.crypto, imageUrl))
        }
        return holdings
    }

    fun saveData(holdingsSorted: ArrayList<Holding>, newPrices: ArrayList<Price>, baseFiat: Rate, priceBtc: Price, priceEth: Price, balanceUsd: BigDecimal, balanceBtc: BigDecimal, balanceEth: BigDecimal, changeUsd: BigDecimal, changeBtc: BigDecimal, changeEth: BigDecimal) {
        this.holdings = holdingsSorted
        this.prices = newPrices
        this.baseFiat = baseFiat
        this.priceBtc = priceBtc
        this.priceEth = priceEth
        this.balanceUsd = balanceUsd
        this.balanceBtc = balanceBtc
        this.balanceEth = balanceEth
        this.changeUsd = changeUsd
        this.changeBtc = changeBtc
        this.changeEth = changeEth
    }

    override fun clearDisposable() {
        compositeDisposable?.clear()
    }

    override fun detachView() {
        compositeDisposable?.dispose()
    }

}