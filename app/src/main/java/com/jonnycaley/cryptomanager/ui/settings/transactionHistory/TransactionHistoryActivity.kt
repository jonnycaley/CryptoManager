package com.jonnycaley.cryptomanager.ui.settings.transactionHistory

import android.os.Bundle
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
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

    val recyclerView by lazy { findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recycler_view) }

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

        setupToolbar() //set up toolbar

        presenter = TransactionHistoryPresenter(TransactionHistoryDataManager.getInstance(this), this) //attach presenter
        presenter.attachView()
    }

    /*
    Function shows transactions list
    */
    override fun showTransactions(transactions: ArrayList<Transaction>) {

        val mLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        recyclerView.layoutManager = mLayoutManager
        transactionsAdapter = TransactionsAdapter(transactions.sortedBy { it.date }.asReversed(), this) //attach adapter
        recyclerView.adapter = transactionsAdapter
    }

    /*
    Function hides no transactions layout
    */
    override fun hideNoTransactionsLayout() {
        layoutNoTransactions.visibility = View.GONE //change visibility
    }

    /*
    Function shows no transactions layout
    */
    override fun showNoTransactionsLayout() {
        layoutNoTransactions.visibility = View.VISIBLE //change visibility
    }

    override fun onResume() {
        super.onResume()
        presenter.getTransactions()
    }

    override fun setPresenter(presenter: TransactionHistoryContract.Presenter) {
        this.presenter = checkNotNull(presenter)
    }

    /*
    Function sets up toolbar title
    */
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