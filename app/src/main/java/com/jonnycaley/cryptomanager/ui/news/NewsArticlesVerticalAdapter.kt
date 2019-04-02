package com.jonnycaley.cryptomanager.ui.news

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Currency
import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.News.Article
import com.jonnycaley.cryptomanager.ui.article.ArticleArgs
import com.jonnycaley.cryptomanager.ui.crypto.CryptoArgs
import com.jonnycaley.cryptomanager.utils.Utils
import com.like.LikeButton
import com.like.OnLikeListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_news_vertical.view.*

class NewsArticlesVerticalAdapter(var newsItems: HashMap<Article, Currency?>, var savedArticles: ArrayList<Article>, var context: Context?, var presenter: NewsContract.Presenter?) : androidx.recyclerview.widget.RecyclerView.Adapter<NewsArticlesVerticalAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_news_vertical, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val article = newsItems.keys.toTypedArray()[position]
        val relatedCrypto = newsItems.values.toTypedArray()[position]

//        if(((position + 1 ) % 3 == 0 ) && (article.originalImageUrl != null)){
//
//            holder.image.visibility = View.GONE
//            holder.imageEnlarged.visibility = View.VISIBLE
//
//            Picasso.with(context)
//                    .load(article.originalImageUrl)
//                    .fit()
//                    .centerCrop()
//                    .into(holder.imageEnlarged)
//
//        } else {
            holder.imageEnlarged.visibility = View.GONE
            holder.image.visibility = View.VISIBLE

            if (article.thumbnail == null) {
                holder.image.visibility = View.GONE
                holder.cardImage.visibility = View.GONE
            } else {
                Picasso.with(context)
                        .load(article.thumbnail)
                        .placeholder(R.drawable.rounded_card_image)
                        .fit()
                        .centerCrop()
                        .into(holder.image)
            }
//        }

        holder.title.text = article.title.toString()
        holder.category.text = article.primaryCategory.toString()
        holder.image.alpha = Float.MAX_VALUE
        if (article.publishedAt != null)
            holder.date.text = Utils.getTimeFrom(article.publishedAt)
        holder.length.text = Utils.getReadTime(article.words)

        if(Utils.isDarkTheme()) {
            holder.likeButton.setLikeDrawable(context?.resources?.getDrawable(R.drawable.bookmark_fill_white))
            holder.likeButton.setUnlikeDrawable(context?.resources?.getDrawable(R.drawable.bookmark_outline_white))
        }

        holder.likeButton.isLiked = savedArticles.any { it.url == article.url }

        if (relatedCrypto != null) {

            val percentage2DP = Utils.formatPercentage(relatedCrypto.quote?.uSD?.percentChange24h?.toBigDecimal())
//                val animRed = ObjectAnimator.ofInt(holder.layoutStockRed, "backgroundColor", Color.WHITE, Color.RED,
//                        Color.WHITE)
//                val animGreen = ObjectAnimator.ofInt(holder.layoutStockGreen, "backgroundColor", Color.WHITE, Color.GREEN,
//                        Color.WHITE)
//
//                animRed.duration = 1000
//                animRed.setEvaluator(ArgbEvaluator())
//                animRed.repeatMode = ValueAnimator.REVERSE
//
//                animGreen.duration = 1000
//                animGreen.setEvaluator(ArgbEvaluator())
//                animGreen.repeatMode = ValueAnimator.REVERSE
            when {
                percentage2DP.substring(0, 1) == "+" -> {
//                holder.percentage.setBackgroundColor(Color.parseColor("#3300F900"))

                    holder.layoutStockGreen.visibility = View.VISIBLE
                    holder.stockNameGreen.text = relatedCrypto.symbol?.toUpperCase()
                    holder.stockPercentageGreen.text = percentage2DP
//                        animGreen.start()//does not work as changes background permanently :(
//                holder.movement.text = "▲"
                }
                else -> {
//                holder.percentage.setBackgroundColor(Color.parseColor("#33FF2600"))
                    holder.layoutStockRed.visibility = View.VISIBLE
                    holder.stockNameRed.text = relatedCrypto.symbol?.toUpperCase()
                    holder.stockPercentageRed.text = percentage2DP

//                        animRed.start()
//                holder.movement.text = "▼"
//                holder.movement.setTextColor(Color.parseColor("#66FF2600"))
                }
            }

            holder.layoutStockGreen.setOnClickListener {
                context?.let { context -> relatedCrypto.symbol?.let { symbol -> relatedCrypto.name?.let { name -> CryptoArgs(symbol, name).launch(context) } } }
            }
            holder.layoutStockRed.setOnClickListener {
                context?.let { context -> relatedCrypto.symbol?.let { symbol -> relatedCrypto.name?.let { name -> CryptoArgs(symbol, name).launch(context) } } }
            }
    }

//        topcurrencies.forEach { crypto ->
//            var bool = false
//            if((item?.title?.toUpperCase()?.contains(crypto.name!!.toUpperCase())!!   ||   item.title?.toUpperCase()?.contains(crypto.symbol!!.toUpperCase())!! )
//                    && (item.coins!!.any { it.tradingSymbol?.toUpperCase() == crypto.symbol?.toUpperCase() })){
//
//                if(position != 0){
//                    if((!((currencies?.get(position -1)?.title?.toUpperCase()?.contains(crypto.name!!.toUpperCase())!!   ||   currencies?.get(position -1)?.title?.toUpperCase()?.contains(crypto.symbol!!.toUpperCase())!! )
//                            && (currencies?.get(position -1)?.coins!!.any { it.tradingSymbol?.toUpperCase() == crypto.symbol?.toUpperCase() })))){
//
//                        bool = true
//
//                    }
//
//                } else {
//
//                    bool = true
//                }
//            }
//
//            if(bool){
//
//                val percentage2DP = Utils.formatPercentage(crypto.quote?.uSD?.percentChange24h?.toBigDecimal())
//
////                val animRed = ObjectAnimator.ofInt(holder.layoutStockRed, "backgroundColor", Color.WHITE, Color.RED,
////                        Color.WHITE)
////                val animGreen = ObjectAnimator.ofInt(holder.layoutStockGreen, "backgroundColor", Color.WHITE, Color.GREEN,
////                        Color.WHITE)
////
////                animRed.duration = 1000
////                animRed.setEvaluator(ArgbEvaluator())
////                animRed.repeatMode = ValueAnimator.REVERSE
////
////                animGreen.duration = 1000
////                animGreen.setEvaluator(ArgbEvaluator())
////                animGreen.repeatMode = ValueAnimator.REVERSE
//
//                when {
//                    percentage2DP.substring(0,1) == "+" -> {
////                holder.percentage.setBackgroundColor(Color.parseColor("#3300F900"))
//
//                        holder.layoutStockGreen.visibility = View.VISIBLE
//                        holder.stockNameGreen.text = crypto.symbol?.toUpperCase()
//                        holder.stockPercentageGreen.text = percentage2DP
//
////                        animGreen.start()//does not work as changes background permanently :(
////                holder.movement.text = "▲"
//                    }
//                    else -> {
////                holder.percentage.setBackgroundColor(Color.parseColor("#33FF2600"))
//
//                        holder.layoutStockRed.visibility = View.VISIBLE
//                        holder.stockNameRed.text = crypto.symbol?.toUpperCase()
//                        holder.stockPercentageRed.text = percentage2DP
//
////                        animRed.start()
////                holder.movement.text = "▼"
////                holder.movement.setTextColor(Color.parseColor("#66FF2600"))
//                    }
//                }
//
//                holder.layoutStockGreen.setOnClickListener{
//                    CryptoArgs(crypto.symbol!!).launch(context!!)
//                }
//                holder.layoutStockRed.setOnClickListener{
//                    CryptoArgs(crypto.symbol!!).launch(context!!)
//                }
//            }
//        }

        holder.likeButton.setOnLikeListener(object : OnLikeListener {

            override fun liked(p0: LikeButton?) {
                presenter?.saveArticle(article)
            }

            override fun unLiked(p0: LikeButton?) {
                presenter?.removeArticle(article)
            }
        })

        holder.setIsRecyclable(false)

        holder.itemView.setOnClickListener {
            val builder = context?.let { context -> Utils.webViewBuilder(context) }
            article.url?.let { url -> builder?.show(url) }

        }
    }

    fun swap(newsItems: HashMap<Article, Currency?>, savedArticles: ArrayList<Article>) {

        val newUrls = ArrayList<String>()
        newUrls.clear()
        savedArticles.sortedBy { it.url }.forEach { newUrls.add(it.url.toString()) }

        val oldUrls = ArrayList<String>()
        oldUrls.clear()
        this.savedArticles.sortedBy { it.url }.forEach { oldUrls.add(it.url.toString()) }

        if((newsItems != this.newsItems) || (newUrls.sorted() != oldUrls.sorted())) {

            Log.i(TAG, "Updating")

            this.newsItems.clear()
            this.savedArticles.clear()
            this.newsItems.putAll(newsItems)
            this.savedArticles.addAll(savedArticles)

            this.notifyDataSetChanged()
        } else {
            Log.i(TAG, "Not updating")
        }
    }

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return newsItems.size
    }

    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to

        val cardImage = view.card_image
        val image = view.image
        val title = view.title
        val category = view.category
        val date = view.date
        val length = view.length
        val likeButton = view.like_button

        val layoutStockGreen = view.layout_stock_green
        val stockNameGreen = view.stock_name_green
        val stockPercentageGreen = view.stock_percentage_green

        val layoutStockRed = view.layout_stock_red
        val stockNameRed = view.stock_name_red
        val stockPercentageRed = view.stock_percentage_red

        val imageEnlarged = view.image_enlarged

    }

    companion object {
        val TAG = "HomeArticlesAdapter"
    }
}