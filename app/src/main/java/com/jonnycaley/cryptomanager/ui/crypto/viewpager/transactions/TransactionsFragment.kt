package com.jonnycaley.cryptomanager.ui.crypto.viewpager.transactions

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
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.AllCurrencies.Datum
import com.jonnycaley.cryptomanager.data.model.DataBase.NotTransaction
import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.Rate
import com.jonnycaley.cryptomanager.ui.transactions.crypto.CryptoTransactionArgs

class TransactionsFragment : Fragment(), TransactionsContract.View, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private lateinit var presenter : TransactionsContract.Presenter

    private var currencySymbol = ""

    lateinit var transactionsAdapter : TransactionsAdapter

    lateinit var mView : View

    val buttonAddTransaction by lazy { mView.findViewById<Button>(R.id.button_add_transaction) }
    val recyclerView by lazy { mView.findViewById<RecyclerView>(R.id.recycler_view) }

    val swipeLayout by lazy { mView.findViewById<SwipeRefreshLayout>(R.id.swipelayout) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currencySymbol = it.getSerializable(ARG_PARAM1) as String
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_transactions, container, false)
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonAddTransaction.setOnClickListener(this)
        swipeLayout.setOnRefreshListener(this)
        
        presenter = TransactionsPresenter(TransactionsDataManager.getInstance(context!!), this)
        presenter.attachView()
    }

    override fun onRefresh() {
        presenter.onResume()
    }

    override fun hideRefreshing() {
        swipeLayout.isRefreshing = false
    }

    override fun onClick(v: View?) {
        when(v?.id){
            buttonAddTransaction.id -> {
                presenter.getAllCurrencies()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume() //notice how i don't call the presenter.getCryptoPrices() here as it is quite unnecessary to load a new set of prices when this would only run when coming from a transaction detail page which is more than likely not long after the prices are obtained in the first place therefore not worth the loading time wait again for update
        Log.i(TAG, "onResume()")
    }

    override fun startTransaction(currency: Datum?, baseImageUrl: String?, baseLinkUrl: String?) {

//        Log.i("ImageUrl", currency?.imageUrl)
//        Log.i("ImageUrl", baseLinkUrl)
//
//        currency.imageUrl

        val notTransaction= currency?.let { NotTransaction(it, currency.imageUrl, baseImageUrl, false) }

        CryptoTransactionArgs(null, notTransaction, true).launch(context!!)
    }

    override fun loadTransactions(transactions: List<Transaction>, currentUsdPrice: Double?, baseFiat: Rate) {

        val mLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = mLayoutManager
        transactionsAdapter = TransactionsAdapter(transactions.sortedBy { it.date }.asReversed(), currencySymbol, currentUsdPrice?.toBigDecimal(), baseFiat, context)
        recyclerView.adapter = transactionsAdapter
    }

    override fun getSymbol(): String {
        return currencySymbol
    }

    override fun setPresenter(presenter: TransactionsContract.Presenter) {
        this.presenter = checkNotNull(presenter)
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String) =
                TransactionsFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable(ARG_PARAM1, param1)
                    }
                }

        private const val ARG_PARAM1 = "symbol"
        private const val TAG = "TransactionsFragment"
    }
}
