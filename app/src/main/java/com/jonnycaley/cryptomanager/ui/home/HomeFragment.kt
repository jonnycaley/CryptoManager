package com.jonnycaley.cryptomanager.ui.home

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cooltechworks.views.shimmer.ShimmerRecyclerView
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.News
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.*
import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Currency
import com.jonnycaley.cryptomanager.ui.article.ArticleArgs
import com.jonnycaley.cryptomanager.utils.Utils
import com.like.LikeButton
import com.squareup.picasso.Picasso
import kotlin.collections.ArrayList

class HomeFragment : Fragment(), HomeContract.View, View.OnClickListener {

    lateinit var mView: View

    private lateinit var presenter: HomeContract.Presenter

    lateinit var articlesVerticalAdapter: ArticlesVerticalAdapter
    lateinit var topMoversAdapter: TopMoversAdapter

    lateinit var topArticle : News

    val scrollLayout by lazy { mView.findViewById<ScrollView>(R.id.scroll_layout) }
    val progressBarLayout by lazy { mView.findViewById<ConstraintLayout>(R.id.progress_bar_layout) }

    val recyclerViewShimmerNews by lazy { mView.findViewById<ShimmerRecyclerView>(R.id.shimmer_recycler_view) }
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

        recyclerViewShimmerNews.showShimmerAdapter()
        cardStar.setOnClickListener(this)
        presenter = HomePresenter(HomeDataManager.getInstance(context!!), this)
        presenter.attachView()
    }

    override fun onClick(v: View?) {
        when(v?.id){
            cardStar.id -> {
                if(cardStar.isLiked) {
                    presenter.removeArticle(topArticle)
                    cardStar.animate()
                }
                else {
                    presenter.saveArticle(topArticle)
                    cardStar.animate()
                }
            }
         }
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

    override fun showNews(news: Array<News>) {

        val arrayList = ArrayList<News>()

        news.forEach { arrayList.add(it) }

        val headerArticle = arrayList.filter { it.thumbnail != null }[0]

        arrayList.remove(headerArticle)

        topArticle = headerArticle

        showTopNewsArticle(headerArticle)

        val mLayoutManager = LinearLayoutManager(context)
        recyclerViewShimmerNews.layoutManager = mLayoutManager
        articlesVerticalAdapter = ArticlesVerticalAdapter(arrayList, context)
        recyclerViewShimmerNews.adapter = articlesVerticalAdapter

    }

    private fun showTopNewsArticle(article: News) {

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
        cardStar.isLiked = false

    }

    override fun showTop100Changes(sortedBy: List<Currency>?) {

        val arrayList = ArrayList<Currency>()

        sortedBy?.forEach { arrayList.add(it) }

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        recyclerViewTopMovers.layoutManager = layoutManager
        topMoversAdapter = TopMoversAdapter(arrayList, context)
        recyclerViewTopMovers.adapter = topMoversAdapter
    }

    fun newInstance(headerStr: String): HomeFragment {
        val fragmentDemo = HomeFragment()
        val args = Bundle()
        args.putString("headerStr", headerStr)
        fragmentDemo.arguments = args
        return fragmentDemo
    }
}