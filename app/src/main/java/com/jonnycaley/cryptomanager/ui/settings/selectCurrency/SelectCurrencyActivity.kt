package com.jonnycaley.cryptomanager.ui.settings.selectCurrency

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.AllCurrencies.Datum
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.ExchangeRates
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.Rate
import com.jonnycaley.cryptomanager.utils.Utils


class SelectCurrencyActivity : AppCompatActivity(), SelectCurrencyContract.View {

    private lateinit var presenter : SelectCurrencyContract.Presenter

    val recyclerView by lazy { findViewById<RecyclerView>(R.id.recycler_view) }

    lateinit var currenciesAdapter : SelectCurrenciesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_currency)

        setupToolbar()

        presenter = SelectCurrencyPresenter(SelectCurrencyDataManager.getInstance(this), this)
        presenter.attachView()
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

    override fun onCurrencySaved() {
        super.onBackPressed()
    }

    override fun showFiats(fiats: List<Rate>?, baseFiat: Rate) {

//        val data = ArrayList<Datum>()
//
//        val datum = Datum()
//
//        datum.coinName = "United States Dollar"
//        datum.symbol = "USD"
//
//        data.add(datum)
//
//        fiats.rates?.forEach {
//            val datum = Datum()
//
//            datum.coinName = Utils.getFiatName(it.fiat)
//            datum.symbol = it.fiat
//
//            data.add(datum)
//        }

        val mLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = mLayoutManager
        currenciesAdapter = SelectCurrenciesAdapter(fiats, presenter, this)
        recyclerView.adapter = currenciesAdapter
    }

    override fun setPresenter(presenter: SelectCurrencyContract.Presenter) {
        this.presenter = checkNotNull(presenter)
    }
}
