package com.jonnycaley.cryptomanager.ui.home

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Color
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
import android.widget.TextView
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Currency
import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.Article
import com.jonnycaley.cryptomanager.ui.article.ArticleArgs
import com.jonnycaley.cryptomanager.utils.Utils
import com.jonnycaley.cryptomanager.utils.interfaces.TabInterface
import com.like.LikeButton
import com.like.OnLikeListener
import com.squareup.picasso.Picasso
import android.support.v4.widget.SwipeRefreshLayout
import android.widget.RelativeLayout
import com.jonnycaley.cryptomanager.ui.crypto.CryptoArgs


class HomeFragment : Fragment(), TabInterface, HomeContract.View, OnLikeListener, SwipeRefreshLayout.OnRefreshListener {

    lateinit var mView: View

    private lateinit var presenter: HomeContract.Presenter

    lateinit var articlesVerticalAdapter: HomeArticlesVerticalAdapter
    lateinit var topMoversAdapter: TopMoversAdapter

    lateinit var topArticle: Article

    val scrollLayout by lazy { mView.findViewById<android.support.v4.widget.SwipeRefreshLayout >(R.id.swipelayout) }
    val progressBarLayout by lazy { mView.findViewById<ConstraintLayout>(R.id.progress_bar_layout) }

    val recyclerViewShimmerNews by lazy { mView.findViewById<RecyclerView>(R.id.shimmer_recycler_view) }
//    val recyclerViewTopMovers by lazy { mView.findViewById<RecyclerView>(R.id.recycler_view_top_movers) }

    val cardTopArticle by lazy { mView.findViewById<CardView>(R.id.card_view) }

    val cardImage by lazy { mView.findViewById<ImageView>(R.id.card_image) }
    val cardTitle by lazy { mView.findViewById<TextView>(R.id.card_title) }
    val cardDescription by lazy { mView.findViewById<TextView>(R.id.card_description) }
    val cardLength by lazy { mView.findViewById<TextView>(R.id.card_length) }
    val cardDate by lazy { mView.findViewById<TextView>(R.id.card_date) }
    val cardStar by lazy { mView.findViewById<LikeButton>(R.id.like_button_top_article) }


    val card1Layout by lazy { mView.findViewById<RelativeLayout>(R.id.card_1_layout) }
    val card1Name by lazy { mView.findViewById<TextView>(R.id.card_1_name) }
    val card1 by lazy { mView.findViewById<RelativeLayout>(R.id.card_1) }
    val card1Percentage by lazy { mView.findViewById<TextView>(R.id.card_1_percentage) }

    val card2Layout by lazy { mView.findViewById<RelativeLayout>(R.id.card_2_layout) }
    val card2Name by lazy { mView.findViewById<TextView>(R.id.card_2_name) }
    val card2 by lazy { mView.findViewById<RelativeLayout>(R.id.card_2) }
    val card2Percentage by lazy { mView.findViewById<TextView>(R.id.card_2_percentage) }

    val card3Layout by lazy { mView.findViewById<RelativeLayout>(R.id.card_3_layout) }
    val card3Name by lazy { mView.findViewById<TextView>(R.id.card_3_name) }
    val card3 by lazy { mView.findViewById<RelativeLayout>(R.id.card_3) }
    val card3Percentage by lazy { mView.findViewById<TextView>(R.id.card_3_percentage) }

    val card4Layout by lazy { mView.findViewById<RelativeLayout>(R.id.card_4_layout) }
    val card4Name by lazy { mView.findViewById<TextView>(R.id.card_4_name) }
    val card4 by lazy { mView.findViewById<RelativeLayout>(R.id.card_4) }
    val card4Percentage by lazy { mView.findViewById<TextView>(R.id.card_4_percentage) }

    val card5Layout by lazy { mView.findViewById<RelativeLayout>(R.id.card_5_layout) }
    val card5Name by lazy { mView.findViewById<TextView>(R.id.card_5_name) }
    val card5 by lazy { mView.findViewById<RelativeLayout>(R.id.card_5) }
    val card5Percentage by lazy { mView.findViewById<TextView>(R.id.card_5_percentage) }

    val card6Layout by lazy { mView.findViewById<RelativeLayout>(R.id.card_6_layout) }
    val card6Name by lazy { mView.findViewById<TextView>(R.id.card_6_name) }
    val card6 by lazy { mView.findViewById<RelativeLayout>(R.id.card_6) }
    val card6Percentage by lazy { mView.findViewById<TextView>(R.id.card_6_percentage) }

    val card7Layout by lazy { mView.findViewById<RelativeLayout>(R.id.card_7_layout) }
    val card7Name by lazy { mView.findViewById<TextView>(R.id.card_7_name) }
    val card7 by lazy { mView.findViewById<RelativeLayout>(R.id.card_7) }
    val card7Percentage by lazy { mView.findViewById<TextView>(R.id.card_7_percentage) }

    val card8Layout by lazy { mView.findViewById<RelativeLayout>(R.id.card_8_layout) }
    val card8Name by lazy { mView.findViewById<TextView>(R.id.card_8_name) }
    val card8 by lazy { mView.findViewById<RelativeLayout>(R.id.card_8) }
    val card8Percentage by lazy { mView.findViewById<TextView>(R.id.card_8_percentage) }

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
        scrollLayout.setOnRefreshListener(this)

        presenter = HomePresenter(HomeDataManager.getInstance(context!!), this)
        presenter.attachView()
    }

    override fun onRefresh() {
        presenter.onResume()
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
        scrollLayout.isRefreshing = false
    }

    override fun showScrollLayout() {
        scrollLayout.visibility = View.VISIBLE
    }

    override fun showProgressBar() {
        progressBarLayout.visibility = View.VISIBLE
    }

    var newsLayoutManager : LinearLayoutManager? = null

    override fun showNews(news: HashMap<Article, Currency?>, savedArticles: ArrayList<Article>) {

        val headerArticle = news.keys.first()

        news.remove(headerArticle)

        topArticle = headerArticle

        cardStar.isLiked = savedArticles.any { it.url == topArticle.url }

        showTopNewsArticle(headerArticle)

        newsLayoutManager = LinearLayoutManager(context)
        recyclerViewShimmerNews.layoutManager = newsLayoutManager
        articlesVerticalAdapter = HomeArticlesVerticalAdapter(news, savedArticles, context, presenter)
        recyclerViewShimmerNews.adapter = articlesVerticalAdapter

//        if(newsLayoutManager == null) {
//            Log.i(TAG, "1")
//            newsLayoutManager = LinearLayoutManager(context)
//            recyclerViewShimmerNews.layoutManager = newsLayoutManager
//            articlesVerticalAdapter = HomeArticlesVerticalAdapter(news, savedArticles, context, presenter)
//            recyclerViewShimmerNews.adapter = articlesVerticalAdapter
//        } else {
//            Log.i(TAG, "2")
//            articlesVerticalAdapter.newsItems = news
//            articlesVerticalAdapter.savedArticles = savedArticles
//            articlesVerticalAdapter.notifyDataSetChanged()
////            articlesVerticalAdapter.newsItems?.forEach { articlesVerticalAdapter.notifyItemChanged(it) }
//        }

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

    override fun showTop100Changes(sortedBy: ArrayList<Currency>) {

//        animRed = ObjectAnimator.ofInt(holder.layout, "backgroundColor", Color.WHITE, Color.RED,
//                Color.WHITE)
//        animGreen = ObjectAnimator.ofInt(holder.layout, "backgroundColor", Color.WHITE, Color.GREEN,
//                Color.WHITE)

        Log.i(TAG, sortedBy.size.toString())

        if (sortedBy.size > 7) {
            for (i in 0..7) {
                val currency = sortedBy.filter { it.name != "Tether" }[i]

                Log.i(TAG, currency.name)

                var card = card1
                var name = card1Name
                var percentage = card1Percentage
                var background = card1Layout

                when (i) {
                    0 -> {
                        card = card1
                        name = card1Name
                        percentage = card1Percentage
                        background = card1Layout
                    }
                    1 -> {
                        card = card2
                        name = card2Name
                        percentage = card2Percentage
                        background = card2Layout
                    }
                    2 -> {
                        card = card3
                        name = card3Name
                        percentage = card3Percentage
                        background = card3Layout
                    }
                    3 -> {
                        card = card4
                        name = card4Name
                        percentage = card4Percentage
                        background = card4Layout
                    }
                    4 -> {
                        card = card5
                        name = card5Name
                        percentage = card5Percentage
                        background = card5Layout
                    }
                    5 -> {
                        card = card6
                        name = card6Name
                        percentage = card6Percentage
                        background = card6Layout
                    }
                    6 -> {
                        card = card7
                        name = card7Name
                        percentage = card7Percentage
                        background = card7Layout
                    }
                    7 -> {
                        card = card8
                        name = card8Name
                        percentage = card8Percentage
                        background = card8Layout
                    }
                }

                Log.i(TAG, currency.quote?.uSD?.percentChange24h?.toBigDecimal().toString())

                val percentage2DP = Utils.formatPercentage(currency.quote?.uSD?.percentChange24h?.toBigDecimal())

//                if(currency.name?.length!! > 9)
//                    name.text = currency.symbol
//                else
                name.text = currency.name
                percentage.text = percentage2DP

                background.setOnClickListener{
                    CryptoArgs(currency.symbol!!).launch(context!!)
                }

                Log.i(TAG, percentage2DP.substring(0, 1))

                when {
                    percentage2DP.substring(0, 1) == "+" -> {
                        card.setBackgroundResource(R.drawable.border_green_large_round)
                        percentage.setTextColor(context?.resources?.getColor(R.color.green)!!)
                        val animGreen = ObjectAnimator.ofInt(background, "backgroundColor", Color.TRANSPARENT, Color.GREEN, Color.TRANSPARENT)

                        if(Math.random() < 0.5){

                            animGreen.duration = 1000
                            animGreen.setEvaluator(ArgbEvaluator())
                            animGreen.repeatMode = ValueAnimator.REVERSE

                            animGreen.start()
                        }
                    }
                    else -> {
                        card.setBackgroundResource(R.drawable.border_red_large_round)
                        percentage.setTextColor(context?.resources?.getColor(R.color.red)!!)
                        val animRed = ObjectAnimator.ofInt(background, "backgroundColor", Color.TRANSPARENT, Color.RED, Color.TRANSPARENT)

                        if(Math.random() < 0.5) {

                            animRed.duration = 1000
                            animRed.setEvaluator(ArgbEvaluator())
                            animRed.repeatMode = ValueAnimator.REVERSE

                            animRed.start()
                        }
                    }
                }
            }
        }
        Log.i(TAG, "Done")

//        Log.i(TAG, "showTop100Changes")
//        Log.i(TAG, (baseCurrency.fiat == null).toString())
//
//        val arrayList = ArrayList<Currency>()
//
//        sortedBy?.forEach { arrayList.add(it) }
//
//        Log.i(TAG, baseCurrency.rate.toString())
//
//        if (layoutManager == null) {
//            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
//            recyclerViewTopMovers.layoutManager = layoutManager
//            topMoversAdapter = TopMoversAdapter(arrayList, baseCurrency, context)
//            recyclerViewTopMovers.adapter = topMoversAdapter
//
//        } else {
//
//            topMoversAdapter.articles = arrayList
//            topMoversAdapter.baseFiat = baseCurrency
//            topMoversAdapter.notifyDataSetChanged()
//        }
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