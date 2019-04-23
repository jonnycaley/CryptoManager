package com.jonnycaley.cryptomanager.ui.article

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import com.jonnycaley.cryptomanager.R
import android.webkit.WebViewClient
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView
import com.takusemba.multisnaprecyclerview.OnSnapListener
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.ViewGroup
import android.webkit.WebSettings
import android.widget.Toast
import com.jonnycaley.cryptomanager.utils.Utils
import com.like.LikeButton
import com.like.OnLikeListener
import kotlinx.android.synthetic.main.activity_article_detail.*

class ArticleActivity : AppCompatActivity(), ArticleContract.View, OnLikeListener {

    private val args by lazy { ArticleArgs.deserializeFrom(intent) }

    private lateinit var presenter : ArticleContract.Presenter

    private val webview by lazy { findViewById<WebView>(R.id.webview) }
    private val recyclerviewSnap by lazy { findViewById<MultiSnapRecyclerView>(R.id.recyclerview_snap) }
    val progressBarLayout by lazy { findViewById<ConstraintLayout>(R.id.progress_bar_layout) }

    private val adapter by lazy { SimilarArticlesHorizontalAdapter(args.article.similarArticles, this) }

    val likeButton by lazy { findViewById<LikeButton>(R.id.like_button_top_article) }

    override fun onCreate(savedInstanceState: Bundle?) {

        if(Utils.isDarkTheme()) {
            setTheme(R.style.darktheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_detail)

        if(!Utils.isDarkTheme()) { //configure dark theme
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)

            likeButton.setLikeDrawable(resources.getDrawable(R.drawable.bookmark_fill_black))
            likeButton.setUnlikeDrawable(resources.getDrawable(R.drawable.bookmark_outlline_black))
        }

        setupToolbar() //setup the toolbar

        webview.webViewClient =  object : WebViewClient() {  //setup the webview client

            override fun onPageFinished(view: WebView, url: String) {
                webview.visibility = View.VISIBLE
                progressBarLayout.visibility = View.GONE
            }
        }
        webview.loadUrl(args.article.url)  //load the url
        webview.settings.setRenderPriority(WebSettings.RenderPriority.HIGH)

        likeButton.setOnLikeListener(this)  //set the listener

        presenter = ArticlePresenter(ArticleDataManager.getInstance(this), this)
        presenter.attachView()
    }


    /*
    Function for when the article is liked
     */
    override fun liked(p0: LikeButton?) {
        presenter.saveArticle(args.article)
    }


    /*
    Function for when the article is unLiked
     */
    override fun unLiked(p0: LikeButton?) {
        presenter.removeArticle(args.article)
    }


    /*
    Function returns the article url
     */
    override fun getArticleUrl(): String? {
        return args.article.url
    }

    /*
    Function sets the initial like button visibility
     */
    override fun setLikeButton(isLiked: Boolean) {
        likeButton.visibility = View.VISIBLE
        likeButton.isLiked = isLiked
    }

    /*
    The setupToolbar method sets up the toolbar title
     */
    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.title = args.article.title
    }

    /*
    Function handles the back press button
     */
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

    /*
    Function initialises the presenter
     */
    override fun setPresenter(presenter: ArticleContract.Presenter) {
        this.presenter = checkNotNull(presenter)
    }
}
