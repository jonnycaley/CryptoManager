package com.jonnycaley.cryptomanager.ui.search

import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.SearchView
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.AllCurrencies.Datum
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.Rate
import com.jonnycaley.cryptomanager.ui.portfolio.PortfolioFragment
import com.jonnycaley.cryptomanager.utils.Utils
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity() , SearchContract.View, SearchView.OnQueryTextListener {

    private lateinit var presenter : SearchContract.Presenter

    val searchBar by lazy { findViewById<SearchView>(R.id.search_view) }
    val recyclerView by lazy { findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recycler_view) }

    val layoutProgressBar by lazy { findViewById<ConstraintLayout>(R.id.progress_bar_layout) }

    val layout by lazy { findViewById<LinearLayout>(R.id.layout) }

    val args by lazy { SearchArgs.deserializeFrom(intent) }

    lateinit var currenciesAdapter : SearchCurrenciesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        if(Utils.isDarkTheme()) {
            setTheme(R.style.darktheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        if(!Utils.isDarkTheme()) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        }
        setupToolbar()
        setupSearchBar()
        recyclerView.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(dy != 0)
                    Utils.hideKeyboardFromActivity(this@SearchActivity)
            }
        })

        presenter = SearchPresenter(SearchDataManager.getInstance(this), this)
        presenter.attachView()
    }

    override fun showProgressLayout() {
        layoutProgressBar.visibility = View.VISIBLE
    }

    override fun hideProgressLayout() {
        layoutProgressBar.visibility = View.GONE
    }

    override fun showError() {
        showSnackBar(resources.getString(R.string.error_occurred))
    }

    fun showSnackBar(message: String) {
        Snackbar.make(layout, message, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry) {
                    when (getSearchType()) {
                        PortfolioFragment.CURRENCY_STRING -> {
                            presenter.getAllCurrencies()
                        }
                        PortfolioFragment.FIAT_STRING -> {
                            presenter.getAllFiats()
                        }
                    }
                }
                .show()
    }

    override fun showSearchBar() {
        searchBar.visibility = View.VISIBLE
    }

    override fun showCurrencies(currencies: List<Datum>?, baseImageUrl: String?, baseLinkUrl: String?) {

        val mLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        recyclerView.layoutManager = mLayoutManager
        currenciesAdapter = SearchCurrenciesAdapter(currencies?.sortedBy { it.sortOrder?.toInt() }, baseImageUrl, baseLinkUrl, this)
        recyclerView.adapter = currenciesAdapter

    }

    override fun getSearchType(): String? {
        return args.transactionString
    }

    override fun showFiats(rates: List<Rate>, withUsdTop: Boolean) {

        val data = ArrayList<Datum>()

        if(withUsdTop){

            val datum = Datum()

            datum.coinName = "United States Dollar"
            datum.symbol = "USD"

            data.add(datum)


            rates.filter { it.fiat != "USD" }.sortedBy { it.fiat }.forEach {
                val datum = Datum()

                datum.coinName = Utils.getFiatName(it.fiat)
                datum.symbol = it.fiat

                data.add(datum)
            }
        } else {

            rates.sortedBy { it.fiat }.forEach {
                val datum = Datum()

                datum.coinName = Utils.getFiatName(it.fiat)
                datum.symbol = it.fiat

                data.add(datum)
            }
        }

        val mLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        recyclerView.layoutManager = mLayoutManager
        currenciesAdapter = SearchCurrenciesAdapter(data, null, null, this)
        recyclerView.adapter = currenciesAdapter
    }

    private fun setupSearchBar() {
        searchBar.setOnQueryTextListener(this)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        Utils.hideKeyboardFromActivity(this)
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        when(args.transactionString){
            PortfolioFragment.CURRENCY_STRING -> {
                presenter.showCurrencies(query?.trim())
            }
            PortfolioFragment.FIAT_STRING -> {
                presenter.showFiats(query?.trim())
            }
        }
        return true
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
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

    override fun setPresenter(presenter: SearchContract.Presenter) {
        this.presenter = checkNotNull(presenter)
    }
}
