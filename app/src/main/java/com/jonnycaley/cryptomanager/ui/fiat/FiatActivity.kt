package com.jonnycaley.cryptomanager.ui.fiat

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.TextView
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction

class FiatActivity : AppCompatActivity() , FiatContract.View{

    private lateinit var presenter : FiatContract.Presenter

    val args by lazy { FiatArgs.deserializeFrom(intent) }

    val recyclerView by lazy { findViewById<RecyclerView>(R.id.recycler_view) }

    val textAvailable by lazy { findViewById<TextView>(R.id.text_available) }
    val textDeposited by lazy { findViewById<TextView>(R.id.text_total_deposited) }
    val textWithdrawn by lazy { findViewById<TextView>(R.id.text_total_withdrawn) }

    lateinit var holdingsAdapter : TransactionsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fiat)

        setupToolbar()

        presenter = FiatPresenter(FiatDataManager.getInstance(this), this)
        presenter.attachView()
    }

    override fun showProgressBar() {

    }

    override fun showAvailableFiat(fiatSymbol: String, availableFiatCount: Long) {
        textAvailable.text = "Available: $fiatSymbol$availableFiatCount"
    }

    override fun showDepositedFiat(fiatSymbol: String, depositedFiatCount: Long) {
        textDeposited.text = "$fiatSymbol$depositedFiatCount"
    }

    override fun showWithdrawnFiat(fiatSymbol: String, withdrawnFiatCount: Long) {
        textWithdrawn.text = "$fiatSymbol${withdrawnFiatCount*-1}"
    }


    override fun onResume() {
        presenter.getTransactions(args.fiat)
        super.onResume()
    }

    override fun showTransactions(fiatSymbol: String, transactions: List<Transaction>) {

        val mLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = mLayoutManager
        holdingsAdapter = TransactionsAdapter(fiatSymbol, transactions, this, this)
        recyclerView.adapter = holdingsAdapter

    }

    override fun getFiatCode(): String {
        return args.fiat
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = args.fiat
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

    override fun setPresenter(presenter: FiatContract.Presenter) {
        this.presenter = checkNotNull(presenter)
    }
}
