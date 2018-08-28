package com.jonnycaley.cryptomanager.ui.currency

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter

class CurrencyActivity : AppCompatActivity() , CurrencyContract.View{

    lateinit var presenter : BasePresenter

    private val args by lazy { CurrencyArgs.deserializeFrom(intent) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_currency)

        setupToolbar()

        presenter = CurrencyPresenter(CurrencyDataManager.getInstance(this), this)
        presenter.attachView()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = args.currency.name
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

    override fun setPresenter(presenter: CurrencyContract.Presenter) {
        this.presenter = checkNotNull(presenter)
    }
}
