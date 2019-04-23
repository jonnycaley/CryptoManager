package com.jonnycaley.cryptomanager.ui.markets

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Currency
import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Market.Market
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.Rate
import com.jonnycaley.cryptomanager.utils.Utils
import com.jonnycaley.cryptomanager.utils.interfaces.TabInterface
import java.math.BigDecimal

class MarketsFragment : androidx.fragment.app.Fragment(), MarketsContract.View, TabInterface, androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    lateinit var root: View

    private lateinit var presenter: MarketsContract.Presenter

    lateinit var currenciesAdapter: CurrenciesAdapter

    val layoutNoInternet by lazy { root.findViewById<RelativeLayout>(R.id.layout_no_internet) }
    val imageNoInternet by lazy { root.findViewById<ImageView>(R.id.image_no_internet) }
    val textTryAgain by lazy { root.findViewById<TextView>(R.id.text_try_again) }

    val recyclerViewCurrencies by lazy { root.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recycler_view_currencies) }
    val searchView by lazy { root.findViewById<SearchView>(R.id.search_view_currencies) }
    val progressBar by lazy { root.findViewById<ProgressBar>(R.id.progress_bar_bottom) }
    val progressBarLayout by lazy { root.findViewById<ConstraintLayout>(R.id.progress_bar_layout) }
    val swipeRefreshLayout by lazy { root.findViewById<androidx.swiperefreshlayout.widget.SwipeRefreshLayout>(R.id.swipelayout) }
    val nestedScrollView by lazy { root.findViewById<ScrollView>(R.id.nested_scroll_view) }

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

    /*
    Function shows the no internet layout
    */
    override fun showNoInternetLayout() {
        layoutNoInternet.visibility = View.VISIBLE
    }

    /*
    Function hides the no internet layout
    */
    override fun hideNoInternetLayout() {
        layoutNoInternet.visibility = View.GONE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { //set all of the saved data from the onCreate attachview
        super.onViewCreated(view, savedInstanceState)

        if(Utils.isDarkTheme()){ //is is dark theme
            imageNoInternet.setImageResource(R.drawable.no_internet_white)
        }

        swipeRefreshLayout.setOnRefreshListener(this) //set onrefresh

        textTryAgain.setOnClickListener(this) //set onclick
        rank.setOnClickListener(this) //set onclick
        name.setOnClickListener(this) //set onclick
        price.setOnClickListener(this) //set onclick
        change.setOnClickListener(this) //set onclick

        text1H.setOnClickListener(this) //set onclick
        text1D.setOnClickListener(this) //set onclick
        text1W.setOnClickListener(this) //set onclick

        presenter = MarketsPresenter(MarketsDataManager.getInstance(context!!), this)
        presenter.attachView()
    }

    override fun onClick(v: View?) { //run onclick functions
        when (v?.id) {
            rank.id -> {
                if (filter == FILTER_RANK_DOWN)
                    filter = FILTER_RANK_UP
                else
                    filter = FILTER_RANK_DOWN

                changeSortText() //change sort text
                notifyFilterChanged() //notify changed
            }
            name.id -> {
                if (filter == FILTER_NAME_DOWN)
                    filter = FILTER_NAME_UP
                else
                    filter = FILTER_NAME_DOWN

                changeSortText() //change sort text
                notifyFilterChanged() //notify changed
            }
            price.id -> {
                if (filter == FILTER_PRICE_DOWN)
                    filter = FILTER_PRICE_UP
                else
                    filter = FILTER_PRICE_DOWN

                changeSortText() //change sort text
                notifyFilterChanged() //notify changed
            }
            change.id -> {
                if (filter == FILTER_CHANGE_DOWN)
                    filter = FILTER_CHANGE_UP
                else
                    filter = FILTER_CHANGE_DOWN
                changeSortText() //change sort text
                notifyFilterChanged() //notify changed
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
            textTryAgain.id -> {
                presenter.getOnlineData()
            }
        }
    }

    /*
    Function changes the time frame text
    */
    private fun changeTimeFrameText() {
        setTextColorSecondary(text1H) //reset time frame
        setTextColorSecondary(text1D) //reset time frame
        setTextColorSecondary(text1W) //reset time frame

        when (timeframe) {
            TIMEFRAME_1H -> {
                setTextColorPrimary(text1H)
            }
            TIMEFRAME_1D -> {
                setTextColorPrimary(text1D)
            }
            TIMEFRAME_1W -> {
                setTextColorPrimary(text1W)
            }
        }
    }

    /*
    Function sets the text secondary color for the theme
    */
    fun setTextColorSecondary(textView: TextView) {
        if(Utils.isDarkTheme()){
            textView.setTextColor(resources.getColor(R.color.dark_text_color_secondary))
        } else {
            textView.setTextColor(resources.getColor(R.color.light_text_color_secondary))
        }
    }

    /*
    Function sets the text primary color for the theme
    */
    fun setTextColorPrimary(textView: TextView) {
        if(Utils.isDarkTheme()){
            textView.setTextColor(resources.getColor(R.color.dark_text_color))
        } else {
            textView.setTextColor(resources.getColor(R.color.light_text_color))
        }
    }

    /*
    Function notifies that the time frame has been changed
    */
    private fun notifyTimeFrameChanged() {
        //change the percentage that shows
        if (mLayoutManager != null) {
            currenciesAdapter = CurrenciesAdapter(currenciesAdapter.currencies, currenciesAdapter.baseFiat, context, timeframe)
            recyclerViewCurrencies.adapter = currenciesAdapter
            currenciesAdapter.sort(filter)
        }
    }

    /*
    Function changes the sort texts for when a new one is clicked
    */
    private fun changeSortText() {
        rank.text = "#"
        name.text = "Name"
        price.text = "Price"
        change.text = "Change"

        setTextColorSecondary(rank)
        setTextColorSecondary(name)
        setTextColorSecondary(price)
        setTextColorSecondary(change)

        when (filter) {
            FILTER_RANK_DOWN -> {
                setTextColorPrimary(rank)
                rank.text = "#▼"
            }
            FILTER_RANK_UP -> {
                setTextColorPrimary(rank)
                rank.text = "#▲"
            }
            FILTER_NAME_DOWN -> {
                setTextColorPrimary(name)
                name.text = "Name▼"
            }
            FILTER_NAME_UP -> {
                setTextColorPrimary(name)
                name.text = "Name▲"
            }
            FILTER_PRICE_DOWN -> {
                setTextColorPrimary(price)
                price.text = "▼Price"
            }
            FILTER_PRICE_UP -> {
                setTextColorPrimary(price)
                price.text = "▲Price"
            }
            FILTER_CHANGE_DOWN -> {
                setTextColorPrimary(change)
                change.text = "▼Change"
            }
            FILTER_CHANGE_UP -> {
                setTextColorPrimary(change)
                change.text = "▲Change"
            }
        }
    }

    /*
    Function notifies when the filter has been changed
    */
    private fun notifyFilterChanged() {
        if (mLayoutManager != null)
            currenciesAdapter.sort(filter)
    }

    /*
    Function shows the market data
    */
    @SuppressLint("SetTextI18n")
    override fun showMarketData(marketData: Market, baseFiat: Rate) {

        textMarketCap.text = "${Utils.getFiatSymbol(baseFiat.fiat)}${marketData.data?.quote?.uSD?.totalMarketCap?.let { baseFiat.rate?.let { it1 -> truncateNumber(it*it1)  }}}"

        textVolume.text = "${Utils.getFiatSymbol(baseFiat.fiat)}${marketData.data?.quote?.uSD?.totalVolume24h?.let { truncateNumber(it) }}"

        textBTCDominance.text = Utils.formatPercentage(marketData.data?.btcDominance?.toBigDecimal()).substring(1)
    }

    var mLayoutManager: androidx.recyclerview.widget.LinearLayoutManager? = null


    /*
    Function returns the number of currencies in the adapter or 100
    */
    override fun getCurrenciesAdapterCount(): Int {
        if (mLayoutManager == null)
            return 100
        else
            return currenciesAdapter.itemCount
    }

    /*
    Function returns the current filter
    */
    override fun getSort(): String {
        return filter
    }

    /*
    Function shows the top 100 changes
    */
    @RequiresApi(Build.VERSION_CODES.M)
    override fun showTop100Changes(currencies: ArrayList<Currency>, baseFiat: Rate, resultsCount: Int) {

        isLoading = false

        if (mLayoutManager == null) {

            mLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            recyclerViewCurrencies.layoutManager = mLayoutManager
            currenciesAdapter = CurrenciesAdapter(ArrayList(currencies), baseFiat, context, timeframe)
            recyclerViewCurrencies.adapter = currenciesAdapter

            nestedScrollView.isFocusableInTouchMode = true
            nestedScrollView.descendantFocusability = ViewGroup.FOCUS_BEFORE_DESCENDANTS

            nestedScrollView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                val view = nestedScrollView.getChildAt(nestedScrollView.childCount - 1) as View
                val diff = view.bottom - (nestedScrollView.height + nestedScrollView.scrollY)


                if ((diff < 10) && !isLoading) {
                    isLoading = true
                    if (resultsCount > currenciesAdapter.itemCount) {
                        presenter.loadMoreItems(currenciesAdapter.currencies, resultsCount - currenciesAdapter.itemCount, searchView.query.trim())
                        progressBar.visibility = View.VISIBLE
                    } else {
                        progressBar.visibility = View.GONE
                    }
                }
            }

        } else {

            currenciesAdapter.swap(ArrayList(currencies), baseFiat)

            if (currencies.size < 100 || resultsCount <= currenciesAdapter.currencies.size) {

                progressBar.visibility = View.GONE

                nestedScrollView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->

                }

            } else {

                nestedScrollView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                    val view = nestedScrollView.getChildAt(nestedScrollView.childCount - 1) as View
                    val diff = view.bottom - (nestedScrollView.height + nestedScrollView.scrollY)


                    if ((diff < 10) && !isLoading) {
                        isLoading = true
                        if (resultsCount > currenciesAdapter.itemCount) {
                            presenter.loadMoreItems(currenciesAdapter.currencies, resultsCount - currenciesAdapter.itemCount, searchView.query.trim())
                            progressBar.visibility = View.VISIBLE
                        } else {
                            progressBar.visibility = View.GONE
                        }
                    }
                }

            }
        }
        currenciesAdapter.sort(filter)
    }

    var layoutManager: androidx.recyclerview.widget.LinearLayoutManager? = null

    /*
    Function executes when the list is reached the bottom
    */
    override fun onRefresh() {
        presenter.loadMoreItems(null, presenter.getResultsCounter() - currenciesAdapter.itemCount, searchView.query.trim())
    }

    /*
    Function stops the refreshing
    */
    override fun stopRefreshing() {
        swipeRefreshLayout.isRefreshing = false
    }

    /*
    Function executes when the fragment resumes
    */
    override fun onResume() {
        super.onResume()
        if(mLayoutManager != null)
            presenter.onResume()
    }

    /*
    Function handles when the tab button is clicked to scroll up to the top and stop current scrolling motion
    */
    override fun onTabClicked(isTabAlreadyClicked: Boolean) {
        if (isTabAlreadyClicked) {
            nestedScrollView.scrollTo(0, 0)
            nestedScrollView.fling(0)
        } else {
            if(layoutNoInternet.visibility == View.VISIBLE && Utils.isNetworkConnected(context!!))
                presenter.getOnlineData()
        }
        //scroll to top
//        else
//            presenter.refresh()
    }

    /*
    Function hides the progress bar layout
    */
    override fun hideProgressBarLayout() {
        progressBarLayout.visibility = View.GONE
        swipeRefreshLayout.isRefreshing = false
    }

    val MILLION = 1000000L.toBigDecimal()
    val BILLION = 1000000000L.toBigDecimal()
    val TRILLION = 1000000000000.toBigDecimal()

    /*
    Function truncates a number
    */
    fun truncateNumber(x: BigDecimal): String {
        return if (x < MILLION)
            x.toString()
        else if (x < BILLION)
            String.format("%.2f", (x / MILLION).toString()) + " M"
        else if (x < TRILLION)
            String.format("%.2f", x / BILLION) + " B"
        else
            String.format("%.2f", x / TRILLION) + " T"
    }

    /*
    Function shows the swipe refresh layout
    */
    override fun showContentLayout() {
        swipeRefreshLayout.visibility = View.VISIBLE
    }

    /*
    Function shows the progress bar layout
    */
    override fun showProgressBarLayout() {
        progressBarLayout.visibility = View.VISIBLE
    }

    /*
    Function hides the swipe refresh layout
    */
    override fun hideContentLayout() {
        swipeRefreshLayout.visibility = View.GONE
    }

    /*
    Function returns the searchview for the listener
    */
    override fun getCurrencySearchView(): SearchView {
        return searchView
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