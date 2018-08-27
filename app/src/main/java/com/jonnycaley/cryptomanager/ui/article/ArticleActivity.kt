package com.jonnycaley.cryptomanager.ui.article

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import kotlinx.android.synthetic.main.activity_article_detail.*
import android.webkit.WebViewClient



class ArticleActivity : AppCompatActivity() , ArticleContract.View{

    private val args by lazy { ArticleArgs.deserializeFrom(intent) }

    lateinit var presenter : BasePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_detail)

        webview.webViewClient = WebViewClient()

        webview.loadUrl(args.article.url)

        presenter = ArticlePresenter(ArticleDataManager.getInstance(this), this)
        presenter.attachView()
    }

    override fun setPresenter(presenter: ArticleContract.Presenter) {
        this.presenter = checkNotNull(presenter)
    }
}
