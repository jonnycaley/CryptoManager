package com.jonnycaley.cryptomanager.ui.home

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import com.cooltechworks.views.shimmer.ShimmerRecyclerView
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Currency
import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.Article
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.Rate
import com.jonnycaley.cryptomanager.ui.article.ArticleArgs
import com.jonnycaley.cryptomanager.utils.Utils
import com.jonnycaley.cryptomanager.utils.interfaces.TabInterface
import com.like.LikeButton
import com.like.OnLikeListener
import com.squareup.picasso.Picasso
import android.os.Parcelable


class HomeFragment : Fragment(), TabInterface, HomeContract.View, OnLikeListener {

    lateinit var mView: View

    private lateinit var presenter: HomeContract.Presenter

    lateinit var articlesVerticalAdapter: HomeArticlesVerticalAdapter
    lateinit var topMoversAdapter: TopMoversAdapter

    lateinit var topArticle: Article

    val scrollLayout by lazy { mView.findViewById<ScrollView>(R.id.scroll_layout) }
    val progressBarLayout by lazy { mView.findViewById<ConstraintLayout>(R.id.progress_bar_layout) }

    val recyclerViewShimmerNews by lazy { mView.findViewById<RecyclerView>(R.id.shimmer_recycler_view) }
    val recyclerViewTopMovers by lazy { mView.findViewById<RecyclerView>(R.id.recycler_view_top_movers) }

    val cardTopArticle by lazy { mView.findViewById<CardView>(R.id.card_view) }

    val cardImage by lazy { mView.findViewById<ImageView>(R.id.card_image) }
    val cardTitle by lazy { mView.findViewById<TextView>(R.id.card_title) }
    val cardDescription by lazy { mView.findViewById<TextView>(R.id.card_description) }
    val cardLength by lazy { mView.findViewById<TextView>(R.id.card_length) }
    val cardDate by lazy { mView.findViewById<TextView>(R.id.card_date) }
    val cardStar by lazy { mView.findViewById<LikeButton>(R.id.like_button_top_article) }


    override fun setPresenter(presenter: HomeContract.Presenter) {
        this.presenter = checkNotNull(presenter)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_home, container, false)

        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cardStar.setOnLikeListener(this)

        presenter = HomePresenter(HomeDataManager.getInstance(context!!), this)
        presenter.attachView()
    }

    override fun onTabClicked() {
        Log.i(TAG, "onTabClicked()")
        presenter.onResume()
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun liked(p0: LikeButton?) {
        println("liked")
        presenter.saveArticle(topArticle)
    }

    override fun unLiked(p0: LikeButton?) {
        println("unLiked")
        presenter.removeArticle(topArticle)
    }

    override fun showNoInternet() {
        Snackbar.make(mView, R.string.splash_internet_required, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry) { presenter.getNews() }
                .show()
    }

    override fun hideProgressBar() {
        progressBarLayout.visibility = View.GONE
    }

    override fun showScrollLayout() {
        scrollLayout.visibility = View.VISIBLE
    }

    override fun showProgressBar() {
        progressBarLayout.visibility = View.VISIBLE
    }

    override fun showNews(news: ArrayList<Article>, savedArticles: ArrayList<Article>) {

        val newNews = ArrayList<Article>()

        news.forEach { newNews.add(it) }

        val headerArticle = newNews.first { it.thumbnail != null }

        newNews.remove(headerArticle)

        topArticle = headerArticle

        cardStar.isLiked = savedArticles.any { it.url == topArticle.url }

        showTopNewsArticle(headerArticle)

        val newsLayoutManager = LinearLayoutManager(context)
        recyclerViewShimmerNews.layoutManager = newsLayoutManager
        articlesVerticalAdapter = HomeArticlesVerticalAdapter(newNews, savedArticles, context, presenter)
        recyclerViewShimmerNews.adapter = articlesVerticalAdapter

    }

    private fun showTopNewsArticle(article: Article) {

        cardTopArticle.setOnClickListener {
            ArticleArgs(article).launch(context!!)
        }

        Picasso.with(context)
                .load(article.originalImageUrl)
                .fit()
                .centerCrop()
                .into(cardImage)

        cardTitle.text = article.title
        cardDescription.text = article.description
        cardLength.text = Utils.getReadTime(article.words)
        cardDate.text = Utils.getTimeFrom(article.publishedAt)
    }

    var layoutManager: LinearLayoutManager? = null

    override fun showTop100Changes(sortedBy: List<Currency>?, baseCurrency: Rate) {

        Log.i(TAG, "showTop100Changes")
        Log.i(TAG, (baseCurrency.fiat == null).toString())

        val arrayList = ArrayList<Currency>()

        sortedBy?.forEach { arrayList.add(it) }

        Log.i(TAG, baseCurrency.rate.toString())

        if (layoutManager == null) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            recyclerViewTopMovers.layoutManager = layoutManager
            topMoversAdapter = TopMoversAdapter(arrayList, baseCurrency, context)
            recyclerViewTopMovers.adapter = topMoversAdapter

        } else {

            topMoversAdapter.articles = arrayList
            topMoversAdapter.baseFiat = baseCurrency
            topMoversAdapter.notifyDataSetChanged()
        }
    }

    fun newInstance(headerStr: String): HomeFragment {
        val fragmentDemo = HomeFragment()
        val args = Bundle()
        args.putString("headerStr", headerStr)
        fragmentDemo.arguments = args
        return fragmentDemo
    }

    companion object {
        val TAG = "HomeFragment"
    }
}