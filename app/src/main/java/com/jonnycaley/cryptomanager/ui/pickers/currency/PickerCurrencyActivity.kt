package com.jonnycaley.cryptomanager.ui.pickers.currency

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.AllCurrencies.Datum
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.ExchangeRates
import com.jonnycaley.cryptomanager.utils.Utils

class PickerCurrencyActivity : AppCompatActivity(), PickerCurrencyContract.View {

    private lateinit var presenter: PickerCurrencyContract.Presenter

    val recyclerView by lazy { findViewById<RecyclerView>(R.id.recycler_view) }

    lateinit var pickerAdapter : PickerCurrenciesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.darktheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picker_currency)

        setupToolbar()

        presenter = PickerCurrencyPresenter(PickerCurrencyDataManager.getInstance(this), this)
        presenter.attachView()
    }


    override fun showFiats(fiats: ExchangeRates) {

        val data = ArrayList<Datum>()

        fiats.rates?.forEach {
            val datum = Datum()

            datum.coinName = Utils.getFiatName(it.fiat)
            datum.symbol = it.fiat

            data.add(datum)
        }

        val mLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = mLayoutManager
        pickerAdapter = PickerCurrenciesAdapter(data, this, this)
        recyclerView.adapter = pickerAdapter
    }

    override fun onPickerChosen(symbol: String?) {
        val intent = Intent()
        intent.putExtra("data", symbol)
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Currency"
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

    override fun setPresenter(presenter: PickerCurrencyContract.Presenter) {
        this.presenter = checkNotNull(presenter)
    }
}
