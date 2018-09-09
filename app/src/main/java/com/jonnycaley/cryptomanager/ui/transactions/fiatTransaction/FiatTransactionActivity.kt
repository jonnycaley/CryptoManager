package com.jonnycaley.cryptomanager.ui.transactions.fiatTransaction

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.jonnycaley.cryptomanager.R

class FiatTransactionActivity : AppCompatActivity(), FiatTransactionContract.View {

    private lateinit var presenter: FiatTransactionContract.Presenter

    val args by lazy { FiatTransactionArgs.deserializeFrom(intent) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fiat_transaction)

        setupToolbar()

        presenter = FiatTransactionPresenter(FiatTransactionDataManager.getInstance(this), this)
        presenter.attachView()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = args.currency.coinName
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

    override fun setPresenter(presenter: FiatTransactionContract.Presenter) {
        this.presenter = checkNotNull(presenter)
    }

}