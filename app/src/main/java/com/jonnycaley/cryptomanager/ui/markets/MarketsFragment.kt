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
import com.reginald.swiperefresh.CustomSwipeRefreshLayout
import android.R.attr.duration
import android.os.Handler
import android.os.Looper
import android.support.v7.widget.LinearSmoothScroller
import android.util.DisplayMetrics


class MarketsFragment : Fragment(), MarketsContract.View, TabInterface, SwipeRefreshLayout.OnRefreshListener {

    lateinit var root : View

    private lateinit var presenter : MarketsContract.Presenter

    lateinit var currenciesAdapter : CurrenciesAdapter

    lateinit var similarArticlesAdapter : ArticlesHorizontalAdapter

    val recyclerViewCurrencies by lazy { root.findViewById<RecyclerView>(R.id.recycler_view_currencies) }
    val recyclerViewLatestNews by lazy { root.findViewById<RecyclerView>(R.id.recycler_view_latest_news) }

    val searchView : SearchView by lazy { root.findViewById<SearchView>(R.id.search_view_currencies) }

    val progressBarLayout by lazy { root.findViewById<ConstraintLayout>(R.id.progress_bar_layout) }

    val scrollLayout : SwipeRefreshLayout by lazy { root.findViewById<SwipeRefreshLayout>(R.id.swipelayout) }

    override fun setPresenter(presenter: MarketsContract.Presenter) {
        this.presenter = checkNotNull(presenter)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.fragment_markets, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { //set all of the saved data from the onCreate attachview
        super.onViewCreated(view, savedInstanceState)

        scrollLayout.setOnRefreshListener(this)

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

    override fun onRefresh() {
        presenter.onResume()
    }

    override fun onResume() {
        super.onResume()
        println("onResume()")
        presenter.onResume()
    }

    override fun onTabClicked() {
        Log.i(TAG, "onTabClicked()")
        presenter.refresh()
    }

    override fun hideProgressBarLayout() {
        progressBarLayout.visibility = View.GONE
        scrollLayout.isRefreshing = false
    }

    override fun showContentLayout() {
        scrollLayout.visibility = View.VISIBLE
    }

    override fun showProgressBarLayout() {
        progressBarLayout.visibility = View.VISIBLE
    }

    override fun hideContentLayout() {
        scrollLayout.visibility = View.GONE
    }

    override fun getCurrencySearchView(): SearchView {
        return searchView
    }

    override fun showTop100Changes(currencies: List<Currency>?, baseFiat : Rate) {

        val arrayList = ArrayList<Currency>()

        currencies?.forEach { arrayList.add(it) }

        val mLayoutManager = LinearLayoutManager(context)
        recyclerViewCurrencies.layoutManager = mLayoutManager
        currenciesAdapter = CurrenciesAdapter(arrayList, baseFiat,  context)
        recyclerViewCurrencies.adapter = currenciesAdapter
    }


    var layoutManager: LinearLayoutManager? = null

    override fun showLatestArticles(latestArticles: ArrayList<Article>, savedArticles: ArrayList<Article>) {

        if(layoutManager == null) {

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