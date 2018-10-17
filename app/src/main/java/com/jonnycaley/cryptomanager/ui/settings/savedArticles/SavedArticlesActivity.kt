package com.jonnycaley.cryptomanager.ui.settings.savedArticles

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.Article

class SavedArticlesActivity : AppCompatActivity(), SavedArticlesContract.View {

    private lateinit var presenter : SavedArticlesContract.Presenter

    lateinit var newsAdapter : SavedArticlesAdapter

    val recyclerView by lazy { findViewById<RecyclerView>(R.id.recycler_view) }
    val layoutNoArticles by lazy { findViewById<RelativeLayout>(R.id.layout_no_articles) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_articles)

        setupToolbar()

        presenter = SavedArticlesPresenter(SavedArticlesDataManager.getInstance(this), this)
        presenter.attachView()
    }

    override fun showNoArticles() {
        layoutNoArticles.visibility = View.VISIBLE
    }

    override fun hideNoArticles() {
        layoutNoArticles.visibility = View.GONE
    }

    override fun showSavedNews(news: ArrayList<Article>) {

        val mLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = mLayoutManager
        newsAdapter = SavedArticlesAdapter(news, this, presenter)
        recyclerView.adapter = newsAdapter
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

    override fun setPresenter(presenter: SavedArticlesContract.Presenter) {
        this.presenter = checkNotNull(presenter)
    }
}
