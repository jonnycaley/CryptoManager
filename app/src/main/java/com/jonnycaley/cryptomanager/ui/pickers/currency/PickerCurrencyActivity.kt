package com.jonnycaley.cryptomanager.ui.pickers.currency

import android.content.Intent
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.AllCurrencies.Datum
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.ExchangeRates
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.Rate
import com.jonnycaley.cryptomanager.utils.Utils
import com.r0adkll.slidr.Slidr
import kotlinx.android.synthetic.main.activity_picker_currency.*

class PickerCurrencyActivity : AppCompatActivity(), PickerCurrencyContract.View, View.OnClickListener, SearchView.OnQueryTextListener {

    private lateinit var presenter: PickerCurrencyContract.Presenter

    val recyclerView by lazy { findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recycler_view) }

    val layout by lazy { findViewById<LinearLayout>(R.id.layout) }

    val progressBarLayout by lazy { findViewById<ConstraintLayout>(R.id.progress_bar_layout) }

    val searchBar by lazy { findViewById<SearchView>(R.id.search_view) }

    val layoutNoInternet by lazy { findViewById<RelativeLayout>(R.id.layout_no_internet) }
    val imageNoInternet by lazy { findViewById<ImageView>(R.id.image_no_internet) }
    val textRetry by lazy { findViewById<TextView>(R.id.text_try_again) }

    lateinit var pickerAdapter : PickerCurrenciesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        if(Utils.isDarkTheme()) {
            setTheme(R.style.darktheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picker_currency)

        Slidr.attach(this)

        if(!Utils.isDarkTheme()) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        }

        if(Utils.isDarkTheme()) {
            imageNoInternet.setImageResource(R.drawable.no_internet_white)
        }

        setupSearchBar()
        recyclerView.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(dy != 0)
                    Utils.hideKeyboardFromActivity(this@PickerCurrencyActivity)
            }
        })

        textRetry.setOnClickListener(this)

        setupToolbar()

        presenter = PickerCurrencyPresenter(PickerCurrencyDataManager.getInstance(this), this)
        presenter.attachView()
    }

    override fun showSearchBar() {
        searchBar.visibility = View.VISIBLE
    }

    private fun setupSearchBar() {
        searchBar.setOnQueryTextListener(this)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        Utils.hideKeyboardFromActivity(this)
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        query?.let {
            presenter.filterFiats(it.trim())

        }
        return true
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

    /*
    Function shows the fiats
    */
    override fun showFiats(fiats: List<Rate>?) {

        val data = ArrayList<Datum>()

        fiats?.sortedBy { it.fiat }?.forEach {
            val datum = Datum()

            datum.coinName = Utils.getFiatName(it.fiat)
            datum.symbol = it.fiat

            data.add(datum)
        }

        val mLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
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

    /*
    Function notifies of picket chosen
    */
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