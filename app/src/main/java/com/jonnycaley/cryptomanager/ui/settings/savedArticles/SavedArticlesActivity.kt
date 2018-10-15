package com.jonnycaley.cryptomanager.ui.settings.savedArticles

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.News
import com.jonnycaley.cryptomanager.ui.settings.SettingsAdapter

class SavedArticlesActivity : AppCompatActivity(), SavedArticlesContract.View {

    private lateinit var presenter : SavedArticlesContract.Presenter

    lateinit var newsAdapter : NewsAdapter

    val recyclerView by lazy { findViewById<RecyclerView>(R.id.recycler_view) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_articles)

        presenter = SavedArticlesPresenter(SavedArticlesDataManager.getInstance(this), this)
        presenter.attachView()
    }

    override fun showSavedNews(news: ArrayList<News>) {

        val mLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = mLayoutManager
        newsAdapter = NewsAdapter(news, this)
        recyclerView.adapter = newsAdapter
    }

    override fun setPresenter(presenter: SavedArticlesContract.Presenter) {
        this.presenter = checkNotNull(presenter)
    }
}
