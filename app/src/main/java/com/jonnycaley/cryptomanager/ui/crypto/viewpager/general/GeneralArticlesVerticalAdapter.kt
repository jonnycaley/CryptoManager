package com.jonnycaley.cryptomanager.ui.crypto.viewpager.general

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.News.Article
import com.jonnycaley.cryptomanager.ui.article.ArticleArgs
import com.jonnycaley.cryptomanager.utils.Utils
import com.like.LikeButton
import com.like.OnLikeListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_news_vertical.view.*

class GeneralArticlesVerticalAdapter(var newsItems: ArrayList<Article>, var savedArticles: ArrayList<Article>, val context: Context?, val generalPresenter : GeneralContract.Presenter?) : androidx.recyclerview.widget.RecyclerView.Adapter<GeneralArticlesVerticalAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_news_vertical, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = newsItems[position]

        if(item.originalImageUrl == null){
            holder.image.visibility = View.GONE
        } else {
            Picasso.with(context)
                    .load(item.originalImageUrl)
                    .fit()
                    .centerCrop()
                    .into(holder.image)
        }

        holder.title.text = item.title.toString()
        holder.category.text = item.primaryCategory.toString()
        holder.image.alpha = Float.MAX_VALUE
        holder.date.text = Utils.getTimeFrom(item.publishedAt)
        holder.length.text = Utils.getReadTime(item.words)

        holder.likeButton.isLiked = savedArticles.any { it.url == item.url }

        holder.likeButton.setOnLikeListener(object : OnLikeListener {

            override fun liked(p0: LikeButton?) {
                Log.i(TAG, "liked")
                item.let { generalPresenter?.saveArticle(it) }
            }

            override fun unLiked(p0: LikeButton?) {
                Log.i(TAG, "unLiked")
                item.let { generalPresenter?.removeArticle(it) }
            }
        })

        if(Utils.isDarkTheme()) {
            holder.likeButton.setLikeDrawable(context?.resources?.getDrawable(R.drawable.bookmark_fill_white))
            holder.likeButton.setUnlikeDrawable(context?.resources?.getDrawable(R.drawable.bookmark_outline_white))
        }

        holder.setIsRecyclable(false)

        holder.itemView.setOnClickListener {
            val builder = context?.let { context -> Utils.webViewBuilder(context) }
            item.url?.let { url -> builder?.show(url) }
        }
    }

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return newsItems.size
    }

    class ViewHolder (view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val image = view.image
        val title = view.title
        val category = view.category
        val date = view.date
        val length = view.length
        val likeButton = view.like_button
    }

    companion object {
        val TAG = this::class.java.simpleName
    }
}