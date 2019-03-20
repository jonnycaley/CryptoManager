package com.jonnycaley.cryptomanager.ui.settings.transactionHistory

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import com.jonnycaley.cryptomanager.utils.Utils
import kotlinx.android.synthetic.main.activity_transaction_history.*

class TransactionHistoryActivity : AppCompatActivity(), TransactionHistoryContract.View {

    private lateinit var presenter : TransactionHistoryContract.Presenter

    lateinit var transactionsAdapter : TransactionsAdapter

    val recyclerView by lazy { findViewById<RecyclerView>(R.id.recycler_view) }

    val layoutNoTransactions by lazy { findViewById<LinearLayout>(R.id.layout_no_transactions) }

    override fun onCreate(savedInstanceState: Bundle?) {

        if(Utils.isDarkTheme()) {
            setTheme(R.style.darktheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_history)

        if(!Utils.isDarkTheme()) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        }

        setupToolbar()

        presenter = TransactionHistoryPresenter(TransactionHistoryDataManager.getInstance(this), this)
        presenter.attachView()
    }

    override fun showTransactions(transactions: ArrayList<Transaction>) {

        val mLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = mLayoutManager
        transactionsAdapter = TransactionsAdapter(transactions.sortedBy { it.date }.asReversed(), this)
        recyclerView.adapter = transactionsAdapter
    }

    override fun hideNoTransactionsLayout() {
        layoutNoTransactions.visibility = View.GONE
    }

    override fun showNoTransactionsLayout() {
        layoutNoTransactions.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        presenter.getTransactions()
    }

    override fun setPresenter(presenter: TransactionHistoryContract.Presenter) {
        this.presenter = checkNotNull(presenter)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                super.onBackPressed()
                return false
            }
        }
        return false
    }
}