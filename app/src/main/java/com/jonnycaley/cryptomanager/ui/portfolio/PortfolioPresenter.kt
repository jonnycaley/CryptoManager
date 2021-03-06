package com.jonnycaley.cryptomanager.ui.portfolio

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


class PortfolioPresenter(var dataManager: PortfolioDataManager, var view: PortfolioContract.View) : PortfolioContract.Presenter {

    var compositeDisposable: CompositeDisposable? = null

    val TAG = "PortfolioPresenter"

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

    fun getFiats() {

        dataManager.readFiats() //gets all the fiats and their corresponding exchange rate to USD
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<ExchangeRates> {
                    override fun onSuccess(exchangeRates: ExchangeRates) {
                        saveFiats(exchangeRates.rates) //saves the fiats
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

    /*
    Function gets the transactions
    */
    override fun getTransactions(timePeriod: String) { //gets all the transactions made

        dataManager.readBaseFiat() //gets the base fiat
                .map { response ->
                    baseFiat = response //saves the base fiat
                }
                .flatMap { dataManager.getTransactions() } //gets all the transactions
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<ArrayList<Transaction>> {

                    override fun onSuccess(transactions: ArrayList<Transaction>) {
                        if (transactions.isEmpty()) { //if the portfolio is empty
                            resetPortfolio()
                            view.showNoHoldingsLayout() //change layout
                            view.hideRefreshing() //change layout
                            view.hideInternetRequiredLayout() //change layout
                            view.hideProgressLayout() //change layout
                            view.showBalance(baseFiat, 0.toBigDecimal(), 0.toBigDecimal(), 0.toBigDecimal())
                            view.showChange(baseFiat, 0.toBigDecimal(), 0.toBigDecimal(), 0.toBigDecimal(), 0.toBigDecimal(), 0.toBigDecimal(), 0.toBigDecimal())
                            view.showHoldings(ArrayList(), ArrayList(), baseFiat, ArrayList() )
                        } else {
                            getHistoricalBtcEthPrices(combineTransactions(transactions), transactions, timePeriod) //combines the transactions into holdings
                        }
                    }

                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable?.add(d)
                    }

                    override fun onError(e: Throwable) {
                        Log.i(TAG, "onError1: ${e.message}")
                        view.stopRefreshing()
                        view.showError()
                        view.hideInternetRequiredLayout()
                    }
                })
    }

    /*
    Function resets the portfolio
    */
    private fun resetPortfolio() {
        balanceUsd = 0.toBigDecimal()
        balanceBtc = 0.toBigDecimal()
        balanceEth = 0.toBigDecimal()
        changeUsd = 0.toBigDecimal()
        changeBtc = 0.toBigDecimal()
        changeEth = 0.toBigDecimal()
        holdings.clear()
        prices.clear()
    }

    var priceBtcHistorical = Price()
    var priceEthHistorical = Price()

    private fun getHistoricalBtcEthPrices(holdings: ArrayList<Holding>, transactions: ArrayList<Transaction>, timePeriod: String) { //

        if (dataManager.checkConnection()) {

            var timePeriodStr = ""
            var aggregateStr = ""

            when (timePeriod) {
                PortfolioFragment.TIME_PERIOD_1H -> {
                    timePeriodStr = PortfolioFragment.TIME_PERIOD_MINUTE; aggregateStr = PortfolioFragment.AGGREGATE_1H //time period configure
                }
                PortfolioFragment.TIME_PERIOD_1D -> {
                    timePeriodStr = PortfolioFragment.TIME_PERIOD_HOUR; aggregateStr = PortfolioFragment.AGGREGATE_1D //time period configure
                }
                PortfolioFragment.TIME_PERIOD_1W -> {
                    timePeriodStr = PortfolioFragment.TIME_PERIOD_HOUR; aggregateStr = PortfolioFragment.AGGREGATE_1W //time period configure
                }
                else -> {
                    timePeriodStr = PortfolioFragment.TIME_PERIOD_DAY; aggregateStr = PortfolioFragment.AGGREGATE_1M //time period configure
                }
            }

            val getBtcPrice: Observable<HistoricalData> = dataManager.getCryptoCompareService().getPriceAt(timePeriodStr, "BTC", "USD", aggregateStr) //get btc price
            val getEthPrice: Observable<HistoricalData> = dataManager.getCryptoCompareService().getPriceAt(timePeriodStr, "ETH", "USD", aggregateStr) //get eth price

            Observable.zip(getBtcPrice, getEthPrice, BiFunction<HistoricalData, HistoricalData, Unit> { btcResponse, ethResponse ->

                val tempPriceBtc = Price()
                tempPriceBtc.symbol = "BTC"
                val tempPricesBtc = Prices()
                tempPricesBtc.uSD = btcResponse.data?.first()?.close
                tempPriceBtc.prices = tempPricesBtc
                priceBtcHistorical = tempPriceBtc //set btc price

                val tempPriceEth = Price()
                tempPriceEth.symbol = "ETH"
                val tempPricesEth = Prices()
                tempPricesEth.uSD = ethResponse.data?.first()?.close
                tempPriceEth.prices = tempPricesEth
                priceEthHistorical = tempPriceEth //set eth price

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
                            view.showRefreshing()
                        }

                        override fun onError(e: Throwable) {
                            Log.i(TAG, "onError4: ${e.message}")
                            view.stopRefreshing()
                            view.hideProgressLayout()
                            view.showError()
                        }

                    })
        } else {
            view.hideRefreshing()
            view.showNoInternet()
        }
    }

    @SuppressLint("CheckResult")
    private fun getHistoricalPrices(holdings: ArrayList<Holding>, transactions: ArrayList<Transaction>, timePeriod: String) { //get the price of each holding at the time stamp

        var cryptoSymbol = ""

        val prices = ArrayList<com.jonnycaley.cryptomanager.data.model.CryptoCompare.PriceAtTimestampForReal.Price>()

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
                res.forEach { jsonNoString -> //foreach holding, the new cost of the holding in usd,btc,eth to purchase it at either the time stamp or the time or purchase if it is before the time stamp is calculated for each transaction in that holding

                    val json = jsonNoString.toString()

                    val gson = Gson().fromJson(JsonModifiers.jsonToTimeStampPrice(json), com.jonnycaley.cryptomanager.data.model.CryptoCompare.PriceAtTimestampForReal.Price::class.java)

                    var newHoldingCost = 0.toBigDecimal()

                    cryptoSymbol = json.substring(2, json.substring(2).indexOf("\"") + 2)

                    gson.symbol = cryptoSymbol

                    transactions.filter { it.symbol.toUpperCase() == cryptoSymbol?.toUpperCase() } //if it's a transaction where it is the base currency (e.g. BTC/USD -> where BTC is the currency) then we need to add the cost
                            .forEach { transaction ->
                                if (transaction.date < getDate(timePeriod) && (timePeriod != PortfolioFragment.TIME_PERIOD_ALL)) { //if the transaction date is before the time period and the time period is not equal to all use the price from the time stamp
                                    if (gson.uSD != null) //for fiat prices like usd/usd returns error with empty data
                                        newHoldingCost += (transaction.quantity * (gson.uSD
                                                ?: 0.toBigDecimal()))
                                    else
                                        newHoldingCost += (transaction.quantity)
                                } else {
                                    newHoldingCost += (transaction.quantity * transaction.price * transaction.isDeductedPriceUsd) //use the price from the current usd price at the time of the transaction
                                }
                            }

                    transactions.filter { it.pairSymbol?.toUpperCase() == cryptoSymbol?.toUpperCase() && it.isDeducted } //if it's a transaction where it is the pair currency (e.g. USD/BTC -> where BTC is the currency) then we need to deduct the cost
                            .forEach { transaction ->
                                if (transaction.date < getDate(timePeriod) && (timePeriod != PortfolioFragment.TIME_PERIOD_ALL)) { //if the transaction date is before the time period and the time period is not equal to all use the price from the time stamp
                                    if (gson.uSD != null) //for fiat prices like usd/usd returns error with empty data
                                        newHoldingCost -= (transaction.price * transaction.quantity * (gson.uSD
                                                ?: 0.toBigDecimal()))
                                    else
                                        newHoldingCost -= (transaction.price * transaction.quantity)

                                } else {
                                    newHoldingCost -= (transaction.quantity * transaction.price * transaction.isDeductedPriceUsd) //use the price from the current usd price at the time of the transaction
                                }

                            }

                    holdings.first { it.symbol.toUpperCase() == cryptoSymbol?.toUpperCase() }.costUsd = newHoldingCost

                    prices.add(gson)
                }
            }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<Unit> {

                        override fun onComplete() {
                            setHistoricalBtcEthHoldings(prices, transactions, holdings, timePeriod)
                        }

                        override fun onNext(cryptoPrices: Unit) {
                        }

                        override fun onSubscribe(d: Disposable) {
                            compositeDisposable?.add(d)
                        }

                        override fun onError(e: Throwable) {
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
                        if (transaction.date < getDate(timePeriod) && timePeriod != PortfolioFragment.TIME_PERIOD_ALL) {

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

                        if (transaction.date < getDate(timePeriod) && timePeriod != PortfolioFragment.TIME_PERIOD_ALL) {

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

            holding.costBtc = newHoldingCostBtc
            holding.costEth = newHoldingCostEth

        }
        getLatestPrices(holdings)
    }

    var baseFiat: Rate = Rate()

    private fun getLatestPrices(holdings: ArrayList<Holding>) { //get the current value of then holdings to then compare with the historical data

        if (dataManager.checkConnection()) {

            var fiatPrices = ExchangeRates()

            var newPrices: ArrayList<Price> = ArrayList()

            var holdingsSorted: ArrayList<Holding> = ArrayList()

            var changeUsd = 0.toBigDecimal()

            val getExchangeRates: Observable<String> = dataManager.getExchangeRateService().getExchangeRates()
            val getMultiPrices: Observable<String> = dataManager.getCryptoCompareServiceWithScalars().getMultiPrice(getCryptoQueryString(holdings), "USD")

            Observable.zip(getExchangeRates, getMultiPrices, BiFunction<String, String, String> { fiatsJson, cryptoPricesJson ->
                fiatPrices = Gson().fromJson(JsonModifiers.jsonToCurrencies(fiatsJson), ExchangeRates::class.java)
                newPrices = getPrices(Gson().fromJson(JsonModifiers.jsonToMultiPrices(cryptoPricesJson), MultiPrices::class.java).prices, fiatPrices)
                ""
            })
                    .subscribeOn(Schedulers.io())
                    .map {
                        holdingsSorted = ArrayList(holdings.sortedBy { holding -> newPrices.first { it.symbol?.toLowerCase() == holding.symbol.toLowerCase() }.prices?.uSD?.times(holding.quantity) }.asReversed())
                    }
                    .flatMapSingle { dataManager.readBaseFiat() }
                    .observeOn(Schedulers.computation())
                    .map { fiat -> baseFiat = fiat }
                    .map { change ->
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
                            view.stopRefreshing()
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

            tempPrices.uSD = 1.toBigDecimal() / (fiatRate.rate ?: 1.toBigDecimal())
            tempPrice.prices = tempPrices

            combinedPrices.add(tempPrice)
        }

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
        }
        return change
    }

    private fun getChangeEth(holdings: ArrayList<Holding>, combinedPrices: ArrayList<Price>): BigDecimal {

        var change = 0.toBigDecimal()

        holdings.filter { it.symbol.toUpperCase() != "ETH" && it.type != Variables.Transaction.Type.fiat }.forEach { holding ->

            val costEthHistorical = holding.costEth //this is what it was at time stamp
            val costEthNow = holding.quantity * (combinedPrices.first { it.symbol?.toUpperCase() == holding.symbol.toUpperCase() }.prices?.uSD ?: 0.toBigDecimal()) / (combinedPrices.first { it.symbol?.toUpperCase() == "ETH" }.prices?.uSD ?: 1.toBigDecimal())

            change += costEthNow - costEthHistorical
        }
        return change
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