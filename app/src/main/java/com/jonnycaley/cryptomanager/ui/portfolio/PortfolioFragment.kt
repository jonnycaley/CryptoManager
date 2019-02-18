package com.jonnycaley.cryptomanager.ui.portfolio

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.NestedScrollView
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import co.ceryle.radiorealbutton.RadioRealButtonGroup
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.MultiPrice.Price
import com.jonnycaley.cryptomanager.data.model.DataBase.Holding
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.Rate
import com.jonnycaley.cryptomanager.ui.markets.MarketsFragment
import com.jonnycaley.cryptomanager.ui.search.SearchArgs
import com.jonnycaley.cryptomanager.utils.Utils
import com.jonnycaley.cryptomanager.utils.interfaces.TabInterface
import java.math.BigDecimal

class PortfolioFragment : Fragment(), PortfolioContract.View, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, TabInterface {

    lateinit var mView: View

    private lateinit var presenter: PortfolioContract.Presenter

    lateinit var holdingsAdapter: HoldingsAdapter

    val swipeLayout by lazy { mView.findViewById<SwipeRefreshLayout>(R.id.swipelayout) }

    val nestedScrollView by lazy { mView.findViewById<NestedScrollView>(R.id.nested_scroll_view) }

    val buttonAddCurrency by lazy { mView.findViewById<Button>(R.id.button_add_currency) }
    val buttonAddFiat by lazy { mView.findViewById<Button>(R.id.button_add_fiat) }

    val layoutEmpty by lazy { mView.findViewById<LinearLayout>(R.id.layout_portfolio_empty) }
    val layoutNotEmty by lazy { mView.findViewById<LinearLayout>(R.id.layout_portfolio_not_empty) }

    val recyclerView by lazy { mView.findViewById<RecyclerView>(R.id.recycler_view) }

    val textBalance by lazy { mView.findViewById<TextView>(R.id.text_balance) }
    val textChange by lazy { mView.findViewById<TextView>(R.id.text_change) }

    val textSortName by lazy { mView.findViewById<TextView>(R.id.text_sort_name) }
    val textSortHoldings by lazy { mView.findViewById<TextView>(R.id.text_sort_holdings) }
    val textSortChange by lazy { mView.findViewById<TextView>(R.id.text_sort_change) }

    val radioGroup: RadioRealButtonGroup by lazy { mView.findViewById<RadioRealButtonGroup>(R.id.radio_group) }

    var chosenPeriod = TIME_PERIOD_1H
    var chosenCurrency = CURRENCY_FIAT
    var chosenSort = SORT_HOLDINGS_DESCENDING

    override fun setPresenter(presenter: PortfolioContract.Presenter) {
        this.presenter = checkNotNull(presenter)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_portfolio, container, false)
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { //set all of the saved data from the onCreate attachview
        super.onViewCreated(view, savedInstanceState)
        //view setup should occur here

        buttonAddCurrency.setOnClickListener(this)
        buttonAddFiat.setOnClickListener(this)
        swipeLayout.setOnRefreshListener(this)
        textBalance.setOnClickListener(this)
        textChange.setOnClickListener(this)

        textSortName.setOnClickListener(this)
        textSortHoldings.setOnClickListener(this)
        textSortChange.setOnClickListener(this)

        setUpPortfolioTimeChoices()

        presenter = PortfolioPresenter(PortfolioDataManager.getInstance(context!!), this)
        presenter.attachView()
    }

    override fun onTabClicked(isTabAlreadyClicked: Boolean) {
        if(isTabAlreadyClicked)
            nestedScrollView.scrollTo(0,0)
        else
            presenter.getTransactions(chosenPeriod)
        Log.i(TAG, "onTabClicked()")
    }

    private fun setUpPortfolioTimeChoices() {

        radioGroup.setOnPositionChangedListener { button, currentPosition, lastPosition ->
            presenter.clearDisposable()
            when (currentPosition) {
                0 -> {
                    chosenPeriod = TIME_PERIOD_1H
                }
                1 -> {
                    chosenPeriod = TIME_PERIOD_1D
                }
                2 -> {
                    chosenPeriod = TIME_PERIOD_1W
                }
                3 -> {
                    chosenPeriod = TIME_PERIOD_1M
                }
                4 -> {
                    chosenPeriod = TIME_PERIOD_ALL
                }
            }
            presenter.getTransactions(chosenPeriod)
        }
    }

    override fun getToggledCurrency(): String {
        return chosenCurrency
    }

    var isPercentage = false

    override fun onClick(v: View?) {
        when (v?.id) {
            buttonAddFiat.id -> {
                SearchArgs(FIAT_STRING).launch(context!!)
            }
            buttonAddCurrency.id -> {
                SearchArgs(CURRENCY_STRING).launch(context!!)
            }
            textBalance.id -> {
                when (chosenCurrency) {
                    CURRENCY_FIAT -> chosenCurrency = CURRENCY_BTC
                    CURRENCY_BTC -> chosenCurrency = CURRENCY_ETH
                    CURRENCY_ETH -> chosenCurrency = CURRENCY_FIAT
                }
                updateView()
            }
            textChange.id -> {
                isPercentage = !isPercentage
                updateView()
            }
            textSortName.id -> {
                if(chosenSort == SORT_NAME_ASCENDING)
                    chosenSort = SORT_NAME_DESCENDING
                else
                    chosenSort = SORT_NAME_ASCENDING
                onSortChanged()
                notifySortTextChanged()
            }
            textSortHoldings.id -> {
                if(chosenSort == SORT_HOLDINGS_DESCENDING)
                    chosenSort = SORT_HOLDINGS_ASCENDING
                else
                    chosenSort = SORT_HOLDINGS_DESCENDING
                onSortChanged()
                notifySortTextChanged()
            }
            textSortChange.id -> {
                if(chosenSort == SORT_CHANGE_DESCENDING)
                    chosenSort = SORT_CHANGE_ASCENDING
                else
                    chosenSort = SORT_CHANGE_DESCENDING
                onSortChanged()
                notifySortTextChanged()
            }
        }
    }

    private fun notifySortTextChanged() {

        textSortName.text = "Name"
        textSortHoldings.text = "Holdings"
        textSortChange.text = "Change"

        when(chosenSort){
            SORT_NAME_ASCENDING -> {
                textSortName.text = "Name▼"
            }
            SORT_NAME_DESCENDING -> {
                textSortName.text = "Name▲"
            }
            SORT_HOLDINGS_ASCENDING -> {
                textSortHoldings.text = "Holdings▲"
            }
            SORT_HOLDINGS_DESCENDING -> {
                textSortHoldings.text = "Holdings▼"
            }
            SORT_CHANGE_ASCENDING -> {
                textSortChange.text = "Change▲"
            }
            SORT_CHANGE_DESCENDING -> {
                textSortChange.text = "Change▼"
            }
        }
    }

    private fun onSortChanged() {
        holdingsAdapter.onSortChanged(chosenSort)
    }

    private fun updateView() {
        showHoldingsLayout()
        showHoldings()
        showBalance()
        showChange()
    }


    var holdings: ArrayList<Holding> = ArrayList()
    var prices: ArrayList<Price> = ArrayList()
    var baseFiat: Rate = Rate()
    var priceBtc = Price()
    var priceEth = Price()
    var balanceUsd = 0.toBigDecimal()
    var balanceBtc = 0.toBigDecimal()
    var balanceEth = 0.toBigDecimal()
    var changeUsd = 0.toBigDecimal()
    var changeBtc = 0.toBigDecimal()
    var changeEth = 0.toBigDecimal()
    var allFiats: ArrayList<Rate> = ArrayList()

    override fun saveData(holdingsSorted: ArrayList<Holding>, newPrices: ArrayList<Price>, baseFiat: Rate, priceBtc: Price, priceEth: Price, balanceUsd: BigDecimal, balanceBtc: BigDecimal, balanceEth: BigDecimal, changeUsd: BigDecimal, changeBtc: BigDecimal, changeEth: BigDecimal) {
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

    override fun saveFiats(rates: List<Rate>?) {
        allFiats.clear()
        rates?.forEach { allFiats.add(it) }
    }

    override fun hideRefreshing() {
        swipeLayout.isRefreshing = false
    }

    override fun onRefresh() {
        presenter.getTransactions(chosenPeriod)
    }

    override fun onResume() {
        super.onResume()
        presenter.getTransactions(chosenPeriod)
    }

    override fun showError() {
        //TODO
    }

    override fun showRefreshing() {
//        swipeLayout.isRefreshing = true
    }

    override fun stopRefreshing() {
//        swipeLayout.isRefreshing = false
    }

    override fun showBalance() {
        when (this.chosenCurrency) { //TODO: HAVE HAVE A PROBLEM HERE WITH TAPPING QUICKLY (UPDATING FAST ENOUGH?)
            CURRENCY_FIAT -> {
                textBalance.text = "${Utils.getFiatSymbol(this.baseFiat.fiat)}${Utils.formatPrice((this.balanceUsd * this.baseFiat.rate!!))}"
            }
            CURRENCY_BTC -> {
                textBalance.text = "BTC ${this.balanceBtc}"//₿
            }
            CURRENCY_ETH -> {
                textBalance.text = "ETH ${this.balanceEth}"//Ξ
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun showChange() {

        when (this.chosenCurrency) {
            CURRENCY_FIAT -> {
                if (!isPercentage) {
                    if (this.changeUsd < 0.toBigDecimal()) {
                        context?.resources?.getColor(R.color.red)?.let { textChange.setTextColor(it) }
                        textChange.text = "-${Utils.getFiatSymbol(this.baseFiat.fiat)}${Utils.formatPrice((this.changeUsd * this.baseFiat.rate!!)).substring(1)}"
                    } else {
                        context?.resources?.getColor(R.color.green)?.let { textChange.setTextColor(it) }
                        textChange.text = "${Utils.getFiatSymbol(this.baseFiat.fiat)}${Utils.formatPrice((this.changeUsd * this.baseFiat.rate!!))}"
                    }
                } else {

                    val change = this.changeUsd
                    var absBalance = this.balanceUsd - this.changeUsd
                    if(absBalance < 0.toBigDecimal())
                        absBalance = (absBalance * (-1).toBigDecimal())

                    if((absBalance.compareTo(0.toBigDecimal()) == 0) || (change.compareTo(0.toBigDecimal()) == 0)){
                        textChange.text = "-"
                        context?.resources?.getColor(R.color.text_grey)?.let { textChange.setTextColor(it) }
                    } else {

                        var changePct = change / absBalance
                        //TODO: absBalance can still be 0 somehow lmao
                        formatPercentage(changePct * 100.toBigDecimal(), textChange)
                    }


//                    if ((this.balanceUsd.toDouble() == this.changeUsd.toDouble()) || (this.balanceUsd - this.changeUsd < 0.toBigDecimal())) {
//                        textChange.text = "-"
//                        context?.resources?.getColor(R.color.text_grey)?.let { textChange.setTextColor(it) }
//                    } else {
//                        if((this.balanceUsd.toDouble() / this.changeUsd.toDouble()) > 0.toDouble()) {
//                            context?.resources?.getColor(R.color.green)?.let { textChange.setTextColor(it) }
//                        }
//                        else {
//                            context?.resources?.getColor(R.color.red)?.let { textChange.setTextColor(it) }
//                        }
//
//                        if(this.balanceUsd - this.changeUsd < 0.toBigDecimal() )
//                        textChange.text = Utils.formatPercentage((this.changeUsd/(this.balanceUsd - this.changeUsd)))
//                    }
                }
            }
            CURRENCY_BTC -> {
                if (!isPercentage) {
                    if (this.changeBtc < 0.toBigDecimal()) {
                        context?.resources?.getColor(R.color.red)?.let { textChange.setTextColor(it) }
                        textChange.text = "-BTC ${Utils.formatPrice(this.changeBtc).substring(1)}"
                    } else {
                        context?.resources?.getColor(R.color.green)?.let { textChange.setTextColor(it) }
                        textChange.text = "BTC ${Utils.formatPrice(this.changeBtc)}"
                    }
                } else {

                    val change = this.changeBtc
                    var absBalance = (this.balanceBtc - this.changeBtc).abs()

                    if((absBalance.compareTo(0.toBigDecimal()) == 0) || (change.compareTo(0.toBigDecimal()) == 0)){
                        textChange.text = "-"
                        context?.resources?.getColor(R.color.text_grey)?.let { textChange.setTextColor(it) }
                    } else {
                        var changePct = change / absBalance
                        formatPercentage(changePct * 100.toBigDecimal(), textChange)
                    }

//                    if ((this.balanceBtc.toDouble()) == (this.changeBtc.toDouble())) {
//                        textChange.text = "-"
//                    } else {
//
////                        var multiplier = 1.toBigDecimal()
////                        if(this.changeEth < 0.toBigDecimal() )
////                            multiplier = -1.toBigDecimal()
//                        textChange.text = Utils.formatPercentage((this.changeBtc) / (this.balanceBtc))
//                    }
                }
            }
            CURRENCY_ETH -> {
                if (!isPercentage) {
                    if (this.changeEth < 0.toBigDecimal()) {
                        context?.resources?.getColor(R.color.red)?.let { textChange.setTextColor(it) }
                        textChange.text = "-ETH ${Utils.formatPrice(this.changeEth).substring(1)}"
                    } else {
                        context?.resources?.getColor(R.color.green)?.let { textChange.setTextColor(it) }
                        textChange.text = "ETH ${Utils.formatPrice(this.changeEth)}"
                    }
                } else {
//                    if ((this.balanceEth) == (this.changeEth)) {
//                        textChange.text = "-"
//                    } else {
////                        var multiplier = 1.toBigDecimal()
////                        if(this.changeEth < 0.toBigDecimal())
////                            multiplier = (-1).toBigDecimal()
//                        textChange.text = Utils.formatPercentage((this.changeEth) / (this.balanceEth))
//                    }

                    val change = this.changeEth
                    var absBalance = (this.balanceEth - this.changeEth).abs()

                    if((absBalance.compareTo(0.toBigDecimal()) == 0) || (change.compareTo(0.toBigDecimal()) == 0)){
                        textChange.text = "-"
                        context?.resources?.getColor(R.color.text_grey)?.let { textChange.setTextColor(it) }
                    } else {
                        var changePct = change / absBalance
                        formatPercentage(changePct * 100.toBigDecimal(), textChange)
                    }
                }
            }
        }
    }

    fun formatPercentage(percentChange24h: BigDecimal?, view : TextView) {

        if(percentChange24h?.compareTo(0.toBigDecimal()) == 0) {
            context?.resources?.getColor(R.color.text_grey)?.let { view.setTextColor(it) }
            view.text =  "-"
        } else {

            val percentage2dp = percentChange24h?.setScale(2, BigDecimal.ROUND_HALF_UP)
            println(percentage2dp)

            return when {
                percentage2dp!! > 0.toBigDecimal() -> {
                    context?.resources?.getColor(R.color.green)?.let { view.setTextColor(it) }
                    view.text = "+$percentage2dp%"
                }
                else -> {
                    context?.resources?.getColor(R.color.red)?.let { view.setTextColor(it) }
                    view.text = "$percentage2dp%"
                }
            }
        }
//
//        val percentage2DP = String.format("%.2f", percentChange24h)
//
////        val percentage2DP = DecimalFormat("#0.00").format(percentChange24h)
//
//        return when {
//            percentage2DP == "0.00" -> {
//                "$percentage2DP%"
////                holder.movement.text = "-"
//            }
//            percentage2DP.toDouble() > 0 -> {
//                "+$percentage2DP%"
//            }
//            else -> {
//                "$percentage2DP%"
//            }
//        }
    }
    override fun showNoHoldingsLayout() {
        layoutEmpty.visibility = View.VISIBLE
        layoutNotEmty.visibility = View.GONE
    }

    override fun showHoldingsLayout() {
        layoutEmpty.visibility = View.GONE
        layoutNotEmty.visibility = View.VISIBLE
    }

    override fun showHoldings() {

        val mLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = mLayoutManager
        holdingsAdapter = HoldingsAdapter(this.holdings, this.prices, this.baseFiat, this.chosenCurrency, this.allFiats, this.isPercentage, context)
        recyclerView.adapter = holdingsAdapter

        holdingsAdapter.onSortChanged(chosenSort)
    }

    fun newInstance(headerStr: String): MarketsFragment {
        val fragmentDemo = MarketsFragment()
        val args = Bundle()
        args.putString("headerStr", headerStr)
        fragmentDemo.arguments = args
        return fragmentDemo
    }

    companion object {

        val TAG = "PortfolioFragment"

        val FIAT_STRING = "FIAT"
        val CURRENCY_STRING = "CURRENCY"

        val TIME_PERIOD_MINUTE = "minute"
        val TIME_PERIOD_HOUR = "hour"
        val TIME_PERIOD_DAY = "day"

        val AGGREGATE_1H = "60"
        val AGGREGATE_1D = "24"
        val AGGREGATE_1W = "168"
        val AGGREGATE_1M = "30"

        val TIME_PERIOD_1H = "1H"
        val TIME_PERIOD_1D = "1D"
        val TIME_PERIOD_1W = "1W"
        val TIME_PERIOD_1M = "1M"
        val TIME_PERIOD_ALL = "ALL"

        val CURRENCY_FIAT = "FIAT"
        val CURRENCY_BTC = "BTC"
        val CURRENCY_ETH = "ETH"

        val SORT_HOLDINGS_ASCENDING = "HOLDINGS_ASCENDING"
        val SORT_NAME_ASCENDING = "NAME_ASCENDING"
        val SORT_CHANGE_ASCENDING = "CHANGE_ASCENDING"

        val SORT_HOLDINGS_DESCENDING = "HOLDINGS_DESCENDING"
        val SORT_NAME_DESCENDING = "NAME_DESCENDING"
        val SORT_CHANGE_DESCENDING = "CHANGE_DESCENDING"
    }

}