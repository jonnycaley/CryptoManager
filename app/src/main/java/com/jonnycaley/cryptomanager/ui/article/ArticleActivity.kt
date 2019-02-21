package com.jonnycaley.cryptomanager.ui.article

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import com.jonnycaley.cryptomanager.R
import android.webkit.WebViewClient
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView
import com.takusemba.multisnaprecyclerview.OnSnapListener
import android.support.v7.widget.LinearLayoutManager
import android.view.ViewGroup
import android.webkit.WebSettings
import android.widget.Toast
import com.like.LikeButton
import com.like.OnLikeListener

class ArticleActivity : AppCompatActivity(), ArticleContract.View, OnLikeListener {

    private val args by lazy { ArticleArgs.deserializeFrom(intent) }

    private lateinit var presenter : ArticleContract.Presenter

    private val webview by lazy { findViewById<WebView>(R.id.webview) }
    private val recyclerviewSnap by lazy { findViewById<MultiSnapRecyclerView>(R.id.recyclerview_snap) }
    val progressBarLayout by lazy { findViewById<ConstraintLayout>(R.id.progress_bar_layout) }


    private val adapter by lazy { SimilarArticlesHorizontalAdapter(args.article.similarArticles, this) }

    val likeButton by lazy { findViewById<LikeButton>(R.id.like_button_top_article) }

    override fun onCreate(savedInstanceState: Bundle?) {

        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.darktheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_detail)

        setupToolbar()

        webview.webViewClient =  object : WebViewClient() {

            override fun onPageFinished(view: WebView, url: String) {
                webview.visibility = View.VISIBLE
                progressBarLayout.visibility = View.GONE
            }
            //TODO: HANDLE ERROR HERE WITH OVERRIDE METHOD
        }
//        webview.webViewClient = obj
        webview.loadUrl(args.article.url)
        webview.settings.setRenderPriority(WebSettings.RenderPriority.HIGH)

        likeButton.setOnLikeListener(this)
//        if(args.article.similarArticles == null || args.article.similarArticles?.isEmpty()!!){
//            recyclerviewSnap.visibility = View.GONE
//
//            val params = webview.layoutParams as ViewGroup.MarginLayoutParams
//            params.bottomMargin = 0
//            webview.layoutParams = params
//
//        } else {
//            setupRelated()
//        }
        presenter = ArticlePresenter(ArticleDataManager.getInstance(this), this)
        presenter.attachView()
    }


    override fun liked(p0: LikeButton?) {
        println("liked")
        presenter.saveArticle(args.article)
    }

    override fun unLiked(p0: LikeButton?) {
        println("unLiked")
        presenter.removeArticle(args.article)
    }

    override fun getArticleUrl(): String? {
        return args.article.url
    }

    override fun setLikeButton(isLiked: Boolean) {
        likeButton.visibility = View.VISIBLE
        likeButton.isLiked = isLiked
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
