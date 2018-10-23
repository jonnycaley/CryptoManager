package com.jonnycaley.cryptomanager.ui.markets

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.Article
import com.jonnycaley.cryptomanager.ui.article.ArticleArgs
import com.jonnycaley.cryptomanager.utils.Utils
import com.like.LikeButton
import com.like.OnLikeListener
import kotlinx.android.synthetic.main.item_news_horizontal.view.*


class ArticlesHorizontalAdapter(val latestArticles : ArrayList<Article>?, val savedArticles : ArrayList<Article>?, val context: Context?, val presenter: MarketsContract.Presenter) : RecyclerView.Adapter<ArticlesHorizontalAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_news_horizontal, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = latestArticles?.get(position)

        holder.title.text = item?.title.toString()
        holder.category.text = item?.source?.name.toString()
        holder.date.text = Utils.getTimeFrom(item?.publishedAt)

        println(savedArticles?.any { it.url == item?.url }!!)

        holder.likeButton.isLiked = savedArticles?.any { it.url == item?.url }!!

        holder.likeButton.setOnLikeListener(object : OnLikeListener {

            override fun liked(p0: LikeButton?) {
                presenter.saveArticle(item!!)
            }

            override fun unLiked(p0: LikeButton?) {
                presenter.removeArticle(item!!)
            }
        })
        holder.setIsRecyclable(false)

        holder.itemView.setOnClickListener {
            ArticleArgs(item!!).launch(context!!)
        }

    }

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return latestArticles?.size ?: 0
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val title = view.title
        val category = view.source
        val date = view.date
        val likeButton = view.like_button
    }

}