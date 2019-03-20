package com.jonnycaley.cryptomanager.ui.pickers.currency

import android.content.Intent
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.AllCurrencies.Datum
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.ExchangeRates
import com.jonnycaley.cryptomanager.utils.Utils
import kotlinx.android.synthetic.main.activity_picker_currency.*

class PickerCurrencyActivity : AppCompatActivity(), PickerCurrencyContract.View, View.OnClickListener {

    private lateinit var presenter: PickerCurrencyContract.Presenter

    val recyclerView by lazy { findViewById<RecyclerView>(R.id.recycler_view) }

    val layout by lazy { findViewById<LinearLayout>(R.id.layout) }

    val progressBarLayout by lazy { findViewById<ConstraintLayout>(R.id.progress_bar_layout) }

    val layoutNoInternet by lazy { findViewById<RelativeLayout>(R.id.layout_no_internet) }
    val imageNoInternet by lazy { findViewById<ImageView>(R.id.image_no_internet) }
    val textRetry by lazy { findViewById<TextView>(R.id.text_try_again) }

    lateinit var pickerAdapter : PickerCurrenciesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        if(Utils.isDarkTheme()) {
            setTheme(R.style.darktheme)
            imageNoInternet.setImageResource(R.drawable.no_internet_white)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picker_currency)

        if(!Utils.isDarkTheme()) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        }

        textRetry.setOnClickListener(this)

        setupToolbar()

        presenter = PickerCurrencyPresenter(PickerCurrencyDataManager.getInstance(this), this)
        presenter.attachView()
    }

    override fun showError() {
        showSnackBar(resources.getString(R.string.error_occurred))
    }

    var snackBar : Snackbar? = null

    fun showSnackBar(message: String) {

        snackBar = Snackbar.make(layout, message, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry) {
                    presenter.getFiats()
                }
        snackBar.let { it?.show() }
    }

    override fun hideProgressBar() {
        progressBarLayout.visibility = View.GONE
    }

    override fun showProgressBar() {
        progressBarLayout.visibility = View.VISIBLE
    }

    override fun showFiats(fiats: ExchangeRates) {

        val data = ArrayList<Datum>()

        fiats.rates?.sortedBy { it.fiat }?.forEach {
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

    override fun onClick(v: View?) {
        presenter.getFiats()
    }

    override fun hideNoInternetLayout() {
        layoutNoInternet.visibility = View.GONE
    }

    override fun showNoInternetLayout() {
        layoutNoInternet.visibility = View.VISIBLE
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