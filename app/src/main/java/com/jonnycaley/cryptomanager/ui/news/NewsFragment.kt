package com.jonnycaley.cryptomanager.ui.news

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Typeface
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.Fragment
import androidx.core.widget.NestedScrollView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Currency
import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.News.Article
import com.jonnycaley.cryptomanager.ui.article.ArticleArgs
import com.jonnycaley.cryptomanager.utils.Utils
import com.jonnycaley.cryptomanager.utils.interfaces.TabInterface
import com.like.LikeButton
import com.like.OnLikeListener
import com.squareup.picasso.Picasso
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.appcompat.app.AppCompatDelegate
import android.widget.RelativeLayout
import com.jonnycaley.cryptomanager.ui.crypto.CryptoArgs


class NewsFragment : androidx.fragment.app.Fragment(), TabInterface, NewsContract.View, OnLikeListener, androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    lateinit var mView: View

    private lateinit var presenter: NewsContract.Presenter

    lateinit var articlesVerticalAdapter: NewsArticlesVerticalAdapter
    lateinit var topMoversAdapter: TopMoversAdapter

    lateinit var topArticle: Article

    val layoutNoInternet by lazy { mView.findViewById<RelativeLayout>(R.id.layout_no_internet) }
    val imageNoInternet by lazy { mView.findViewById<ImageView>(R.id.image_no_internet) }
    val textRetry by lazy { mView.findViewById<TextView>(R.id.text_try_again) }

    val scrollLayout by lazy { mView.findViewById<NestedScrollView>(R.id.scroll_layout) }
    val swipeLayout by lazy { mView.findViewById<androidx.swiperefreshlayout.widget.SwipeRefreshLayout>(R.id.swipelayout) }
    val progressBarLayout by lazy { mView.findViewById<ConstraintLayout>(R.id.progress_bar_layout) }

    val recyclerViewShimmerNews by lazy { mView.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.shimmer_recycler_view) }

    val layoutTopArticle by lazy { mView.findViewById<RelativeLayout>(R.id.layout_top) }

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

    override fun setPresenter(presenter: NewsContract.Presenter) {
        this.presenter = checkNotNull(presenter)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_news, container, false)
        return mView
    }

    /*
    Function saves the top news to storage
    */
    override fun hideNoInternetLayout() {
        layoutNoInternet.visibility = View.GONE
    }

    /*
    Function shows the snackbar with an error
    */
    override fun showError() {
        showSnackBar(resources.getString(R.string.error_occurred))
    }

    var snackBar : Snackbar? = null

    /*
    Function shows the snackbar
    */
    fun showSnackBar(message: String) {

        snackBar = Snackbar.make(scrollLayout, message, Snackbar.LENGTH_LONG)
                .setAction(R.string.retry) {
                    presenter.onRefresh()
                }
        snackBar.let { it?.show() } //show snack bar
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        if(Utils.isDarkTheme())
            imageNoInternet.setImageResource(R.drawable.no_internet_white)

        textRetry.setOnClickListener(this) //set onclick
        cardStar.setOnLikeListener(this) //set onclick
        swipeLayout.setOnRefreshListener(this) //set onclick

        presenter = NewsPresenter(NewsDataManager.getInstance(context!!), this)
        presenter.attachView() //runs on first creation of fragment
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            textRetry.id -> {
                presenter.onRefresh() //run button click
            }
        }
    }

    override fun onRefresh() {
        presenter.onRefresh() //refresh presenter
    }

    override fun onTabClicked(isTabAlreadyClicked : Boolean) {

        if(isTabAlreadyClicked) {
            scrollLayout.scrollTo(0, 0)
            scrollLayout.fling(0)
        } else {
            if(layoutNoInternet.visibility == View.VISIBLE && Utils.isNetworkConnected(context!!)) //if currently visible
                presenter.onRefresh()
            else
                presenter.onResume()
        }
    }

    override fun liked(p0: LikeButton?) {
        presenter.saveArticle(topArticle)
    }

    override fun unLiked(p0: LikeButton?) {
        presenter.removeArticle(topArticle)
    }

    override fun showNoInternetLayout() {
        layoutNoInternet.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        progressBarLayout.visibility = View.GONE
        swipeLayout.isRefreshing = false
    }

    override fun showScrollLayout() {
        swipeLayout.visibility = View.VISIBLE
    }

    override fun showProgressBar() {
        progressBarLayout.visibility = View.VISIBLE
    }

    var newsLayoutManager: androidx.recyclerview.widget.LinearLayoutManager? = null

    var isLastPage: Boolean = false
    var isLoading: Boolean = false

    /*
    Function shows the list of news
    */
    override fun showNews(news: HashMap<Article, Currency?>, savedArticles: ArrayList<Article>) {

        val headerArticle = news.keys.first()

        val templist = HashMap<Article, Currency?>()

        news.forEach { templist[it.key] = it.value }

        templist.remove(headerArticle)

        topArticle = headerArticle

        cardStar.isLiked = savedArticles.any { it.url == topArticle.url }

        if(newsLayoutManager == null) {

            newsLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)

            recyclerViewShimmerNews.layoutManager = newsLayoutManager
            articlesVerticalAdapter = NewsArticlesVerticalAdapter(templist, savedArticles, context, presenter)
            recyclerViewShimmerNews.adapter = articlesVerticalAdapter

        } else {
            articlesVerticalAdapter.swap(templist, savedArticles)
        }
        showTopNewsArticle(headerArticle)
    }

    override fun setIsLoading(b: Boolean) {
        isLoading = false
    }

    /*
    Function shows the top news article
    */
    private fun showTopNewsArticle(article: Article) {

        layoutTopArticle.setOnClickListener {
            val builder = context?.let { context -> Utils.webViewBuilder(context) }
            article.url?.let { url -> builder?.show(url) }
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

    var layoutManager: androidx.recyclerview.widget.LinearLayoutManager? = null

    /*
    Function shows the top 8 changes cards
    */
    override fun showTop8Changes(sortedBy: ArrayList<Currency>, illuminate: Boolean) {
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

                val percentage2DP = Utils.formatPercentage(currency.quote?.uSD?.percentChange24h?.toBigDecimal())

                name.text = currency.name
                percentage.text = percentage2DP

                background.setOnClickListener {
                    context?.let { context -> currency.symbol?.let { symbol -> currency.name?.let { name -> CryptoArgs(symbol, name).launch(context) } } }
                }

                when {
                    percentage2DP.substring(0, 1) == "+" -> {
                        card.setBackgroundResource(R.drawable.border_green_large_round)
                        context?.resources?.getColor(R.color.green)?.let { percentage.setTextColor(it) }

                        if (Math.random() < 0.6) {

                            var color: Int

                            color = if(Utils.isDarkTheme())
                                context?.resources?.getColor(R.color.backgroundblack)!!
                            else
                                context?.resources?.getColor(R.color.backgroundwhite)!!

                            val animGreen = ObjectAnimator.ofInt(background, "backgroundColor", color, context?.resources?.getColor(R.color.green)!!, color) //be careful with color.white and color.transparent as it makes it look shit lol

                            animGreen.duration = 1500
                            animGreen.setEvaluator(ArgbEvaluator())
                            animGreen.repeatMode = ValueAnimator.REVERSE

                            if (illuminate) {
                                animGreen.start()
                            }
                        }
                    }
                    else -> {
                        card.setBackgroundResource(R.drawable.border_red_large_round)
                        context?.resources?.getColor(R.color.red)?.let { percentage.setTextColor(it) }

                        var color = 0

                        if(Utils.isDarkTheme())
                            color = context?.resources?.getColor(R.color.backgroundblack)!!
                        else
                            color = context?.resources?.getColor(R.color.backgroundwhite)!!

                        if (Math.random() < 0.6) {
                            val animRed = ObjectAnimator.ofInt(background, "backgroundColor", color, context?.resources?.getColor(R.color.red)!!, color) //be careful with color.white and color.transparent as it makes it look shit lol

                            animRed.duration = 1500
                            animRed.setEvaluator(ArgbEvaluator())
                            animRed.repeatMode = ValueAnimator.REVERSE

                            if (illuminate) {
                                animRed.start()
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        val TAG = "NewsFragment"
    }
}