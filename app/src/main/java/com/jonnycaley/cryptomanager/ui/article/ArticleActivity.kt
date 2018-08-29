package com.jonnycaley.cryptomanager.ui.article

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import android.webkit.WebViewClient
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView
import com.takusemba.multisnaprecyclerview.OnSnapListener
import android.support.v7.widget.LinearLayoutManager
import android.view.ViewGroup
import com.jonnycaley.cryptomanager.ui.adapters.SimilarArticlesHorizontalAdapter


class ArticleActivity : AppCompatActivity(), ArticleContract.View{

    private val args by lazy { ArticleArgs.deserializeFrom(intent) }

    lateinit var presenter : BasePresenter

    private val webview by lazy { findViewById<WebView>(R.id.webview) }
    private val recyclerviewSnap by lazy { findViewById<MultiSnapRecyclerView>(R.id.recyclerview_snap) }

    private val adapter by lazy { SimilarArticlesHorizontalAdapter(args.article.similarArticles, this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_detail)

        setupToolbar()

        webview.webViewClient = WebViewClient()
        webview.loadUrl(args.article.url)

        if(args.article.similarArticles == null || args.article.similarArticles?.isEmpty()!!){
            recyclerviewSnap.visibility = View.GONE

            val params = webview.layoutParams as ViewGroup.MarginLayoutParams
            params.bottomMargin = 0
            webview.layoutParams = params

        } else {
            setupRelated()
        }

        presenter = ArticlePresenter(ArticleDataManager.getInstance(this), this)
        presenter.attachView()
    }

    private fun setupRelated() {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        recyclerviewSnap.layoutManager = layoutManager
        recyclerviewSnap.adapter = adapter
        recyclerviewSnap.setOnSnapListener(OnSnapListener {
            // do something with the position of the snapped view
        })
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
//        supportActionBar?.title = args.article.title
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

    override fun setPresenter(presenter: ArticleContract.Presenter) {
        this.presenter = checkNotNull(presenter)
    }
}
