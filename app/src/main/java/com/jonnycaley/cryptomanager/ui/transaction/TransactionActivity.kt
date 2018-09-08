package com.jonnycaley.cryptomanager.ui.transaction

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.jonnycaley.cryptomanager.R

class TransactionActivity : AppCompatActivity(), TransactionContract.View {

    private lateinit var presenter: TransactionContract.Presenter

    val args by lazy { TransactionArgs.deserializeFrom(intent) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction)

        setupToolbar()

        presenter = TransactionPresenter(TransactionDataManager.getInstance(this), this)
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

    override fun setPresenter(presenter: TransactionContract.Presenter) {
        this.presenter = checkNotNull(presenter)
    }

}

