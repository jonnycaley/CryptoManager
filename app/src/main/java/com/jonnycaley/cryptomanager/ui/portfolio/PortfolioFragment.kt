package com.jonnycaley.cryptomanager.ui.portfolio

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.NestedScrollView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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

class PortfolioFragment : androidx.fragment.app.Fragment(), PortfolioContract.View, View.OnClickListener, androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener, TabInterface {

    lateinit var mView: View

    private lateinit var presenter: PortfolioContract.Presenter

    lateinit var holdingsAdapter: HoldingsAdapter

    val layoutNoInternet by lazy { mView.findViewById<RelativeLayout>(R.id.layout_no_internet) }
    val textTryAgain by lazy { mView.findViewById<TextView>(R.id.text_try_again) }

    val imageNoInternet by lazy { mView.findViewById<ImageView>(R.id.image_no_internet) }

    val swipeLayout by lazy { mView.findViewById<androidx.swiperefreshlayout.widget.SwipeRefreshLayout>(R.id.swipelayout) }

    val nestedScrollView by lazy { mView.findViewById<NestedScrollView>(R.id.nested_scroll_view) }

    val buttonAddCurrency by lazy { mView.findViewById<Button>(R.id.button_add_currency) }
    val buttonAddFiat by lazy { mView.findViewById<Button>(R.id.button_add_fiat) }

    val layoutEmpty by lazy { mView.findViewById<LinearLayout>(R.id.layout_portfolio_empty) }
    val layoutNotEmty by lazy { mView.findViewById<LinearLayout>(R.id.layout_portfolio_not_empty) }

    val recyclerView by lazy { mView.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recycler_view) }

    val textBalance by lazy { mView.findViewById<TextView>(R.id.text_balance) }
    val textChange by lazy { mView.findViewById<TextView>(R.id.text_change) }

    val textPortfolioBalance by lazy { mView.findViewById<TextView>(R.id.text_portfolio_balance) }

    val textSortName by lazy { mView.findViewById<TextView>(R.id.text_sort_name) }
    val textSortHoldings by lazy { mView.findViewById<TextView>(R.id.text_sort_holdings) }
    val textSortChange by lazy { mView.findViewById<TextView>(R.id.text_sort_change) }

    val layoutProgress by lazy { mView.findViewById<ConstraintLayout>(R.id.layout_progress) }
    val progressBarHorizontal by lazy { mView.findViewById<ProgressBar>(R.id.progress_bar_horizontal) }

    val radioGroup: RadioRealButtonGroup by lazy { mView.findViewById<RadioRealButtonGroup>(R.id.radio_group) }

    var chosenPeriod = TIME_PERIOD_1H
    var chosenCurrency = CURRENCY_FIAT
    var chosenSort = SORT_HOLDINGS_DESCENDING

    var isColdStartup = true

    override fun setPresenter(presenter: PortfolioContract.Presenter) {
        this.presenter = checkNotNull(presenter)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_portfolio, container, false)
        return mView
    }

    override fun isNotColdStartup() {
        isColdStartup = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { //set all of the saved data from the onCreate attachview
        super.onViewCreated(view, savedInstanceState)
        //view setup should occur here

        val custom_font = Typeface.createFromAsset(context?.applicationContext?.assets, "fonts/Roboto-Bold.ttf")

        textPortfolioBalance.typeface = custom_font

        buttonAddCurrency.setOnClickListener(this) //set listener
        buttonAddFiat.setOnClickListener(this) //set listener
        swipeLayout.setOnRefreshListener(this) //set listener
        textBalance.setOnClickListener(this) //set listener
        textChange.setOnClickListener(this) //set listener

        textSortName.setOnClickListener(this) //set listener
        textSortHoldings.setOnClickListener(this) //set listener
        textSortChange.setOnClickListener(this) //set listener
        textTryAgain.setOnClickListener(this) //set listener

        if(Utils.isDarkTheme())
            imageNoInternet.setImageResource(R.drawable.no_internet_white) //change icon
        else
            imageNoInternet.setImageResource(R.drawable.no_internet_black) //change icon

        setUpPortfolioTimeChoices() //set choices

        presenter = PortfolioPresenter(PortfolioDataManager.getInstance(context!!), this) //attach presenter
        presenter.attachView()
    }

    /*
    Function when a tab is clicked
    */
    override fun onTabClicked(isTabAlreadyClicked: Boolean) {
        if (isTabAlreadyClicked)
            nestedScrollView.scrollTo(0, 0)
        if(layoutNoInternet.visibility == View.VISIBLE && Utils.isNetworkConnected(context!!))
            presenter.getTransactions(chosenPeriod)
        Log.i(TAG, "onTabClicked()")
    }

    /*
    Function sets up the portfolio time choices
    */
    private fun setUpPortfolioTimeChoices() {

        radioGroup.setOnPositionChangedListener { button, currentPosition, lastPosition -> //set listener
            presenter.clearDisposable()
            when (currentPosition) {
                0 -> {
                    chosenPeriod = TIME_PERIOD_1H //time period config
                }
                1 -> {
                    chosenPeriod = TIME_PERIOD_1D //time period config
                }
                2 -> {
                    chosenPeriod = TIME_PERIOD_1W //time period config
                }
                3 -> {
                    chosenPeriod = TIME_PERIOD_1M //time period config
                }
                4 -> {
                    chosenPeriod = TIME_PERIOD_ALL //time period config
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
                context?.let { SearchArgs(FIAT_STRING).launch(it) }
            }
            buttonAddCurrency.id -> {
                context?.let { SearchArgs(CURRENCY_STRING).launch(it) }
            }
            textBalance.id -> {
                when (chosenCurrency) {
                    CURRENCY_FIAT -> chosenCurrency = CURRENCY_BTC
                    CURRENCY_BTC -> chosenCurrency = CURRENCY_ETH
                    CURRENCY_ETH -> chosenCurrency = CURRENCY_FIAT
                }
                presenter.updateView()
            }
            textChange.id -> {
                isPercentage = !isPercentage
                presenter.updateView()
            }
            textSortName.id -> {
                if (chosenSort == SORT_NAME_ASCENDING)
                    chosenSort = SORT_NAME_DESCENDING
                else
                    chosenSort = SORT_NAME_ASCENDING
                onSortChanged()
                notifySortTextChanged()
            }
            textSortHoldings.id -> {
                if (chosenSort == SORT_HOLDINGS_DESCENDING)
                    chosenSort = SORT_HOLDINGS_ASCENDING
                else
                    chosenSort = SORT_HOLDINGS_DESCENDING
                onSortChanged()
                notifySortTextChanged()
            }
            textSortChange.id -> {
                if (chosenSort == SORT_CHANGE_DESCENDING)
                    chosenSort = SORT_CHANGE_ASCENDING
                else
                    chosenSort = SORT_CHANGE_DESCENDING
                onSortChanged()
                notifySortTextChanged()
            }
            textTryAgain.id -> {
                presenter.getTransactions(chosenPeriod)
            }
        }
    }

    /*
    Function sets the text color secondary
    */
    fun setTextColorSecondary(textView: TextView) {
        if(Utils.isDarkTheme()){
            textView.setTextColor(resources.getColor(R.color.dark_text_color_secondary))
        } else {
            textView.setTextColor(resources.getColor(R.color.light_text_color_secondary))
        }
    }


    /*
    Function sets the text color primary
    */
    fun setTextColorPrimary(textView: TextView) {
        if(Utils.isDarkTheme()){
            textView.setTextColor(resources.getColor(R.color.dark_text_color))
        } else {
            textView.setTextColor(resources.getColor(R.color.light_text_color))
        }
    }

    private fun notifySortTextChanged() {

        textSortName.text = "Name"
        textSortHoldings.text = "Holdings"
        textSortChange.text = "Price"


        setTextColorSecondary(textSortName)
        setTextColorSecondary(textSortHoldings)
        setTextColorSecondary(textSortChange)

        when (chosenSort) {
            SORT_NAME_ASCENDING -> {
                textSortName.text = "Name▼"
                setTextColorPrimary(textSortName)
            }
            SORT_NAME_DESCENDING -> {
                textSortName.text = "Name▲"
                setTextColorPrimary(textSortName)
            }
            SORT_HOLDINGS_ASCENDING -> {
                textSortHoldings.text = "▲Holdings"
                setTextColorPrimary(textSortHoldings)
            }
            SORT_HOLDINGS_DESCENDING -> {
                textSortHoldings.text = "▼Holdings"
                setTextColorPrimary(textSortHoldings)
            }
            SORT_CHANGE_ASCENDING -> {
                textSortChange.text = "▲Price"
                setTextColorPrimary(textSortChange)
            }
            SORT_CHANGE_DESCENDING -> {
                textSortChange.text = "▼Price"
                setTextColorPrimary(textSortChange)
            }
        }
    }

    private fun onSortChanged() {
        holdingsAdapter.onSortChanged(chosenSort)
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

    }

    override fun hideProgressLayout() {
        layoutProgress.visibility = View.GONE
    }

    override fun showNoInternet() {
        if(isColdStartup)
            layoutNoInternet.visibility = View.VISIBLE
    }

    override fun hideInternetRequiredLayout() {
        layoutNoInternet.visibility = View.GONE
    }

    override fun showRefreshing() {
        if(!swipeLayout.isRefreshing)
            progressBarHorizontal.visibility = View.VISIBLE
//        swipeLayout.isRefreshing = true
    }

    override fun stopRefreshing() {
        progressBarHorizontal.visibility = View.GONE
        swipeLayout.isRefreshing = false
//        swipeLayout.isRefreshing = false
    }

    /*
    Function shows the balance text
    */
    @SuppressLint("SetTextI18n")
    override fun showBalance(baseFiat: Rate, balanceUsd: BigDecimal, balanceBtc: BigDecimal, balanceEth: BigDecimal) {

        val fiat = Utils.getFiatSymbol(baseFiat.fiat)

        Log.i(TAG, "BAL"+balanceUsd.toString())

        when (this.chosenCurrency) {
            CURRENCY_FIAT -> {
                textBalance.text = Utils.getPriceTextAbs((balanceUsd * (baseFiat.rate ?: 1.toBigDecimal())).toDouble(), fiat)
            }
            CURRENCY_BTC -> {
                textBalance.text = Utils.getPriceTextAbs(balanceBtc.toDouble(), "฿")
            }
            CURRENCY_ETH -> {
                textBalance.text = Utils.getPriceTextAbs(balanceEth.toDouble(), "Ξ")
            }
        }
    }

    /*
    Function shows the change texts
    */
    @SuppressLint("SetTextI18n")
    override fun showChange(baseFiat: Rate, balanceUsd: BigDecimal, balanceBtc: BigDecimal, balanceEth: BigDecimal, changeUsd: BigDecimal, changeBtc: BigDecimal, changeEth: BigDecimal) {

        val fiat = Utils.getFiatSymbol(baseFiat.fiat)

        when (this.chosenCurrency) {
            CURRENCY_FIAT -> {
                if (!isPercentage) {
                    textChange.text = Utils.getPriceTextAbs((changeUsd * (baseFiat.rate ?: 1.toBigDecimal())).toDouble(), fiat)
                    if (changeUsd < 0.toBigDecimal()) {
                        context?.resources?.getColor(R.color.red)?.let { textChange.setTextColor(it) }
                    } else if(changeUsd == 0.toBigDecimal()) {
                        context?.resources?.getColor(R.color.black)?.let { textChange.setTextColor(it) }
                    } else {
                        context?.resources?.getColor(R.color.green)?.let { textChange.setTextColor(it) }
                    }
                } else {
                    val change = changeUsd
                    var absBalance = balanceUsd - changeUsd
                    if (absBalance < 0.toBigDecimal())
                        absBalance = (absBalance * (-1).toBigDecimal())

                    if ((absBalance.compareTo(0.toBigDecimal()) == 0) || (change.compareTo(0.toBigDecimal()) == 0)) {
                        textChange.text = "-"
                        context?.resources?.getColor(R.color.text_grey)?.let { textChange.setTextColor(it) }
                    } else {

                        val changePct = change / absBalance

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
                    textChange.text = Utils.getPriceTextAbs(changeBtc.toDouble(), "฿")
                    if (changeBtc < 0.toBigDecimal()) {
                        context?.resources?.getColor(R.color.red)?.let { textChange.setTextColor(it) }
                    } else if(changeBtc == 0.toBigDecimal()) {
                        context?.resources?.getColor(R.color.black)?.let { textChange.setTextColor(it) }
                    } else {
                        context?.resources?.getColor(R.color.green)?.let { textChange.setTextColor(it) }
                    }
                } else {

                    val change = changeBtc
                    val absBalance = (balanceBtc - changeBtc).abs()

                    if ((absBalance.compareTo(0.toBigDecimal()) == 0) || (change.compareTo(0.toBigDecimal()) == 0)) {
                        textChange.text = "-"
                        context?.resources?.getColor(R.color.text_grey)?.let { textChange.setTextColor(it) }
                    } else {
                        val changePct = change / absBalance
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
                    textChange.text = Utils.getPriceTextAbs(changeEth.toDouble(), "Ξ")
                    if (changeEth < 0.toBigDecimal()) {
                        context?.resources?.getColor(R.color.red)?.let { textChange.setTextColor(it) }
                    } else if(changeEth == 0.toBigDecimal()) {
                        context?.resources?.getColor(R.color.black)?.let { textChange.setTextColor(it) }
                    } else {
                        context?.resources?.getColor(R.color.green)?.let { textChange.setTextColor(it) }
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
                    val change = changeEth
                    val absBalance = (balanceEth - changeEth).abs()

                    if ((absBalance.compareTo(0.toBigDecimal()) == 0) || (change.compareTo(0.toBigDecimal()) == 0)) {
                        textChange.text = "-"
                        context?.resources?.getColor(R.color.text_grey)?.let { textChange.setTextColor(it) }
                    } else {
                        val changePct = change / absBalance
                        formatPercentage(changePct * 100.toBigDecimal(), textChange)
                    }
                }
            }
        }
    }

    /*
    Function formats the percentage
    */
    fun formatPercentage(percentChange24h: BigDecimal, view: TextView) {

        if (percentChange24h.compareTo(0.toBigDecimal()) == 0) {
            context?.resources?.getColor(R.color.text_grey)?.let { view.setTextColor(it) }
            view.text = "-"
        } else {

            val percentage2dp = percentChange24h.setScale(2, BigDecimal.ROUND_HALF_UP)

            return when {
                percentage2dp > 0.toBigDecimal() -> {
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

    override fun showHoldings(holdings: ArrayList<Holding>, prices: ArrayList<Price>, baseFiat: Rate, allFiats: ArrayList<Rate>) {

        if(holdings.isEmpty())
            showNoHoldingsLayout()
        else {

            showHoldingsLayout()

            val mLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)

            recyclerView.layoutManager = mLayoutManager
            holdingsAdapter = HoldingsAdapter(holdings, prices, baseFiat, this.chosenCurrency, allFiats, this.isPercentage, context)
            recyclerView.adapter = holdingsAdapter

            holdingsAdapter.onSortChanged(chosenSort)
        }
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