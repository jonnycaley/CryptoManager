package com.jonnycaley.cryptomanager.ui.portfolio

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
                            when (timePeriod) {
                                PortfolioFragment.TIME_PERIOD_ALL -> getLatestPrices(combineTransactions(transactions))
                                else -> getHistoricalPrices(combineTransactions(transactions), transactions, timePeriod)
                            }
                        }
                    }

                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable?.add(d)
                        view.showRefreshing()
                    }

                    override fun onError(e: Throwable) {
                        println("onError: ${e.message}")
                        view.stopRefreshing()
                        view.showError()
                    }

                })
    }

    private fun getLatestPrices(holdings: ArrayList<Holding>) { //happens for the 'ALL' tab

        if (dataManager.checkConnection()) {

            var fiatPrices = ExchangeRates()

            var prices : MultiPrices? = null

            var newPrices : ArrayList<Price> = ArrayList()

            var holdingsSorted : ArrayList<Holding> = ArrayList()

            dataManager.getExchangeRateService().getExchangeRates()
                    .observeOn(Schedulers.computation())
                    .map { fiats -> fiatPrices = Gson().fromJson(JsonModifiers.jsonToCurrencies(fiats), ExchangeRates::class.java) }
                    .observeOn(Schedulers.io())
                    .flatMap { dataManager.getCryptoCompareServiceWithScalars().getMultiPrice(getCryptoQueryString(holdings), "USD,BTC") } // gets all crypto conversion rates for all time info
                    .observeOn(Schedulers.computation())
                    .map { json -> JsonModifiers.jsonToMultiPrices(json) }
                    .map { json -> Gson().fromJson(json, MultiPrices::class.java) }
                    .map { multiPrices -> prices = multiPrices }
                    .map { newPrices = getPrices(prices?.prices, fiatPrices) }
                    .map { holdingsSorted = ArrayList(holdings.sortedBy { transaction -> newPrices.filter { it.symbol?.toLowerCase() == transaction.symbol.toLowerCase() }[0].prices?.uSD?.times(transaction.quantity) }.asReversed()) }
                    .observeOn(Schedulers.io())
                    .flatMapSingle { dataManager.readBaseFiat() }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<Rate> {
                        override fun onComplete() {

                        }

                        override fun onNext(baseFiat: Rate) {

                            view.showHoldingsLayout()
                            view.showHoldings(holdingsSorted, baseFiat, newPrices)
                            view.showBalance(getBalance(holdingsSorted, newPrices), baseFiat)
                            view.showChange(getChange(holdingsSorted, newPrices), baseFiat)

                            view.hideRefreshing()
                        }

                        override fun onSubscribe(d: Disposable) {
                            compositeDisposable?.add(d)
                        }

                        override fun onError(e: Throwable) {
                            println("onError: ${e.message}")
                            view.stopRefreshing()
                            view.showError()
                        }

                    })

        } else {

        }

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

                        var newHoldingCost = 0.toDouble()

                        transactions.filter { it.symbol == cryptoSymbol }
                                .forEach { transaction ->
                                    if (transaction.date < getDate(timePeriod)) {
                                        newHoldingCost += (transaction.quantity * cryptoPrices.data?.get(0)?.close!!)
//                                        println("Get price from before")
//                                        println(transaction.quantity)
//                                        println(cryptoPrices.data?.get(0)?.close!!)
                                    } else {
//                                        println("Get price from bought")
//                                        println(transaction.quantity)
//                                        println(transaction.price)
//                                        println(transaction.isDeductedPrice)
                                        newHoldingCost += (transaction.quantity * transaction.price * transaction.isDeductedPrice)
                                    }
                                }

                        transactions.filter { it.pairSymbol == cryptoSymbol && it.isDeducted }
                                .forEach { transaction ->
                                    if (transaction.date < getDate(timePeriod)) {
                                        newHoldingCost -= (transaction.price * transaction.quantity * cryptoPrices.data?.get(0)?.close!!)
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
                                        newHoldingCost -= (transaction.quantity * transaction.price * transaction.isDeductedPrice)
                                    }

                                }

//                        println("New cost: $newHoldingCost")

                        holdings.first { it.symbol == cryptoSymbol }.cost = newHoldingCost

                        prices.add(cryptoPrices)
                    }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<Boolean> {

                        override fun onComplete() {
                            getLatestPrices(holdings)
                        }

                        override fun onNext(cryptoPrices: Boolean) {
                        }

                        override fun onSubscribe(d: Disposable) {
                            compositeDisposable?.add(d)
                        }

                        override fun onError(e: Throwable) {
                            println("onError: ${e.message}")
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

        val combinesPrices = ArrayList(prices)

        fiatPrices.rates?.forEach { fiatRate ->

            val tempPrice = Price()
            tempPrice.symbol = fiatRate.fiat

            val tempPrices = Prices()

            tempPrices.uSD = 1 / fiatRate.rate!!
            tempPrice.prices = tempPrices

            combinesPrices.add(tempPrice)
        }

//        combinesPrices.forEach { println("Price( symbol: ${it.symbol} , uSD: ${it.prices?.uSD})") }

        return combinesPrices
    }

    private fun getCryptoQueryString(holdings: ArrayList<Holding>): String {

        var cryptoSymbolsQueryString = ""

        holdings.filter { it.type == Variables.Transaction.Type.crypto }.forEach { cryptoSymbolsQueryString += it.symbol + "," }

        cryptoSymbolsQueryString = if (cryptoSymbolsQueryString.isNotEmpty())
            cryptoSymbolsQueryString.substring(0, cryptoSymbolsQueryString.length - 1)
        else
            "BTC"

        return cryptoSymbolsQueryString
    }

    private fun getChange(holdings: ArrayList<Holding>, combinedPrices: ArrayList<Price>): Double {

        var change = 0.toDouble()

        holdings.filter { it.type == Variables.Transaction.Type.crypto }.forEach { holding ->

            val price = combinedPrices.filter { it.symbol?.toLowerCase() == holding.symbol.toLowerCase() }[0].prices?.uSD
            val value = price?.times(holding.quantity)
            change += value?.minus(holding.cost)!!

        }

        return change
    }

    private fun getBalance(holdings: ArrayList<Holding>, prices: ArrayList<Price>): Double {

        var balance = 0.toDouble()

        holdings.forEach { transaction ->
            //            println(transaction.quantity)
//            println(prices.filter { it.symbol?.toLowerCase() == transaction.symbol.toLowerCase() }?.get(0)?.prices?.uSD)
            balance += prices.filter { it.symbol?.toLowerCase() == transaction.symbol.toLowerCase() }?.get(0)?.prices?.uSD?.times(transaction.quantity)!!
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

            var getCurrentHoldings = 0.toFloat()

            var getCost = 0.toDouble()

            var imageUrl: String? = null

            val getAllTransactionsFor = transactions.filter { it.symbol == key }

            getAllTransactionsFor.forEach {
                imageUrl = it.baseImageUrl
                getCurrentHoldings += it.quantity
                getCost += it.price * it.quantity * it.isDeductedPrice
            }

            val getAllTransactionsAgainst = transactions.filter { (it.pairSymbol == key) && (it.isDeducted) }

            getAllTransactionsAgainst.forEach {
                imageUrl = it.pairImageUrl
                getCurrentHoldings -= (it.price * it.quantity)
                getCost -= it.price * it.quantity * it.isDeductedPrice
            }

            if ((getAllTransactionsFor.isNotEmpty() && getAllTransactionsFor[0].pairSymbol == null) || ((getAllTransactionsAgainst.isNotEmpty() && getAllTransactionsAgainst[0].pairSymbol == null)))
                holdings.add(Holding(key, getCurrentHoldings, getCost, Variables.Transaction.Type.fiat, imageUrl))
            else
                holdings.add(Holding(key, getCurrentHoldings, getCost, Variables.Transaction.Type.crypto, imageUrl))

        }
//        holdings.forEach { Log.i(TAG, "Holding(Symbol: ${it.symbol}, type: ${it.type}, quantity: ${it.quantity}, cost: ${it.cost})") }

        return holdings
    }

    override fun clearDisposable() {
        compositeDisposable?.clear()
    }

    override fun detachView() {
        compositeDisposable?.dispose()
    }

}