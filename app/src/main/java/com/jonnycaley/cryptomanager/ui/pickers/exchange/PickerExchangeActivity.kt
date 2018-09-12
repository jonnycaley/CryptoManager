package com.jonnycaley.cryptomanager.ui.pickers.exchange

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.Exchanges.Exchanges

class PickerExchangeActivity : AppCompatActivity(), PickerExchangeContract.View {

    private lateinit var presenter: PickerExchangeContract.Presenter

    lateinit var exchangesAdapter : ExchangesAdapter

    val recyclerView by lazy { findViewById<RecyclerView>(R.id.recycler_view) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picker_exchange)

        setupToolbar()

        presenter = PickerExchangePresenter(PickerExchangeDataManager.getInstance(this), this)
        presenter.attachView()
    }

    override fun onExchangeChosen(name: String?) {
        val intent = Intent()
        intent.putExtra("data", name)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun showExchanges(exchanges: Exchanges?) {

        val mLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = mLayoutManager
        exchangesAdapter = ExchangesAdapter(exchanges?.exchanges?.sortedBy { it.name }, this, this)
        recyclerView.adapter = exchangesAdapter

    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "PickerExchangeActivity"
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

    override fun setPresenter(presenter: PickerExchangeContract.Presenter) {
        this.presenter = checkNotNull(presenter)
    }
}