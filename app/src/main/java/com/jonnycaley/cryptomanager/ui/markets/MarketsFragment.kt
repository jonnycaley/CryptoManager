package com.jonnycaley.cryptomanager.ui.markets

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Currency
import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.Article
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.Rate
import com.jonnycaley.cryptomanager.utils.interfaces.TabInterface
import android.R.attr.duration
import android.os.Handler
import android.os.Looper
import android.support.v4.widget.NestedScrollView
import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Market.Market
import com.jonnycaley.cryptomanager.utils.Utils
import com.jonnycaley.cryptomanager.utils.interfaces.PaginationScrollListener
import java.math.BigDecimal


class MarketsFragment : Fragment(), MarketsContract.View, TabInterface, SwipeRefreshLayout.OnRefreshListener {

    lateinit var root: View

    private lateinit var presenter: MarketsContract.Presenter

    lateinit var currenciesAdapter: CurrenciesAdapter

    lateinit var similarArticlesAdapter: ArticlesHorizontalAdapter

    val recyclerViewCurrencies by lazy { root.findViewById<RecyclerView>(R.id.recycler_view_currencies) }
    val recyclerViewLatestNews by lazy { root.findViewById<RecyclerView>(R.id.recycler_view_latest_news) }

    val searchView by lazy { root.findViewById<SearchView>(R.id.search_view_currencies) }

    val progressBar by lazy { root.findViewById<ProgressBar>(R.id.progress_bar) }

    val progressBarLayout by lazy { root.findViewById<ConstraintLayout>(R.id.progress_bar_layout) }

    val swipeRefreshLayout by lazy { root.findViewById<SwipeRefreshLayout>(R.id.swipelayout) }

    val nestedScrollView by lazy { root.findViewById<NestedScrollView>(R.id.nested_scroll_view) }

    val textMarketCap by lazy { root.findViewById<TextView>(R.id.text_market_cap) }
    val textVolume by lazy { root.findViewById<TextView>(R.id.text_volume) }
    val textBTCDominance by lazy { root.findViewById<TextView>(R.id.text_btc_dominance) }

    val textMarketCapPercentage by lazy { root.findViewById<TextView>(R.id.text_market_cap_percentage) }
    val textVolumePercentage by lazy { root.findViewById<TextView>(R.id.text_volume_percentage) }
    val textBTCDominancePercentage by lazy { root.findViewById<TextView>(R.id.text_btc_dominance_percentage) }

    override fun setPresenter(presenter: MarketsContract.Presenter) {
        this.presenter = checkNotNull(presenter)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.fragment_markets, container, false)
        return root
    }

    var isLastPage = false
    var isLoading = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { //set all of the saved data from the onCreate attachview
        super.onViewCreated(view, savedInstanceState)

        swipeRefreshLayout.setOnRefreshListener(this)

        presenter = MarketsPresenter(MarketsDataManager.getInstance(context!!), this)
        presenter.attachView()
    }

    private val mHandler = Handler(Looper.getMainLooper())
    val pixelsToMove = 30

    private val SCROLLING_RUNNABLE = object : Runnable {

        override fun run() {
            recyclerViewLatestNews.smoothScrollBy(pixelsToMove, 0)
            mHandler.postDelayed(this, duration.toLong())
        }
    }

    override fun showMarketData(marketData: Market?) {

        textMarketCap.text = "$${truncateNumber(marketData?.data?.quote?.uSD?.totalMarketCap!!)}"

        textVolume.text = "$${truncateNumber(marketData.data?.quote?.uSD?.totalVolume24h!!)}"

        textBTCDominance.text = Utils.formatPercentage(marketData.data?.btcDominance!!.toBigDecimal())

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

    var mLayoutManager : LinearLayoutManager? = null

    override fun showTop100Changes(currencies: List<Currency>?, baseFiat: Rate) {

//        val arrayList = ArrayList<Currency>()
//
//        currencies?.forEach { arrayList.add(it) }

        if(mLayoutManager == null) {

            mLayoutManager = LinearLayoutManager(context)
            recyclerViewCurrencies.layoutManager = mLayoutManager
            currenciesAdapter = CurrenciesAdapter(ArrayList(currencies), baseFiat, context)
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
                    presenter.loadMoreItems(currenciesAdapter.currencies)
                }
            })
        } else {

            currenciesAdapter.swap(currencies as ArrayList<Currency>?, baseFiat)

        }
    }


    var layoutManager: LinearLayoutManager? = null

    override fun showLatestArticles(latestArticles: ArrayList<Article>, savedArticles: ArrayList<Article>) {

        if (layoutManager == null) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            recyclerViewLatestNews.layoutManager = object : LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false) {
            }
            similarArticlesAdapter = ArticlesHorizontalAdapter(latestArticles, savedArticles, context, presenter)
            recyclerViewLatestNews.adapter = similarArticlesAdapter
        } else {
            similarArticlesAdapter.latestArticles = latestArticles
            similarArticlesAdapter.savedArticles = savedArticles
            similarArticlesAdapter.notifyDataSetChanged()
        }
    }


    override fun onRefresh() {
        presenter.loadMoreItems(null)
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }
    override fun onTabClicked() {
        presenter.refresh()
    }

    override fun hideProgressBarLayout() {
        progressBarLayout.visibility = View.GONE
        swipeRefreshLayout.isRefreshing = false
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
    }
}