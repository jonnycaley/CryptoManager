package com.jonnycaley.cryptomanager.ui.crypto.viewpager.transactions

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction

class TransactionsFragment : Fragment(), TransactionsContract.View {

    private lateinit var presenter : TransactionsContract.Presenter

    private var currencySymbol: String? = null

    lateinit var transactionsAdapter : TransactionsAdapter

    lateinit var mView : View

    val recyclerView by lazy { mView.findViewById<RecyclerView>(R.id.recycler_view) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currencySymbol = it.getSerializable(ARG_PARAM1) as String
        }
    }

    override fun getSymbol(): String? {
        return currencySymbol
    }

    override fun loadTransactions(transactions: List<Transaction>) {

        val mLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = mLayoutManager
        transactionsAdapter = TransactionsAdapter(transactions, context)
        recyclerView.adapter = transactionsAdapter
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_transactions, container, false)
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = TransactionsPresenter(TransactionsDataManager.getInstance(context!!), this)
        presenter.attachView()
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
    }
}
