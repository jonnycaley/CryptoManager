package com.jonnycaley.cryptomanager.ui.fiat

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import com.jonnycaley.cryptomanager.ui.transactions.fiat.create.CreateFiatTransactionArgs
import com.jonnycaley.cryptomanager.utils.Utils
import java.math.BigDecimal
import kotlin.math.absoluteValue

class FiatActivity : AppCompatActivity() , FiatContract.View, View.OnClickListener {

    private lateinit var presenter : FiatContract.Presenter

    val args by lazy { FiatArgs.deserializeFrom(intent) }

    val recyclerView by lazy { findViewById<RecyclerView>(R.id.recycler_view) }

    val textAvailable by lazy { findViewById<TextView>(R.id.text_available) }
    val textDeposited by lazy { findViewById<TextView>(R.id.text_total_deposited) }
    val textWithdrawn by lazy { findViewById<TextView>(R.id.text_total_withdrawn) }

    lateinit var holdingsAdapter : TransactionsAdapter

    val buttonAddTransaction by lazy { findViewById<Button>(R.id.button_add_transaction) }

    override fun onCreate(savedInstanceState: Bundle?) {

        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.darktheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fiat)

        buttonAddTransaction.setOnClickListener(this)

        setupToolbar()

        presenter = FiatPresenter(FiatDataManager.getInstance(this), this)
        presenter.attachView()
    }

    override fun onClick(v: View?) {
        when(v?.id){
            buttonAddTransaction.id -> {
                CreateFiatTransactionArgs(args.fiat, false).launch(this)
            }
        }
    }

    override fun showProgressBar() {

    }

    override fun showAvailableFiat(fiatSymbol: String, availableFiatCount: BigDecimal) {
        textAvailable.text = "Available: ${Utils.getPriceTextAbs(availableFiatCount.toDouble(), fiatSymbol)}"
    }

    override fun showDepositedFiat(fiatSymbol: String, depositedFiatCount: BigDecimal) {
        textDeposited.text = "${Utils.getPriceTextAbs(depositedFiatCount.toDouble(), fiatSymbol)}"
    }

    override fun showWithdrawnFiat(fiatSymbol: String, withdrawnFiatCount: BigDecimal) {
        textWithdrawn.text = "${Utils.getPriceTextAbs(withdrawnFiatCount.toDouble().absoluteValue, fiatSymbol)}"
    }


    override fun onResume() {
        presenter.getTransactions(args.fiat)
        super.onResume()
    }

    override fun showTransactions(fiatSymbol: String, transactions: List<Transaction>) {

        val mLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = mLayoutManager
        holdingsAdapter = TransactionsAdapter(args.fiat, fiatSymbol, transactions, this, this)
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
