package com.jonnycaley.cryptomanager.ui.settings.savedArticles

import android.content.Context
import android.support.v7.widget.RecyclerView
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


class SavedArticlesAdapter(val articles: ArrayList<Article>?, val context: Context?, val presenter: SavedArticlesContract.Presenter) : RecyclerView.Adapter<SavedArticlesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_news_vertical, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = articles?.get(position)

        if(item?.thumbnail == null){
            holder.image.visibility = View.GONE
        } else {
            Picasso.with(context)
                    .load(item?.thumbnail)
                    .fit()
                    .centerCrop()
                    .into(holder.image)
        }

        holder.likeButton.isLiked = true

        holder.likeButton.setOnLikeListener(object : OnLikeListener{
            override fun liked(p0: LikeButton?) {

            }

            override fun unLiked(p0: LikeButton?) {
                presenter.removeArticle(articles, item!!)
            }

        })

        holder.title.text = item?.title.toString()
        holder.category.text = item?.primaryCategory.toString()
        holder.image.alpha = Float.MAX_VALUE
        holder.date.text = Utils.getTimeFrom(item?.publishedAt)
        holder.length.text = Utils.getReadTime(item?.words)

        holder.setIsRecyclable(false)

        holder.itemView.setOnClickListener {
            ArticleArgs(item!!).launch(context!!)
        }
    }

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return articles?.size ?: 0
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val image = view.image
        val title = view.title
        val category = view.category
        val date = view.date
        val length = view.length
        val likeButton = view.like_button
    }
}