package com.jonnycaley.cryptomanager.ui.settings.bookmarkedArticles

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.News.Article
import com.jonnycaley.cryptomanager.utils.Utils
import com.r0adkll.slidr.Slidr
import kotlinx.android.synthetic.main.activity_saved_articles.*

class BookmarkedArticlesActivity : AppCompatActivity(), BookmarkedArticlesContract.View {

    private lateinit var presenter : BookmarkedArticlesContract.Presenter

    lateinit var newsAdapter : BookmarkedArticlesAdapter

    val recyclerView by lazy { findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recycler_view) }
    val layoutNoArticles by lazy { findViewById<RelativeLayout>(R.id.layout_no_articles) }

    val layout by lazy { findViewById<LinearLayout>(R.id.layout) }
    val progressLayout by lazy { findViewById<ConstraintLayout>(R.id.progress_bar_layout) }

    override fun onCreate(savedInstanceState: Bundle?) {

        if(Utils.isDarkTheme()) {
            setTheme(R.style.darktheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_articles)

        Slidr.attach(this)

        if(!Utils.isDarkTheme()) { // is dark theme
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        }
        overridePendingTransition(R.anim.new_activity_open,R.anim.old_activity_close) // override transactions

        setupToolbar() // setup toolbar

        presenter = BookmarkedArticlesPresenter(BookmarkedArticlesDataManager.getInstance(this), this) // set up presenter
        presenter.attachView()
    }

    /*
    Function executes on pause
    */
    override fun onPause() {
        super.onPause()
    }

    /*
    Function hides progress layout
    */
    override fun hideProgressLayout() {
        progressLayout.visibility = View.GONE //change visibility
    }

    /*
    Function shows progress layout
    */
    override fun showProgressLayout() {
        progressLayout.visibility = View.VISIBLE //change visibility
    }

    /*
    Function shows error
    */
    override fun showError() {
        showSnackBar(resources.getString(R.string.error_occurred)) //show error
    }

    /*
    Function shows snackbar
    */
    fun showSnackBar(message: String) {
        Snackbar.make(layout, message, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry) { presenter.loadSavedArticles() }
                .show()
    }

    /*
    Function shows no articles layout
    */
    override fun showNoArticles() {
        layoutNoArticles.visibility = View.VISIBLE //change visibility
    }

    override fun hideNoArticles() {
        layoutNoArticles.visibility = View.GONE //change visibility
    }

    override fun showSavedNews(news: ArrayList<Article>) {

        val mLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        recyclerView.layoutManager = mLayoutManager
        newsAdapter = BookmarkedArticlesAdapter(news, this, presenter)
        recyclerView.adapter = newsAdapter
    }


    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.title = ""
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                super.onBackPressed()
                overridePendingTransition(R.anim.old_activity_open, R.anim.new_activity_close)
                return false
            }
        }
        return false
    }

    override fun setPresenter(presenter: BookmarkedArticlesContract.Presenter) {
        this.presenter = checkNotNull(presenter)
    }
}