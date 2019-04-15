package com.jonnycaley.cryptomanager.ui.fiat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import com.jonnycaley.cryptomanager.ui.transactions.fiat.FiatTransactionArgs
import com.jonnycaley.cryptomanager.utils.Utils
import kotlinx.android.synthetic.main.activity_fiat.*
import java.math.BigDecimal
import kotlin.math.absoluteValue

class FiatActivity : AppCompatActivity() , FiatContract.View, View.OnClickListener {

    private lateinit var presenter : FiatContract.Presenter

    val args by lazy { FiatArgs.deserializeFrom(intent) }

    val recyclerView by lazy { findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recycler_view) }

    val textAvailable by lazy { findViewById<TextView>(R.id.text_available) }
    val textDeposited by lazy { findViewById<TextView>(R.id.text_total_deposited) }
    val textWithdrawn by lazy { findViewById<TextView>(R.id.text_total_withdrawn) }

    lateinit var holdingsAdapter : TransactionsAdapter

    val buttonAddTransaction by lazy { findViewById<Button>(R.id.button_add_transaction) }

    override fun onCreate(savedInstanceState: Bundle?) {

        if(Utils.isDarkTheme()) {
            setTheme(R.style.darktheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fiat)

        buttonAddTransaction.setOnClickListener(this) //add transaction button listener

        setupToolbar() //set up toolbar

        presenter = FiatPresenter(FiatDataManager.getInstance(this), this)
        presenter.attachView() //set up presenter
    }

    override fun onClick(v: View?) {
        when(v?.id){
            buttonAddTransaction.id -> {
                FiatTransactionArgs(null, args.fiat, false).launch(this) //set up transaction button
            }
        }
    }

    /*
    Function displays the progress bar - none atm as its quick
    */
    override fun showProgressBar() {
    }

    /*
    Function shows the available fiat text
    */
    override fun showAvailableFiat(fiatSymbol: String, availableFiatCount: BigDecimal) {
        textAvailable.text = "Available: ${Utils.getPriceTextAbs(availableFiatCount.toDouble(), fiatSymbol)}"
    }

    /*
    Function shows the deposited fiat text
    */
    override fun showDepositedFiat(fiatSymbol: String, depositedFiatCount: BigDecimal) {
        textDeposited.text = "${Utils.getPriceTextAbs(depositedFiatCount.toDouble(), fiatSymbol)}"
    }

    /*
    Function shows the withdrawn fiat text
    */
    override fun showWithdrawnFiat(fiatSymbol: String, withdrawnFiatCount: BigDecimal) {
        textWithdrawn.text = "${Utils.getPriceTextAbs(withdrawnFiatCount.toDouble().absoluteValue, fiatSymbol)}"
    }

    /*
    Function executes when the activity resumes and refreshes the transactions
    */
    override fun onResume() {
        presenter.getTransactions(args.fiat)
        super.onResume()
    }

    /*
    Function shows the transactions list
    */
    override fun showTransactions(fiatSymbol: String, transactions: List<Transaction>) {

        val mLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        recyclerView.layoutManager = mLayoutManager
        holdingsAdapter = TransactionsAdapter(args.fiat, fiatSymbol, transactions, this, this)
        recyclerView.adapter = holdingsAdapter

    }

    /*
    Function returns the fiat code
    */
    override fun getFiatCode(): String {
        return args.fiat
    }

    val title : TextView by lazy { findViewById<TextView>(R.id.title) }
    val toolbar : Toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }

    /*
    Function sets up the toolbar text and arrow color for different themes
    */
    private fun setupToolbar() {

        if(!Utils.isDarkTheme()) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        }
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        title.text = args.fiat
    }

    /*
    Function handles the toolbar buttons clicked
    */
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
