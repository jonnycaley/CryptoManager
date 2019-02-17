package com.jonnycaley.cryptomanager.ui.markets

import android.graphics.Typeface
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Currency
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.Rate
import com.jonnycaley.cryptomanager.utils.interfaces.TabInterface
import android.support.v4.widget.NestedScrollView
import android.util.Log
import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Market.Market
import com.jonnycaley.cryptomanager.utils.Utils
import com.jonnycaley.cryptomanager.utils.interfaces.PaginationScrollListener
import java.math.BigDecimal


class MarketsFragment : Fragment(), MarketsContract.View, TabInterface, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    lateinit var root: View

    private lateinit var presenter: MarketsContract.Presenter

    lateinit var currenciesAdapter: CurrenciesAdapter

    val recyclerViewCurrencies by lazy { root.findViewById<RecyclerView>(R.id.recycler_view_currencies) }
    val searchView by lazy { root.findViewById<SearchView>(R.id.search_view_currencies) }
    val progressBar by lazy { root.findViewById<ProgressBar>(R.id.progress_bar_bottom) }
    val progressBarLayout by lazy { root.findViewById<ConstraintLayout>(R.id.progress_bar_layout) }
    val swipeRefreshLayout by lazy { root.findViewById<SwipeRefreshLayout>(R.id.swipelayout) }
    val nestedScrollView by lazy { root.findViewById<NestedScrollView>(R.id.nested_scroll_view) }

    val rank by lazy { root.findViewById<TextView>(R.id.rank) }
    val name by lazy { root.findViewById<TextView>(R.id.name) }
    val price by lazy { root.findViewById<TextView>(R.id.price) }
    val change by lazy { root.findViewById<TextView>(R.id.change) }

    val textMarketCap by lazy { root.findViewById<TextView>(R.id.text_market_cap) }
    val textVolume by lazy { root.findViewById<TextView>(R.id.text_volume) }
    val textBTCDominance by lazy { root.findViewById<TextView>(R.id.text_btc_dominance) }

    val text1H by lazy { root.findViewById<TextView>(R.id.text_1H) }
    val text1D by lazy { root.findViewById<TextView>(R.id.text_1D) }
    val text1W by lazy { root.findViewById<TextView>(R.id.text_1W) }

    var isLastPage = false
    var isLoading = false

    var filter = FILTER_NONE

    var timeframe = TIMEFRAME_1H

    override fun setPresenter(presenter: MarketsContract.Presenter) {
        this.presenter = checkNotNull(presenter)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.fragment_markets, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { //set all of the saved data from the onCreate attachview
        super.onViewCreated(view, savedInstanceState)

        swipeRefreshLayout.setOnRefreshListener(this)

        rank.setOnClickListener(this)
        name.setOnClickListener(this)
        price.setOnClickListener(this)
        change.setOnClickListener(this)

        text1H.setOnClickListener(this)
        text1D.setOnClickListener(this)
        text1W.setOnClickListener(this)

        presenter = MarketsPresenter(MarketsDataManager.getInstance(context!!), this)
        presenter.attachView()
    }

    override fun onClick(v: View?) {
        when(v?.id){
            rank.id -> {
                if(filter == FILTER_RANK_DOWN)
                    filter = FILTER_RANK_UP
                else
                    filter = FILTER_RANK_DOWN

                changeSortText()
                notifyFilterChanged()
            }
            name.id -> {
                if(filter == FILTER_NAME_DOWN)
                    filter = FILTER_NAME_UP
                else
                    filter = FILTER_NAME_DOWN

                changeSortText()
                notifyFilterChanged()
            }
            price.id -> {
                if(filter == FILTER_PRICE_DOWN)
                    filter = FILTER_PRICE_UP
                else
                    filter = FILTER_PRICE_DOWN

                changeSortText()
                notifyFilterChanged()
            }
            change.id -> {
                if(filter == FILTER_CHANGE_DOWN)
                    filter = FILTER_CHANGE_UP
                else
                    filter = FILTER_CHANGE_DOWN
                changeSortText()
                notifyFilterChanged()
            }
            text1H.id -> {
                timeframe = TIMEFRAME_1H
                changeTimeFrameText()
                notifyTimeFrameChanged()
            }
            text1D.id -> {
                timeframe = TIMEFRAME_1D
                changeTimeFrameText()
                notifyTimeFrameChanged()
            }
            text1W.id -> {
                timeframe = TIMEFRAME_1W
                changeTimeFrameText()
                notifyTimeFrameChanged()
            }
        }
    }

    private fun changeTimeFrameText() {

        text1H.setTypeface(null, Typeface.NORMAL)
        text1D.setTypeface(null, Typeface.NORMAL)
        text1W.setTypeface(null, Typeface.NORMAL)

        when(timeframe) {
            TIMEFRAME_1H -> {
                text1H.setTypeface(text1H.typeface, Typeface.BOLD)
            }
            TIMEFRAME_1D -> {
                text1D.setTypeface(text1D.typeface, Typeface.BOLD)
            }
            TIMEFRAME_1W -> {
                text1W.setTypeface(text1W.typeface, Typeface.BOLD)
            }
        }
    }

    private fun notifyTimeFrameChanged() {
        //change the percentage that shows
        if(mLayoutManager != null) {
            currenciesAdapter = CurrenciesAdapter(currenciesAdapter.currencies, currenciesAdapter.baseFiat, context, timeframe)
            recyclerViewCurrencies.adapter = currenciesAdapter
            currenciesAdapter.sort(filter)
        }
    }

    private fun changeSortText() {
        rank.text = "#"
        name.text = "Name (Symbol)"
        price.text = "Price"
        change.text = "Change"

        when(filter){
            FILTER_RANK_DOWN -> {
                rank.text = "#▼"
            }
            FILTER_RANK_UP -> {
                rank.text = "#▲"
            }
            FILTER_NAME_DOWN -> {
                name.text = "Name (Symbol)▼"
            }
            FILTER_NAME_UP -> {
                name.text = "Name (Symbol)▲"
            }
            FILTER_PRICE_DOWN -> {
                price.text = "Price▼"
            }
            FILTER_PRICE_UP -> {
                price.text = "Price▲"
            }
            FILTER_CHANGE_DOWN -> {
                change.text = "Change▼"
            }
            FILTER_CHANGE_UP -> {
                change.text = "Change▲"
            }
        }
    }

    private fun notifyFilterChanged() {
        if(mLayoutManager != null)
            currenciesAdapter.sort(filter)
    }

    override fun showMarketData(marketData: Market?) {

        textMarketCap.text = "$${truncateNumber(marketData?.data?.quote?.uSD?.totalMarketCap!!)}"

        textVolume.text = "$${truncateNumber(marketData?.data?.quote?.uSD?.totalVolume24h!!)}"

        textBTCDominance.text = Utils.formatPercentage(marketData?.data?.btcDominance!!.toBigDecimal()).substring(1)

    }

    var mLayoutManager : LinearLayoutManager? = null

    override fun getCurrenciesAdapterCount(): Int {
        if(mLayoutManager == null)
            return 100
        else
            return currenciesAdapter.currencies?.size ?: 100
    }

    override fun getSort(): String {
        return filter
    }

    override fun showTop100Changes(currencies: List<Currency>?, baseFiat: Rate, resultsCount: Int) {

        Log.i(MarketsPresenter.TAG, currencies?.size.toString() + " S E E  H E R E1")

        if(mLayoutManager == null) {

            Log.i(MarketsPresenter.TAG, currencies?.size.toString() + " S E E  H E R E2")

            mLayoutManager = LinearLayoutManager(context)
            recyclerViewCurrencies.layoutManager = mLayoutManager
            currenciesAdapter = CurrenciesAdapter(ArrayList(currencies), baseFiat, context, timeframe)
            recyclerViewCurrencies.adapter = currenciesAdapter

            nestedScrollView.setOnScrollChangeListener(object : PaginationScrollListener(mLayoutManager!!) {
                override fun isLastPage(): Boolean {
                    return isLastPage
                }

                override fun isLoading(): Boolean {
                    return isLoading
                }

                override fun loadMoreItems() {
                    isLoading = true
                    if(resultsCount > currenciesAdapter.currencies?.size!!){
                        presenter.loadMoreItems(currenciesAdapter.currencies, resultsCount - currenciesAdapter.currencies!!.size, searchView.query.trim())
                        progressBar.visibility = View.VISIBLE
                    } else {
                        progressBar.visibility = View.GONE
                    }
                }
            })
        } else {

            Log.i(MarketsPresenter.TAG, currencies?.size.toString() + " S E E  H E R E3")
            Log.i(MarketsPresenter.TAG, resultsCount.toString())
            Log.i(MarketsPresenter.TAG, currenciesAdapter.currencies?.size.toString()!!)

            currenciesAdapter.swap(ArrayList(currencies), baseFiat)

            if(currencies?.size!! < 100 || resultsCount <= currenciesAdapter.currencies?.size!!) {
                println("1")
                progressBar.visibility = View.GONE

                nestedScrollView.setOnScrollChangeListener(object : PaginationScrollListener(mLayoutManager!!) {
                    override fun isLastPage(): Boolean {
                        return false
                    }

                    override fun isLoading(): Boolean {
                        return false
                    }

                    override fun loadMoreItems() {

                    }
                })
            } else {
                println("2")
                nestedScrollView.setOnScrollChangeListener(object : PaginationScrollListener(mLayoutManager!!) {
                    override fun isLastPage(): Boolean {
                        return isLastPage
                    }

                    override fun isLoading(): Boolean {
                        return isLoading
                    }

                    override fun loadMoreItems() {
                        isLoading = true
                        if(resultsCount > currenciesAdapter.currencies?.size!!){
                            presenter.loadMoreItems(currenciesAdapter.currencies, resultsCount - currenciesAdapter.currencies!!.size, searchView.query.trim())
                            progressBar.visibility = View.VISIBLE
                        } else {
                            progressBar.visibility = View.GONE
                        }
                    }
                })
            }
        }
        currenciesAdapter.sort(filter)
    }

    var layoutManager: LinearLayoutManager? = null

    override fun onRefresh() {
        presenter.loadMoreItems(null, presenter.getResultsCounter() - currenciesAdapter.currencies?.size!!, searchView.query.trim())
    }

    override fun stopRefreshing() {
        swipeRefreshLayout.isRefreshing = false
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }
    override fun onTabClicked(isTabAlreadyClicked: Boolean) {
        if(isTabAlreadyClicked) {
            nestedScrollView.scrollTo(0, 0)
            nestedScrollView.fling(0)
        }
        //scroll to top
        else
            presenter.refresh()
    }

    override fun hideProgressBarLayout() {
        progressBarLayout.visibility = View.GONE
        swipeRefreshLayout.isRefreshing = false
        Log.i(TAG, "${swipeRefreshLayout.isRefreshing}")
    }

    val MILLION = 1000000L.toBigDecimal()
    val BILLION = 1000000000L.toBigDecimal()
    val TRILLION = 1000000000000.toBigDecimal()

    fun truncateNumber(x: BigDecimal): String {
        return if (x < MILLION)
            x.toString()
        else if (x < BILLION)
            String.format("%.2f", (x / MILLION).toString()) + " M"
        else if (x < TRILLION)
            String.format("%.2f", x / BILLION)+ " B"
        else
            String.format("%.2f", x / TRILLION) + " T"
    }

    override fun showContentLayout() {
        swipeRefreshLayout.visibility = View.VISIBLE
    }

    override fun showProgressBarLayout() {
        progressBarLayout.visibility = View.VISIBLE
    }

    override fun hideContentLayout() {
        swipeRefreshLayout.visibility = View.GONE
    }

    override fun getCurrencySearchView(): SearchView {
        return searchView
    }

    fun newInstance(headerStr: String): MarketsFragment {
        val fragmentDemo = MarketsFragment()
        val args = Bundle()
        args.putString("headerStr", headerStr)
        fragmentDemo.arguments = args
        return fragmentDemo
    }

    companion object {
        val TAG = "MarketsFragment"

        val FILTER_NONE = "FILTER_NONE"

        val FILTER_RANK_DOWN = "FILTER_RANK_DOWN"
        val FILTER_RANK_UP = "FILTER_RANK_UP"

        val FILTER_NAME_DOWN = "FILTER_NAME_DOWN"
        val FILTER_NAME_UP = "FILTER_NAME_UP"

        val FILTER_PRICE_DOWN = "FILTER_PRICE_DOWN"
        val FILTER_PRICE_UP = "FILTER_PRICE_UP"

        val FILTER_CHANGE_DOWN = "FILTER_CHANGE_DOWN"
        val FILTER_CHANGE_UP = "FILTER_CHANGE_UP"

        val TIMEFRAME_1H = "TIMEFRAME_1H"
        val TIMEFRAME_1D = "TIMEFRAME_1D"
        val TIMEFRAME_1W = "TIMEFRAME_1W"

    }
}