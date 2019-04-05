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

    val headerGlobalData by lazy { root.findViewById<TextView>(R.id.header_global_data) }
    val headerAllCurrencies by lazy { root.findViewById<TextView>(R.id.header_currencies) }

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

    override fun showNoInternetLayout() {
        layoutNoInternet.visibility = View.VISIBLE
    }

    override fun hideNoInternetLayout() {
        layoutNoInternet.visibility = View.GONE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { //set all of the saved data from the onCreate attachview
        super.onViewCreated(view, savedInstanceState)

        if(Utils.isDarkTheme()){
            imageNoInternet.setImageResource(R.drawable.no_internet_white)
        }

        swipeRefreshLayout.setOnRefreshListener(this)

//        val custom_font = Typeface.createFromAsset(context?.applicationContext?.assets, "fonts/Roboto-Bold.ttf")

//        headerAllCurrencies.typeface = custom_font
//        headerGlobalData.typeface = custom_font

        textTryAgain.setOnClickListener(this)
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
        when (v?.id) {
            rank.id -> {
                if (filter == FILTER_RANK_DOWN)
                    filter = FILTER_RANK_UP
                else
                    filter = FILTER_RANK_DOWN

                changeSortText()
                notifyFilterChanged()
            }
            name.id -> {
                if (filter == FILTER_NAME_DOWN)
                    filter = FILTER_NAME_UP
                else
                    filter = FILTER_NAME_DOWN

                changeSortText()
                notifyFilterChanged()
            }
            price.id -> {
                if (filter == FILTER_PRICE_DOWN)
                    filter = FILTER_PRICE_UP
                else
                    filter = FILTER_PRICE_DOWN

                changeSortText()
                notifyFilterChanged()
            }
            change.id -> {
                if (filter == FILTER_CHANGE_DOWN)
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
            textTryAgain.id -> {
                presenter.getOnlineData()
            }
        }
    }

    private fun changeTimeFrameText() {

//        text1H.setTypeface(null, Typeface.NORMAL)
//        text1D.setTypeface(null, Typeface.NORMAL)
//        text1W.setTypeface(null, Typeface.NORMAL)

        setTextColorSecondary(text1H)
        setTextColorSecondary(text1D)
        setTextColorSecondary(text1W)

        when (timeframe) {
            TIMEFRAME_1H -> {
                setTextColorPrimary(text1H)
//                text1H.setTypeface(text1H.typeface, Typeface.BOLD)
            }
            TIMEFRAME_1D -> {
                setTextColorPrimary(text1D)
//                text1D.setTypeface(text1D.typeface, Typeface.BOLD)
            }
            TIMEFRAME_1W -> {
                setTextColorPrimary(text1W)
//                text1W.setTypeface(text1W.typeface, Typeface.BOLD)
            }
        }
    }

    fun setTextColorSecondary(textView: TextView) {
        if(Utils.isDarkTheme()){
            textView.setTextColor(resources.getColor(R.color.dark_text_color_secondary))
        } else {
            textView.setTextColor(resources.getColor(R.color.light_text_color_secondary))
        }
    }


    fun setTextColorPrimary(textView: TextView) {
        if(Utils.isDarkTheme()){
            textView.setTextColor(resources.getColor(R.color.dark_text_color))
        } else {
            textView.setTextColor(resources.getColor(R.color.light_text_color))
        }
    }

    private fun notifyTimeFrameChanged() {
        //change the percentage that shows
        if (mLayoutManager != null) {
            currenciesAdapter = CurrenciesAdapter(currenciesAdapter.currencies, currenciesAdapter.baseFiat, context, timeframe)
            recyclerViewCurrencies.adapter = currenciesAdapter
            currenciesAdapter.sort(filter)
        }
    }

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
//                rank.setTypeface(rank.typeface, Typeface.BOLD)
                rank.text = "#▼"
            }
            FILTER_RANK_UP -> {
                setTextColorPrimary(rank)
//                rank.setTypeface(rank.typeface, Typeface.BOLD)
                rank.text = "#▲"
            }
            FILTER_NAME_DOWN -> {
                setTextColorPrimary(name)
//                name.setTypeface(name.typeface, Typeface.BOLD)
                name.text = "Name▼"
            }
            FILTER_NAME_UP -> {
                setTextColorPrimary(name)
//                name.setTypeface(name.typeface, Typeface.BOLD)
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

    private fun notifyFilterChanged() {
        if (mLayoutManager != null)
            currenciesAdapter.sort(filter)
    }

    @SuppressLint("SetTextI18n")
    override fun showMarketData(marketData: Market, baseFiat: Rate) {

        //TODO: MARKET DATA COMES BACK NULL SOMETIMES WTF

        println("See here")

        println(marketData.data?.quote?.uSD?.totalMarketCap)
        println(marketData.data?.quote?.uSD?.totalVolume24h)
        println(marketData.data?.btcDominance)

        textMarketCap.text = "${Utils.getFiatSymbol(baseFiat.fiat)}${marketData.data?.quote?.uSD?.totalMarketCap?.let { baseFiat.rate?.let { it1 -> truncateNumber(it*it1)  }}}"

        textVolume.text = "${Utils.getFiatSymbol(baseFiat.fiat)}${marketData.data?.quote?.uSD?.totalVolume24h?.let { truncateNumber(it) }}"

        textBTCDominance.text = Utils.formatPercentage(marketData.data?.btcDominance?.toBigDecimal()).substring(1)

    }

    var mLayoutManager: androidx.recyclerview.widget.LinearLayoutManager? = null

    override fun getCurrenciesAdapterCount(): Int {
        if (mLayoutManager == null)
            return 100
        else
            return currenciesAdapter.itemCount
    }

    override fun getSort(): String {
        return filter
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun showTop100Changes(currencies: ArrayList<Currency>, baseFiat: Rate, resultsCount: Int) {

        isLoading = false

        if (mLayoutManager == null) {

            Log.i(TAG, currencies.size.toString() + " S E E  H E R E2")

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

            Log.i(TAG, currencies.size.toString() + " S E E  H E R E3")
            Log.i(TAG, resultsCount.toString())
            Log.i(TAG, currenciesAdapter.currencies.size.toString())

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


//    override fun showTop100Changes(currencies: List<Currency>?, baseRate: Rate, resultsCount: Int) {
//
//        isLoading = false
//
//        Log.i(TAG, currencies?.size.toString() + " S E E  H E R E1")
//        Log.i(TAG, "$resultsCount results")
//
//        if (mLayoutManager == null) {
//
//            Log.i(TAG, currencies?.size.toString() + " S E E  H E R E2")
//
//            mLayoutManager = LinearLayoutManager(context)
//            recyclerViewCurrencies.layoutManager = mLayoutManager
//            currenciesAdapter = CurrenciesAdapter(ArrayList(currencies), baseRate, context, timeframe)
//            recyclerViewCurrencies.adapter = currenciesAdapter
//
//            nestedScrollView.viewTreeObserver.addOnScrollChangedListener {
//
//                val view = nestedScrollView.getChildAt(nestedScrollView.childCount - 1) as View
//                val diff = view.bottom - (nestedScrollView.height + nestedScrollView.scrollY)
//
//                if ((diff == 0) && !isLoading) {
//
//
//
//                    isLoading = true
//                    if(resultsCount > currenciesAdapter.currencies?.size!!){
//                        presenter.loadMoreItems(currenciesAdapter.currencies, resultsCount - currenciesAdapter.currencies!!.size, searchView.query.trim())
//                        progressBar.visibility = View.VISIBLE
//                    } else {
//                        progressBar.visibility = View.GONE
//                    }
//
//                    println("1")
//                    println(resultsCount)
//                    println(currenciesAdapter.currencies?.size!!)
//                    isLoading = true
//                    if (resultsCount > currenciesAdapter.currencies?.size!!) {
//                        println("2")
//                        presenter.loadMoreItems(currenciesAdapter.currencies, resultsCount - currenciesAdapter.currencies!!.size, searchView.query.trim())
//                        progressBar.visibility = View.VISIBLE
//                    } else {
//                        println("3")
//                        progressBar.visibility = View.GONE
//                    }
//                }
//
//            }
//        } else {
//
//            Log.i(TAG, currencies?.size.toString() + " S E E  H E R E3")
//            Log.i(TAG, resultsCount.toString())
//            Log.i(TAG, currenciesAdapter.currencies?.size.toString()!!)
//
//            currenciesAdapter.swap(ArrayList(currencies), baseRate)
//
//            if (currencies?.size!! < 100 || resultsCount <= currenciesAdapter.currencies?.size!!) {
//                progressBar.visibility = View.GONE
//
//                nestedScrollView.viewTreeObserver.addOnScrollChangedListener {
//                    //                var scrollY = nestedScrollView.scrollY
//                }
//
//            } else {
//
//                nestedScrollView.viewTreeObserver.addOnScrollChangedListener {
//
//                    val view = nestedScrollView.getChildAt(nestedScrollView.childCount - 1) as View
//                    val diff = view.bottom - (nestedScrollView.height + nestedScrollView.scrollY)
//
//
//
//
//                    if ((diff == 0) && !isLoading) {
//                        isLoading = true
//                        if(resultsCount > currenciesAdapter.currencies?.size!!){
//                            presenter.loadMoreItems(currenciesAdapter.currencies, resultsCount - currenciesAdapter.currencies!!.size, searchView.query.trim())
//                            progressBar.visibility = View.VISIBLE
//                        } else {
//                            progressBar.visibility = View.GONE
//                        }
//                    }
//
//                }
//
//            }
//        }
//        currenciesAdapter.sort(filter)
//    }


    var layoutManager: androidx.recyclerview.widget.LinearLayoutManager? = null

    override fun onRefresh() {
        presenter.loadMoreItems(null, presenter.getResultsCounter() - currenciesAdapter.itemCount, searchView.query.trim())
    }

    override fun stopRefreshing() {
        swipeRefreshLayout.isRefreshing = false
    }

    override fun onResume() {
        super.onResume()
        if(mLayoutManager != null)
            presenter.onResume()
    }

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
            String.format("%.2f", x / BILLION) + " B"
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