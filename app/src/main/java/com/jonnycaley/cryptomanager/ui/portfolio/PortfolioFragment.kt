package com.jonnycaley.cryptomanager.ui.portfolio

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.MultiPrice.MultiPrices
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.MultiPrice.Price
import com.jonnycaley.cryptomanager.data.model.DataBase.Holding
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.ExchangeRates
import com.jonnycaley.cryptomanager.ui.search.SearchArgs
import com.jonnycaley.cryptomanager.utils.Utils
import com.reginald.swiperefresh.CustomSwipeRefreshLayout

class PortfolioFragment : Fragment(), PortfolioContract.View, View.OnClickListener, CustomSwipeRefreshLayout.OnRefreshListener {

    lateinit var mView: View

    private lateinit var presenter: PortfolioContract.Presenter

    lateinit var holdingsAdapter : HoldingsAdapter

    val swipeLayout by lazy { mView.findViewById<CustomSwipeRefreshLayout>(R.id.swipelayout) }

    val buttonAddCurrency by lazy { mView.findViewById<Button>(R.id.button_add_currency) }
    val buttonAddFiat by lazy { mView.findViewById<Button>(R.id.button_add_fiat) }

    val layoutEmpty by lazy { mView.findViewById<LinearLayout>(R.id.layout_portfolio_empty) }
    val layoutNotEmty by lazy { mView.findViewById<LinearLayout>(R.id.layout_portfolio_not_empty) }

    val recyclerView by lazy { mView.findViewById<RecyclerView>(R.id.recycler_view) }

    val textBalance by lazy { mView.findViewById<TextView>(R.id.text_balance) }
    val textChange by lazy { mView.findViewById<TextView>(R.id.text_change) }

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

        presenter = PortfolioPresenter(PortfolioDataManager.getInstance(context!!), this)
        presenter.attachView()
    }

    override fun onClick(v: View?) {
        when(v?.id){
            buttonAddFiat.id -> {
                SearchArgs(FIAT_STRING).launch(context!!)
            }
            buttonAddCurrency.id -> {
                SearchArgs(CURRENCY_STRING).launch(context!!)
            }
        }
    }

    override fun hideRefreshing() {
        swipeLayout.refreshComplete()
    }

    override fun onRefresh() {
        presenter.getTransactions()
    }

    override fun onResume() {
        super.onResume()
        presenter.getTransactions()
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

    override fun showBalance(balance: Double) {
        textBalance.text = "$${Utils.formatPrice(balance)}"
    }

    override fun showChange(change: Double) {

        if (change < 0) {
            context?.resources?.getColor(R.color.red)?.let { textChange.setTextColor(it) }
            textChange.text = "-$${Utils.formatPrice(change).substring(1)}"
        } else {
            context?.resources?.getColor(R.color.green)?.let { textChange.setTextColor(it) }
            textChange.text = "$${Utils.formatPrice(change)}"
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

    override fun showHoldings(holdings: ArrayList<Holding>, prices: ArrayList<Price>) {

        val mLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = mLayoutManager
        holdingsAdapter = HoldingsAdapter(holdings, prices,  context)
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
        val FIAT_STRING = "FIAT"
        val CURRENCY_STRING = "CURRENCY"
    }
}