package com.jonnycaley.cryptomanager.ui.portfolio

import android.os.Bundle
import android.support.v4.app.Fragment
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

    lateinit var holdingsAdapter : HoldingsAdapter

    val swipeLayout by lazy { mView.findViewById<SwipeRefreshLayout>(R.id.swipelayout) }

    val buttonAddCurrency by lazy { mView.findViewById<Button>(R.id.button_add_currency) }
    val buttonAddFiat by lazy { mView.findViewById<Button>(R.id.button_add_fiat) }

    val layoutEmpty by lazy { mView.findViewById<LinearLayout>(R.id.layout_portfolio_empty) }
    val layoutNotEmty by lazy { mView.findViewById<LinearLayout>(R.id.layout_portfolio_not_empty) }

    val recyclerView by lazy { mView.findViewById<RecyclerView>(R.id.recycler_view) }

    val textBalance by lazy { mView.findViewById<TextView>(R.id.text_balance) }
    val textChange by lazy { mView.findViewById<TextView>(R.id.text_change) }

    val radioGroup : RadioRealButtonGroup by lazy { mView.findViewById<RadioRealButtonGroup>(R.id.radio_group) }

    var chosenPeriod = TIME_PERIOD_1H
    var chosenCurrency = CURRENCY_FIAT

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
        setUpPortfolioTimeChoices()

        presenter = PortfolioPresenter(PortfolioDataManager.getInstance(context!!), this)
        presenter.attachView()
    }

    override fun onTabClicked() {
        Log.i(TAG, "onTabClicked()")
    }

    private fun setUpPortfolioTimeChoices() {

        radioGroup.setOnPositionChangedListener { button, currentPosition, lastPosition ->
            presenter.clearDisposable()
            when(currentPosition){
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

    override fun onClick(v: View?) {
        when(v?.id){
            buttonAddFiat.id -> {
                SearchArgs(FIAT_STRING).launch(context!!)
            }
            buttonAddCurrency.id -> {
                SearchArgs(CURRENCY_STRING).launch(context!!)
            }
            textBalance.id -> {
                when(chosenCurrency){
                    CURRENCY_FIAT -> chosenCurrency = CURRENCY_BTC
                    CURRENCY_BTC -> chosenCurrency = CURRENCY_ETH
                    CURRENCY_ETH -> chosenCurrency = CURRENCY_FIAT
                }
                updateView()
            }
        }
    }

    private fun updateView() {

        showHoldingsLayout()
        showHoldings()
        showBalance()
        showChange()
    }


    var holdings: ArrayList<Holding> = ArrayList()
    var prices: ArrayList<Price> = ArrayList()
    var baseFiat : Rate = Rate()
    var priceBtc = Price()
    var priceEth = Price()
    var balance = 0.toBigDecimal()
    var changeUsd = 0.toBigDecimal()
    var changeBtc = 0.toBigDecimal()
    var changeEth = 0.toBigDecimal()

    override fun saveData(holdingsSorted: ArrayList<Holding>, newPrices: ArrayList<Price>, baseFiat: Rate, priceBtc: Price, priceEth: Price, balance: BigDecimal, changeUsd: BigDecimal, changeBtc: BigDecimal, changeEth: BigDecimal) {
        this.holdings = holdingsSorted
        this.prices = newPrices
        this.baseFiat = baseFiat
        this.priceBtc = priceBtc
        this.priceEth = priceEth
        this.balance = balance
        this.changeUsd = changeUsd
        this.changeBtc = changeBtc
        this.changeEth = changeEth
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
        when(this.chosenCurrency){ //TODO: HAVE HAVE A PROBLEM HERE WITH TAPPING QUICKLY (UPDATING FAST ENOUGH?)
            CURRENCY_FIAT -> {
                textBalance.text = "${Utils.getFiatSymbol(this.baseFiat.fiat)}${Utils.formatPrice((this.balance * this.baseFiat.rate?.toBigDecimal()!!).toDouble())}"
            }
            CURRENCY_BTC -> {
                textBalance.text = "BTC ${Utils.formatPrice((this.balance / this.priceBtc.prices?.uSD?.toBigDecimal()!!).toDouble())}"//₿
            }
            CURRENCY_ETH -> {
                textBalance.text = "ETH ${Utils.formatPrice((this.balance / this.priceEth.prices?.uSD?.toBigDecimal()!!).toDouble())}"//Ξ
            }
        }
    }

    override fun showChange() {

        when(this.chosenCurrency){ //TODO: HAVE HAVE A PROBLEM HERE WITH TAPPING QUICKLY (UPDATING FAST ENOUGH?)
            CURRENCY_FIAT -> {
                if (this.changeUsd < 0.toBigDecimal()) {
                    context?.resources?.getColor(R.color.red)?.let { textChange.setTextColor(it) }
                    textChange.text = "-${Utils.getFiatSymbol(this.baseFiat.fiat)}${Utils.formatPrice((this.changeUsd*this.baseFiat.rate?.toBigDecimal()!!).toDouble()).substring(1)}"
                } else {
                    context?.resources?.getColor(R.color.green)?.let { textChange.setTextColor(it) }
                    textChange.text = "${Utils.getFiatSymbol(this.baseFiat.fiat)}${Utils.formatPrice((this.changeUsd*this.baseFiat.rate?.toBigDecimal()!!).toDouble()!!)}"
                }
            }
            CURRENCY_BTC -> {
                if (this.changeBtc < 0.toBigDecimal()) {
                    context?.resources?.getColor(R.color.red)?.let { textChange.setTextColor(it) }
                    textChange.text = "-BTC ${Utils.formatPrice(this.changeBtc.toDouble()).substring(1)}"
                } else {
                    context?.resources?.getColor(R.color.green)?.let { textChange.setTextColor(it) }
                    textChange.text = "BTC ${Utils.formatPrice(this.changeBtc.toDouble())}"
                }
            }
            CURRENCY_ETH -> {
                if (this.changeBtc < 0.toBigDecimal()) {
                    context?.resources?.getColor(R.color.red)?.let { textChange.setTextColor(it) }
                    textChange.text = "-ETH ${Utils.formatPrice(this.changeEth.toDouble()).substring(1)}"
                } else {
                    context?.resources?.getColor(R.color.green)?.let { textChange.setTextColor(it) }
                    textChange.text = "ETH ${Utils.formatPrice(this.changeEth.toDouble())}"
                }
            }
        }
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
        holdingsAdapter = HoldingsAdapter(this.holdings, this.prices, this.baseFiat, this.chosenCurrency, context)
        recyclerView.adapter = holdingsAdapter

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
    }

}