package com.jonnycaley.cryptomanager.ui.search

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.SearchView
import com.jonnycaley.cryptomanager.R

class SearchActivity : AppCompatActivity() , SearchContract.View, SearchView.OnQueryTextListener {

    private lateinit var presenter : SearchContract.Presenter

    val searchBar by lazy { findViewById<SearchView>(R.id.search_view) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        setupToolbar()
        setupSearchBar()

        presenter = SearchPresenter(SearchDataManager.getInstance(this), this)
        presenter.attachView()
    }

    private fun setupSearchBar() {
        searchBar.setOnQueryTextListener(this)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        println(query)
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        println(query)
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
