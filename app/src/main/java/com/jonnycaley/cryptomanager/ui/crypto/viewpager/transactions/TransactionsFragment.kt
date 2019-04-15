package com.jonnycaley.cryptomanager.ui.crypto.viewpager.transactions

import android.os.Bundle
import androidx.core.widget.NestedScrollView
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

class TransactionsFragment : androidx.fragment.app.Fragment(), TransactionsContract.View, View.OnClickListener, androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener {

    private lateinit var presenter : TransactionsContract.Presenter

    private var currencySymbol = ""

    lateinit var transactionsAdapter : TransactionsAdapter

    lateinit var mView : View

    val buttonAddTransaction by lazy { mView.findViewById<Button>(R.id.button_add_transaction) }
    val recyclerView by lazy { mView.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recycler_view) }

    val swipeLayout by lazy { mView.findViewById<androidx.swiperefreshlayout.widget.SwipeRefreshLayout>(R.id.swipelayout) }

    val nestedScrollView by lazy { mView.findViewById<NestedScrollView>(R.id.nested_scroll_view) }

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

    /*
    Function executes when the refresh layout is refreshed
    */
    override fun onRefresh() {
        presenter.onResume()
    }

    /*
    Function hides the refreshing icon
    */
    override fun hideRefreshing() {
        swipeLayout.isRefreshing = false
    }

    override fun onClick(v: View?) {
        when(v?.id){
            buttonAddTransaction.id -> {
                presenter.startTransaction()
            }
        }
    }

    /*
    Function executes when the fragment resumes
    */
    override fun onResume() {
        super.onResume()
        presenter.onResume() //notice how i don't call the presenter.getCryptoPrices() here as it is quite unnecessary to load a new set of prices when this would only run when coming from a transaction detail page which is more than likely not long after the prices are obtained in the first place therefore not worth the loading time wait again for update
    }

    /*
    Function starts the crypto transaction activity
    */
    override fun startTransaction(currency: Datum?, baseImageUrl: String?, baseLinkUrl: String?) {

        val notTransaction = currency?.let { NotTransaction(it, currency.imageUrl, baseImageUrl, false) }
        CryptoTransactionArgs(null, notTransaction, false).launch(context!!)
    }

    /*
    Function loads the crypto transactions into the list
    */
    override fun loadTransactions(transactions: List<Transaction>, currentUsdPrice: Double?, baseFiat: Rate) {

        val mLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        recyclerView.layoutManager = mLayoutManager
        transactionsAdapter = TransactionsAdapter(transactions.sortedBy { it.date }.asReversed(), currencySymbol, currentUsdPrice?.toBigDecimal(), baseFiat, context)
        recyclerView.adapter = transactionsAdapter
    }

    /*
    Function returns the crypto symbol
    */
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
