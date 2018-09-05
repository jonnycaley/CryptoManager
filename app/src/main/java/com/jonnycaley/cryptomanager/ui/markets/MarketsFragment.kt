package com.jonnycaley.cryptomanager.ui.portfolio

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Currency
import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.News
import com.jonnycaley.cryptomanager.ui.markets.ArticlesHorizontalAdapter
import com.jonnycaley.cryptomanager.ui.markets.CurrenciesAdapter
import com.jonnycaley.cryptomanager.ui.markets.MarketsContract
import com.jonnycaley.cryptomanager.ui.markets.MarketsDataManager
import com.jonnycaley.cryptomanager.ui.markets.MarketsPresenter
import com.reginald.swiperefresh.CustomSwipeRefreshLayout


class MarketsFragment : Fragment(), MarketsContract.View{

    lateinit var root : View

    private lateinit var presenter : MarketsContract.Presenter

    lateinit var currenciesAdapter : CurrenciesAdapter

    lateinit var similarArticlesAdapter : ArticlesHorizontalAdapter

    val recyclerViewCurrencies by lazy { root.findViewById<RecyclerView>(R.id.recycler_view_currencies) }
    val recyclerViewLatestNews by lazy { root.findViewById<RecyclerView>(R.id.recycler_view_latest_news) }

    val searchView : SearchView by lazy { root.findViewById<SearchView>(R.id.search_view_currencies) }

    val swipeRefreshLayout : CustomSwipeRefreshLayout by lazy { root.findViewById<CustomSwipeRefreshLayout>(R.id.swipelayout) }

    override fun setPresenter(presenter: MarketsContract.Presenter) {
        this.presenter = checkNotNull(presenter)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.fragment_markets, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { //set all of the saved data from the onCreate attachview
        super.onViewCreated(view, savedInstanceState)

        swipeRefreshLayout.setOnRefreshListener {
            Toast.makeText(context, "Refreshing", Toast.LENGTH_SHORT).show()
        }

        presenter = MarketsPresenter(MarketsDataManager.getInstance(context!!), this)
        presenter.attachView()
    }

    override fun getCurrencySearchView(): SearchView {
        return searchView
    }

    override fun showTop100Changes(currencies: List<Currency>?) {

        val arrayList = ArrayList<Currency>()

        currencies?.forEach { arrayList.add(it) }

        val mLayoutManager = LinearLayoutManager(context)
        recyclerViewCurrencies.layoutManager = mLayoutManager
        currenciesAdapter = CurrenciesAdapter(arrayList, context)
        recyclerViewCurrencies.adapter = currenciesAdapter
    }

    override fun showLatestArticles(news: Array<News>) {

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        recyclerViewLatestNews.layoutManager = layoutManager
        similarArticlesAdapter = ArticlesHorizontalAdapter(news, context)
        recyclerViewLatestNews.adapter = similarArticlesAdapter
    }
}