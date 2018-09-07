package com.jonnycaley.cryptomanager.ui.search

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.SearchView
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.AllCurrencies.Datum
import com.jonnycaley.cryptomanager.utils.Utils

class SearchActivity : AppCompatActivity() , SearchContract.View, SearchView.OnQueryTextListener {

    private lateinit var presenter : SearchContract.Presenter

    val searchBar by lazy { findViewById<SearchView>(R.id.search_view) }

    val recyclerView by lazy { findViewById<RecyclerView>(R.id.recycler_view) }

    lateinit var currenciesAdapter : SearchCurrenciesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        setupToolbar()
        setupSearchBar()

        presenter = SearchPresenter(SearchDataManager.getInstance(this), this)
        presenter.attachView()
    }

    override fun showCurrencies(currencies: List<Datum>?, baseImageUrl: String?, baseLinkUrl: String?) {

        val mLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = mLayoutManager
        currenciesAdapter = SearchCurrenciesAdapter(currencies?.sortedBy { it.sortOrder?.toInt() }, baseImageUrl, baseLinkUrl, this)
        recyclerView.adapter = currenciesAdapter

    }

    private fun setupSearchBar() {
        searchBar.setOnQueryTextListener(this)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        presenter.showCurrencies(query)
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
