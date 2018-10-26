package com.jonnycaley.cryptomanager.ui.portfolio

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
import com.jonnycaley.cryptomanager.utils.JsonModifiers
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.*
import io.reactivex.Observable
import io.reactivex.Observer
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList

class PortfolioPresenter(var dataManager: PortfolioDataManager, var view: PortfolioContract.View) : PortfolioContract.Presenter {

    var compositeDisposable: CompositeDisposable? = null

    val TAG = this.javaClass.simpleName

    init {
        this.view.setPresenter(this)
    }

    override fun attachView() {
        if (compositeDisposable == null || (compositeDisposable as CompositeDisposable).isDisposed) {
            compositeDisposable = CompositeDisposable()
        }
    }

    override fun getTransactions(timePeriod: String) {

        dataManager.getTransactions()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<ArrayList<Transaction>> {

                    override fun onSuccess(transactions: ArrayList<Transaction>) {
                        view.stopRefreshing()

                        if (transactions.isEmpty()) {
                            view.showNoHoldingsLayout()
                            view.hideRefreshing()
                        } else {
//                            when (timePeriod) {
//                                PortfolioFragment.TIME_PERIOD_ALL -> getLatestPrices(combineTransactions(transactions))
//                                else -> getHistoricalBtcEthPrices(combineTransactions(transactions), transactions, timePeriod)
//                            }
                            transactions.forEach { Log.i(TAG, "Transaction(exchange = ${it.exchange}, symbol = ${it.symbol}, pairSymbol = ${it.pairSymbol}, quantity = ${it.quantity}, price = ${it.price}, priceUSD = ${it.priceUSD}, date = ${it.date}, notes = ${it.notes}, isDeducted = ${it.isDeducted}, isDeductedPriceUsd = ${it.isDeductedPriceUsd}, baseImageUrl = ${it.baseImageUrl}, pairImageUrl = ${it.pairImageUrl}, btcPrice = ${it.btcPrice}, ethPrice = ${it.ethPrice})") }

                            getHistoricalBtcEthPrices(combineTransactions(transactions), transactions, timePeriod)
                        }
                    }

                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable?.add(d)
                        view.showRefreshing()
                    }

                    override fun onError(e: Throwable) {
                        println("onError1: ${e.message}")
                        view.stopRefreshing()
                        view.showError()
                    }

                })
    }

    var baseFiat: Rate = Rate()

    private fun getLatestPrices(holdings: ArrayList<Holding>) { //happens for the 'ALL' tab

        if (dataManager.checkConnection()) {

            var fiatPrices = ExchangeRates()

            var newPrices: ArrayList<Price> = ArrayList()

            var holdingsSorted: ArrayList<Holding> = ArrayList()

            var changeUsd = 0.toBigDecimal()

            dataManager.getExchangeRateService().getExchangeRates()
                    .observeOn(Schedulers.computation())
                    .map { fiats -> fiatPrices = Gson().fromJson(JsonModifiers.jsonToCurrencies(fiats), ExchangeRates::class.java) }
                    .subscribeOn(Schedulers.io())
                    .flatMap { dataManager.getCryptoCompareServiceWithScalars().getMultiPrice(getCryptoQueryString(holdings), "USD") } // gets all crypto conversion rates for all time info
                    .observeOn(Schedulers.computation())
                    .map { json -> Gson().fromJson(JsonModifiers.jsonToMultiPrices(json), MultiPrices::class.java) }
                    .map { multiPrices -> newPrices = getPrices(multiPrices.prices, fiatPrices) }
                    .map { holdingsSorted = ArrayList(holdings.sortedBy { holding -> newPrices.first { it.symbol?.toLowerCase() == holding.symbol.toLowerCase() }.prices?.uSD?.toBigDecimal()?.times(holding.quantity) }.asReversed()) }
                    .observeOn(Schedulers.io())
                    .flatMapSingle { dataManager.readBaseFiat() }
                    .observeOn(Schedulers.computation())
                    .map { fiat -> baseFiat = fiat }
                    .map { change -> changeUsd = getChangeUsd(holdingsSorted, newPrices) }
                    .map { getBalance(holdingsSorted, newPrices) }
                    .map { balance ->
                        view.saveData(holdingsSorted, newPrices, baseFiat, newPrices.first { it.symbol?.toUpperCase() == "BTC" }, newPrices.first { it.symbol?.toUpperCase() == "ETH" }, balance, changeUsd, getChangeBtc(holdingsSorted, newPrices), getChangeEth(holdingsSorted, newPrices))
                    }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<Unit> {
                        override fun onComplete() {
                            updateView()
                        }

                        override fun onNext(baseFiat: Unit) {

                            view.hideRefreshing()
                        }

                        override fun onSubscribe(d: Disposable) {
                            compositeDisposable?.add(d)
                        }

                        override fun onError(e: Throwable) {
                            println("onError2: ${e.message}")
                            view.stopRefreshing()
                            view.showError()
                        }

                    })

        } else {

        }

    }

    private fun updateView() {

        view.showHoldingsLayout()
        view.showHoldings()
        view.showBalance()
        view.showChange()

    }

    private fun getHistoricalPrices(holdings: ArrayList<Holding>, transactions: ArrayList<Transaction>, timePeriod: String) {

        var cryptoSymbol: String? = null

        val prices = ArrayList<HistoricalData>()

        if (dataManager.checkConnection()) {

            Observable.fromArray(holdings)
                    .flatMapIterable { holding -> holding }
                    .flatMap { holding ->
                        //get the price at the time frame requested
                        cryptoSymbol = holding.symbol
                        when (timePeriod) {
                            PortfolioFragment.TIME_PERIOD_1H -> return@flatMap dataManager.getCryptoCompareService().getPriceAt(PortfolioFragment.TIME_PERIOD_MINUTE, holding.symbol, "USD", PortfolioFragment.AGGREGATE_1H)
                            PortfolioFragment.TIME_PERIOD_1D -> return@flatMap dataManager.getCryptoCompareService().getPriceAt(PortfolioFragment.TIME_PERIOD_HOUR, holding.symbol, "USD", PortfolioFragment.AGGREGATE_1D)
                            PortfolioFragment.TIME_PERIOD_1W -> return@flatMap dataManager.getCryptoCompareService().getPriceAt(PortfolioFragment.TIME_PERIOD_HOUR, holding.symbol, "USD", PortfolioFragment.AGGREGATE_1W)
                            else -> return@flatMap dataManager.getCryptoCompareService().getPriceAt(PortfolioFragment.TIME_PERIOD_DAY, holding.symbol, "USD", PortfolioFragment.AGGREGATE_1M)
                        }
                    }
                    .subscribeOn(Schedulers.computation())
                    .map { cryptoPrices ->

                        Log.i(TAG, "Got data for cryptoSymbol: $cryptoSymbol")

                        cryptoPrices.symbol = cryptoSymbol

                        var newHoldingCost = 0.toDouble()

                        transactions.filter { it.symbol.toUpperCase() == cryptoSymbol?.toUpperCase() }
                                .forEach { transaction ->
                                    if (transaction.date < getDate(timePeriod) && (timePeriod != PortfolioFragment.TIME_PERIOD_ALL)) {
                                        if (cryptoPrices.data?.isNotEmpty()!!) //for fiat prices like usd/usd returns error with empty data
                                            newHoldingCost += (transaction.quantity * cryptoPrices.data?.get(0)?.close!!)
                                        else
                                            newHoldingCost += (transaction.quantity * 1.toDouble())


//                                        println("Get price from before")
//                                        println(transaction.quantity)
//                                        println(cryptoPrices.data?.get(0)?.close!!)
                                    } else {
//                                        println("Get price from bought")
//                                        println(transaction.quantity)
//                                        println(transaction.price)
//                                        println(transaction.isDeductedPrice)
                                        newHoldingCost += (transaction.quantity * transaction.price * transaction.isDeductedPriceUsd)
                                    }
                                }

                        transactions.filter { it.pairSymbol?.toUpperCase() == cryptoSymbol?.toUpperCase() && it.isDeducted }
                                .forEach { transaction ->
                                    if (transaction.date < getDate(timePeriod) && (timePeriod != PortfolioFragment.TIME_PERIOD_ALL)) {
                                        if (cryptoPrices.data?.isNotEmpty()!!) //for fiat prices like usd/usd returns error with empty data
                                            newHoldingCost -= (transaction.price * transaction.quantity * cryptoPrices.data?.get(0)?.close!!)
                                        else
                                            newHoldingCost -= (transaction.price * transaction.quantity * 1.toDouble())
//                                        println("Get price from before")
//                                        print("Price: " + transaction.price)
//                                        print("PriceUSD: " + transaction.priceUSD)
//                                        println("Quantity" + transaction.quantity)
//                                        println("Price usd time related:" + cryptoPrices.data?.get(0)?.close!!)
//                                        println("${transaction.isDeductedPrice}")

                                    } else {
//                                        println("Get price from bought")
//                                        println(transaction.quantity)
//                                        println(transaction.price)
//                                        println(transaction.isDeductedPrice)
                                        newHoldingCost -= (transaction.quantity * transaction.price * transaction.isDeductedPriceUsd)
                                    }

                                }
                        println("Altering holding price of $cryptoSymbol holding")

//                        println("New cost: $newHoldingCost")

                        holdings.first { it.symbol.toUpperCase() == cryptoSymbol?.toUpperCase() }.costUsd = newHoldingCost

                        prices.add(cryptoPrices)
                    }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<Boolean> {

                        override fun onComplete() {
                            setHistoricalBtcEthHoldings(prices, transactions, holdings, timePeriod)
                        }

                        override fun onNext(cryptoPrices: Boolean) {
                        }

                        override fun onSubscribe(d: Disposable) {
                            compositeDisposable?.add(d)
                        }

                        override fun onError(e: Throwable) {
                            println("onError3: ${e.message}")
                            view.stopRefreshing()
                            view.showError()
                        }

                    })

        } else {

        }
    }

    private fun setHistoricalBtcEthHoldings(prices: ArrayList<HistoricalData>, transactions: ArrayList<Transaction>, holdings: ArrayList<Holding>, timePeriod: String) {

        var newHoldingCostBtc = 0.toDouble()
        var newHoldingCostEth = 0.toDouble()

        holdings.forEach { holding ->

            transactions.filter { it.symbol.toUpperCase() == holding.symbol.toUpperCase() }
                    .forEach { transaction ->
                        if (transaction.date < getDate(timePeriod) && timePeriod != PortfolioFragment.TIME_PERIOD_ALL) {

                            var price = 1.toDouble()

                            if(prices.first { it.symbol?.toUpperCase() == transaction.symbol.toUpperCase() }.data?.isNotEmpty()!!)
                                price = prices.first { it.symbol?.toUpperCase() == transaction.symbol.toUpperCase() }.data?.first()?.close!!

                            println(transaction.quantity * transaction.price * transaction.isDeductedPriceUsd)
//                            transaction.quantity * transaction.price * transaction.isDeductedPriceUsd

//                            if(transaction.symbol.toUpperCase() != "BTC"){
                            newHoldingCostBtc += ((transaction.quantity * price) / priceBtcHistorical.prices?.uSD!!)
//                            }
//                            if(transaction.symbol.toUpperCase() != "ETH") {
                            newHoldingCostEth += ((transaction.quantity * price) / priceEthHistorical.prices?.uSD!!)
//                            }

//                            Log.i(TAG, "quantity: ${transaction.quantity}, price: ${transaction.price}, isDeductedPriceUsd: ${transaction.isDeductedPriceUsd}, btcPrice: ${btcResponse.data?.get(0)?.close!!}, ")
//                            Log.i(TAG, "Primary symbol ${transaction.symbol}")
//                            Log.i(TAG, "Adding: ${((transaction.quantity * transaction.price) / btcResponse.data?.get(0)?.close!!)}")
                        } else {
                            newHoldingCostBtc += ((transaction.quantity * transaction.price * transaction.isDeductedPriceUsd) / transaction.btcPrice)                                   // transaction.price * transaction.isDeductedPriceUsd  used to be transaction.priceUsd but that gives us the price at the time and not the price we paid for it!!!!!!!!!!
                            newHoldingCostEth += ((transaction.quantity * transaction.price * transaction.isDeductedPriceUsd) / transaction.ethPrice)

//                            Log.i(TAG, "pricePaid: ${(transaction.quantity * transaction.price * transaction.isDeductedPriceUsd)}; btcPrice: ${transaction.btcPrice}; ")
//                            Log.i(TAG, "New holding from creation price: ${((transaction.quantity * transaction.price * transaction.isDeductedPriceUsd) / transaction.btcPrice)}")
                        }
                    }

            transactions.filter { (it.pairSymbol?.toUpperCase() == holding.symbol.toUpperCase()) && it.isDeducted }
                    .forEach { transaction ->

                        println("Transaction : ${transaction.symbol}")

                        if (transaction.date < getDate(timePeriod) && timePeriod != PortfolioFragment.TIME_PERIOD_ALL) {
//                            Log.i(TAG, "Secondary symbol ${transaction.pairSymbol}")
//                            Log.i(TAG, "Bought before date -> get priceBtc at time as valid")

                            println("Transaction 1")

                            var price = 1.toDouble()

                            if(prices.first { it.symbol?.toUpperCase() == transaction.pairSymbol?.toUpperCase() }.data?.isNotEmpty()!!)
                                price = prices.first { it.symbol?.toUpperCase() == transaction.pairSymbol?.toUpperCase() }.data?.first()?.close!!

                            newHoldingCostBtc -= ((transaction.price * transaction.quantity * price) / priceBtcHistorical.prices?.uSD!!)
                            newHoldingCostEth -= ((transaction.price * transaction.quantity * price) / priceEthHistorical.prices?.uSD!!)
                        } else {

                            println("Transaction 2")

//                            Log.i(TAG, "Bought before date -> get priceBtc at time of purchase")
//                                            println("Get price from bought")
//                                            println(transaction.price)
//                                            println(transaction.quantity)
//                                            println(transaction.isDeductedPriceUsd)
//                                            println(transaction.btcPrice)
                            newHoldingCostBtc -= (transaction.price * transaction.quantity * transaction.isDeductedPriceUsd / (transaction.btcPrice))
                            newHoldingCostEth -= (transaction.price * transaction.quantity * transaction.isDeductedPriceUsd / (transaction.ethPrice))
                        }
                    }

            println("Holding: ${holding.symbol} new costBtc: $newHoldingCostBtc new costEth: $newHoldingCostEth")

            holding.costBtc = newHoldingCostBtc
            holding.costEth = newHoldingCostEth

        }

        getLatestPrices(holdings)

    }


    var priceBtcHistorical = Price() //TODO: DO WE NEED TO DO THESE OR CAN WE SAVE THEM TO THE ACTIVITY LIKE WE DO AT THE END AND THEN REFERNENCE THEM THEN?
    var priceEthHistorical = Price() //TODO: DO WE NEED TO DO THESE OR CAN WE SAVE THEM TO THE ACTIVITY LIKE WE DO AT THE END AND THEN REFERNENCE THEM THEN?

    private fun getHistoricalBtcEthPrices(holdings: ArrayList<Holding>, transactions: ArrayList<Transaction>, timePeriod: String) {

        if (dataManager.checkConnection()) {

            var timePeriodStr = ""
            var aggregateStr = ""

            when (timePeriod) {
                PortfolioFragment.TIME_PERIOD_1H -> {
                    timePeriodStr = PortfolioFragment.TIME_PERIOD_MINUTE; aggregateStr = PortfolioFragment.AGGREGATE_1H
                }
                PortfolioFragment.TIME_PERIOD_1D -> {
                    timePeriodStr = PortfolioFragment.TIME_PERIOD_HOUR; aggregateStr = PortfolioFragment.AGGREGATE_1D
                }
                PortfolioFragment.TIME_PERIOD_1W -> {
                    timePeriodStr = PortfolioFragment.TIME_PERIOD_HOUR; aggregateStr = PortfolioFragment.AGGREGATE_1W
                }
                else -> {
                    timePeriodStr = PortfolioFragment.TIME_PERIOD_DAY; aggregateStr = PortfolioFragment.AGGREGATE_1M
                }
            }

            dataManager.getCryptoCompareService().getPriceAt(timePeriodStr, "BTC", "USD", aggregateStr)
                    .observeOn(Schedulers.computation())
                    .map { btcResponse ->

                        val tempPrice = Price()
                        tempPrice.symbol = "BTC"
                        val tempPrices = Prices()
                        tempPrices.uSD = btcResponse.data?.first()?.close
                        tempPrice.prices = tempPrices
                        priceBtcHistorical = tempPrice

                        Log.i(TAG, "Saving historical Btc Price: ${btcResponse.data?.first()?.close}")

//                        var newHoldingCostBtc = 0.toDouble()

//                        holdings.forEach {
//                            Log.i(TAG, "${it.symbol} BTC cost: ${it.costBtc}")
//                            Log.i(TAG, "${it.symbol} ETH cost: ${it.costEth}")
//                        }

//                        holdings.forEach { holding ->

//                            transactions.filter { it.symbol.toUpperCase() == holding.symbol.toUpperCase() }
//                                    .forEach { transaction ->
//                                        if (transaction.date < getDate(timePeriod) && timePeriod != PortfolioFragment.TIME_PERIOD_ALL) {
//                                            Log.i(TAG, "quantity: ${transaction.quantity}, price: ${transaction.price}, isDeductedPriceUsd: ${transaction.isDeductedPriceUsd}, btcPrice: ${btcResponse.data?.get(0)?.close!!}, ")
//                                            newHoldingCostBtc += ((transaction.quantity * transaction.priceUSD) / btcResponse.data?.get(0)?.close!!)
//
//                                            Log.i(TAG, "Primary symbol ${transaction.symbol}")
//                                            Log.i(TAG, "Adding: ${((transaction.quantity * transaction.price) / btcResponse.data?.get(0)?.close!!)}")
//                                        } else {
//                                            newHoldingCostBtc += ((transaction.quantity * transaction.priceUSD) / transaction.btcPrice)                                   // transaction.price * transaction.isDeductedPriceUsd  used to be transaction.priceUsd but that gives us the price at the time and not the price we paid for it!!!!!!!!!!
//
//
//                                            Log.i(TAG, "pricePaid: ${(transaction.quantity * transaction.price * transaction.isDeductedPriceUsd)}; btcPrice: ${transaction.btcPrice}; ")
//                                            Log.i(TAG, "New holding from creation price: ${((transaction.quantity * transaction.price * transaction.isDeductedPriceUsd) / transaction.btcPrice) }")
//                                        }
//                                    }
//
//                            transactions.filter { (it.pairSymbol?.toUpperCase() == holding.symbol.toUpperCase()) && it.isDeducted }
//                                    .forEach { transaction ->
//                                        if (transaction.date < getDate(timePeriod) && timePeriod != PortfolioFragment.TIME_PERIOD_ALL) {
//                                            Log.i(TAG, "Secondary symbol ${transaction.pairSymbol}")
//                                            newHoldingCostBtc -= ((transaction.price * transaction.quantity * transaction.isDeductedPriceUsd) / (btcResponse.data?.get(0)?.close!!))
//                                            Log.i(TAG, "Bought before date -> get priceBtc at time as valid")
//
//                                        } else {
//                                            Log.i(TAG, "Bought before date -> get priceBtc at time of purchase")
////                                            println("Get price from bought")
////                                            println(transaction.price)
////                                            println(transaction.quantity)
////                                            println(transaction.isDeductedPriceUsd)
////                                            println(transaction.btcPrice)
//                                            newHoldingCostBtc -= (transaction.price * transaction.quantity * transaction.isDeductedPriceUsd / (transaction.btcPrice))
//                                        }
//                                    }


//                            Log.i(TAG, "Old costBtc: ${holding.costBtc} for ${holding.symbol}")
//                            Log.i(TAG, "New costBtc: $newHoldingCostBtc for ${holding.symbol}")


//                            holding.costBtc = newHoldingCostBtc
//                        }

                    }
                    .observeOn(Schedulers.io())
                    .flatMap { dataManager.getCryptoCompareService().getPriceAt(timePeriodStr, "ETH", "USD", aggregateStr) }
                    .observeOn(Schedulers.computation())
                    .map { ethResponse ->

                        val tempPrice = Price()
                        tempPrice.symbol = "ETH"
                        val tempPrices = Prices()
                        tempPrices.uSD = ethResponse.data?.first()?.close

                        Log.i(TAG, "Saving historical Eth Price: ${ethResponse.data?.first()?.close}")

                        tempPrice.prices = tempPrices
                        priceEthHistorical = tempPrice


//                        var newHoldingCostEth = 0.toDouble()
//
//                        holdings.forEach { holding ->
//
//                            transactions.filter { it.symbol.toUpperCase() == holding.symbol.toUpperCase() }
//                                    .forEach { transaction ->
//                                        if (transaction.date < getDate(timePeriod) && timePeriod != PortfolioFragment.TIME_PERIOD_ALL) {
//                                            newHoldingCostEth += ((transaction.quantity * transaction.price * transaction.isDeductedPriceUsd) / (ethResponse.data?.get(0)?.close!!))
////                                            println("Get price from before")
////                                            println(transaction.priceUSD * transaction.quantity)
////                                            println(ethResponse.data?.get(0)?.close!!)
//                                        } else {
//                                            newHoldingCostEth += ((transaction.quantity * transaction.price * transaction.isDeductedPriceUsd) / transaction.ethPrice)
////                                            println("Get price from bought")
////                                            println(transaction.priceUSD)
////                                            println(transaction.ethPrice)
////                                        println(transaction.isDeductedPriceUsd)
//                                        }
//                                    }
//
//                            transactions.filter { (it.pairSymbol?.toUpperCase() == holding.symbol.toUpperCase()) && it.isDeducted }
//                                    .forEach { transaction ->
//                                        if (transaction.date < getDate(timePeriod) && timePeriod != PortfolioFragment.TIME_PERIOD_ALL) {
//                                            newHoldingCostEth -= (transaction.price * transaction.quantity * transaction.isDeductedPriceUsd / (ethResponse.data?.get(0)?.close!!))
////                                            println("Get price from before")
////                                            print("price: " + transaction.price)
////                                            print("quantity: " + transaction.quantity)
////                                            println("isDeductedPriceUsd" + transaction.isDeductedPriceUsd)
////                                            println("Price usd time related:" + ethResponse.data?.get(0)?.close!!)
////                                            println("${transaction.isDeductedPriceUsd}")
//
//                                        } else {
////                                            println("Get price from bought")
////                                            println(transaction.price)
////                                            println(transaction.quantity)
////                                            println(transaction.isDeductedPriceUsd)
////                                            println(transaction.ethPrice)
//                                            newHoldingCostEth -= (transaction.price * transaction.quantity * transaction.isDeductedPriceUsd / (transaction.ethPrice))
//                                        }
//
//                                    }
//
//
//                            Log.i(TAG, "Old costEth: ${holding.costEth} for ${holding.symbol}")
//                            Log.i(TAG, "New costEth: $newHoldingCostEth for ${holding.symbol}")
//
//                            holding.costEth = newHoldingCostEth
//                        }

                    }
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
                            println("onError4: ${e.message}")
                            view.stopRefreshing()
                            view.showError()
                        }

                    })

        } else {

        }
    }

    fun getDate(timePeriod: String): Date {
        val cal = Calendar.getInstance()

        when (timePeriod) {
            PortfolioFragment.TIME_PERIOD_1H -> {
                cal.add(Calendar.HOUR, -1)
            }
            PortfolioFragment.TIME_PERIOD_1D -> {
                cal.add(Calendar.DAY_OF_YEAR, -1)
            }
            PortfolioFragment.TIME_PERIOD_1W -> {
                cal.add(Calendar.DAY_OF_YEAR, -7)
            }
            PortfolioFragment.TIME_PERIOD_1M -> {
                cal.add(Calendar.DAY_OF_YEAR, -30)
            }
        }
        return cal.getTime()
    }

    private fun getPrices(prices: List<Price>?, fiatPrices: ExchangeRates): ArrayList<Price> {

        val combinedPrices = ArrayList(prices)

        fiatPrices.rates?.forEach { fiatRate ->

            val tempPrice = Price()
            tempPrice.symbol = fiatRate.fiat

            val tempPrices = Prices()

            tempPrices.uSD = 1 / fiatRate.rate!!
            tempPrice.prices = tempPrices

            combinedPrices.add(tempPrice)
        }

//        combinesPrices.forEach { println("Price( symbol: ${it.symbol} , uSD: ${it.prices?.uSD})") }

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
            val value = price?.toBigDecimal()?.times(holding.quantity)
            change += value?.minus(holding.costUsd.toBigDecimal())!!
        }

        return change
    }


    private fun getChangeBtc(holdings: ArrayList<Holding>, combinedPrices: ArrayList<Price>): BigDecimal {

        var change = 0.toBigDecimal()

        holdings.filter { it.symbol.toUpperCase() != "BTC" }.forEach { holding ->

            println("BTC calculation")

            println(holding.symbol)

            val costBtcHistorical = holding.costBtc.toBigDecimal() //this is what it was at time stamp
            println(costBtcHistorical)

            val costBtcNow = holding.quantity * combinedPrices.first { it.symbol?.toUpperCase() == holding.symbol.toUpperCase() }.prices?.uSD?.toBigDecimal()!! / combinedPrices.first { it.symbol?.toUpperCase() == "BTC" }.prices?.uSD!!.toBigDecimal()!!
            println(costBtcNow)

            change += costBtcNow - costBtcHistorical
            println(change)

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
//        println("priceBtcNow: ${priceBtcNow.prices!!.uSD}")
//        println("priceBtcThen: ${priceBtcHistorical.prices!!.uSD}")
//
//        println("Portfolio now: $portfolioNow")
//        println("Portfolio then: $portfolioHistorical")
//
//        println("portfolioBtcNow: $btcNow")
//        println("portfolioBtcThen: $btcThen")
//
//        println("btcDifference: $btcDifference")
//
//        return btcDifference
    }

    private fun getChangeEth(holdings: ArrayList<Holding>, combinedPrices: ArrayList<Price>): BigDecimal {


        var change = 0.toBigDecimal()

        holdings.filter { it.symbol.toUpperCase() != "ETH" }.forEach { holding ->

            println(holding.symbol)

            println("ETH calculation")


            val costEthHistorical = holding.costEth.toBigDecimal() //this is what it was at time stamp
            println(costEthHistorical)

            val costEthNow = holding.quantity * combinedPrices.first { it.symbol?.toUpperCase() == holding.symbol.toUpperCase() }.prices?.uSD?.toBigDecimal()!! / combinedPrices.first { it.symbol?.toUpperCase() == "ETH" }.prices?.uSD!!.toBigDecimal()!!
            println(costEthNow)

            change += costEthNow - costEthHistorical
            println(change)


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

        return change


//        val ethThen = portfolioHistorical / priceEthHistorical.prices?.uSD!!
//        val ethNow = portfolioNow / priceEthNow.prices?.uSD!!
//
//        val ethDifference = ethNow - ethThen
//
//        println("priceEthNow: ${priceEthNow.prices!!.uSD}")
//        println("priceEthThen: ${priceEthHistorical.prices!!.uSD}")
//
//        println("Portfolio now: $portfolioNow")
//        println("Portfolio then: $portfolioHistorical")
//
//        println("portfolioEthNow: $ethNow")
//        println("portfolioEthThen: $ethThen")
//
//        println("EthDifference: $ethDifference")
//
//        return ethDifference
    }


    private fun getBalance(holdings: ArrayList<Holding>, prices: ArrayList<Price>): BigDecimal {

        var balance = 0.toBigDecimal()

        holdings.forEach { transaction ->
            //            println(transaction.quantity)
//            println(prices.filter { it.symbol?.toLowerCase() == transaction.symbol.toLowerCase() }?.get(0)?.prices?.uSD)
            balance += prices.filter { it.symbol?.toLowerCase() == transaction.symbol.toLowerCase() }?.get(0)?.prices?.uSD?.toBigDecimal()?.times(transaction.quantity)!!
        }

        return balance

    }

    private fun combineTransactions(transactions: ArrayList<Transaction>): ArrayList<Holding> {

        val holdings = ArrayList<Holding>()

        val transactionKeys = ArrayList<String>()

//        holdings.forEach { println("Transaction(exchange = ${it.exchange}, symbol = ${it.symbol}, pairSymbol = ${it.pairSymbol}, quantity = ${it.quantity}, price = ${it.price}, priceUSD = ${it.priceUSD}, date = ${it.date}, notes = ${it.notes}, isDeducted = ${it.isDeducted}), isDeductedPrice = ${it.isDeductedPrice})") }

        transactions.forEach {
            if (!transactionKeys.contains(it.symbol)) {
                transactionKeys.add(it.symbol)
            }
        }

        transactions.forEach {
            if ((it.pairSymbol != null) && (!transactionKeys.contains(it.pairSymbol!!) && (it.isDeducted)))
                transactionKeys.add(it.pairSymbol!!)
        }

        transactionKeys.forEach { key ->

            Log.i(TAG, "For holding $key")

            var getCurrentHoldings = 0.toBigDecimal()

            var getCostUsd = 0.toDouble()
            var getCostBtc = 0.toDouble()
            var getCostEth = 0.toDouble()

            var imageUrl: String? = null

            val getAllTransactionsFor = transactions.filter { it.symbol == key }

            getAllTransactionsFor.forEach {
                imageUrl = it.baseImageUrl
                Log.i(TAG, "Adding ${it.quantity}")
                getCurrentHoldings += it.quantity.toBigDecimal()
                getCostUsd += it.price * it.quantity * it.isDeductedPriceUsd
                getCostBtc += (it.price * it.quantity * it.isDeductedPriceUsd / (it.btcPrice))
                getCostEth += (it.price * it.quantity * it.isDeductedPriceUsd / (it.ethPrice))
            }

            val getAllTransactionsAgainst = transactions.filter { (it.pairSymbol == key) && (it.isDeducted) }

            getAllTransactionsAgainst.forEach {

                Log.i(TAG, it.price.toString())
                Log.i(TAG, it.quantity.toString())
                Log.i(TAG, (it.quantity * it.price).toString())

                imageUrl = it.pairImageUrl
                getCurrentHoldings -= (it.price.toBigDecimal()* it.quantity.toBigDecimal())
                getCostUsd -= it.price * it.quantity * it.isDeductedPriceUsd
                getCostBtc -= (it.price * it.quantity * it.isDeductedPriceUsd / (it.btcPrice))
                getCostEth -= (it.price * it.quantity * it.isDeductedPriceUsd / (it.ethPrice))
            }

            if ((getAllTransactionsFor.isNotEmpty() && getAllTransactionsFor[0].pairSymbol == null) || ((getAllTransactionsAgainst.isNotEmpty() && getAllTransactionsAgainst[0].pairSymbol == null)))
                holdings.add(Holding(key, getCurrentHoldings, getCostUsd, getCostBtc, getCostEth, Variables.Transaction.Type.fiat, imageUrl))
            else
                holdings.add(Holding(key, getCurrentHoldings, getCostUsd, getCostBtc, getCostEth, Variables.Transaction.Type.crypto, imageUrl))

        }
        holdings.forEach { Log.i(TAG, "Holding(Symbol: ${it.symbol}, type: ${it.type}, quantity: ${it.quantity}, costUsd: ${it.costUsd}, costBtc: ${it.costBtc}, costEth: ${it.costEth})") }

        return holdings
    }

    override fun clearDisposable() {
        compositeDisposable?.clear()
    }

    override fun detachView() {
        compositeDisposable?.dispose()
    }

}