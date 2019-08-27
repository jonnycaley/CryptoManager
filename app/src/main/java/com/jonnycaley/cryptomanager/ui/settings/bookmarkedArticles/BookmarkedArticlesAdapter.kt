package com.jonnycaley.cryptomanager.ui.settings.bookmarkedArticles

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.News.Article
import com.jonnycaley.cryptomanager.utils.Utils
import com.like.LikeButton
import com.like.OnLikeListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_news_vertical.view.*


class BookmarkedArticlesAdapter(val articles: ArrayList<Article>?, val context: Context?, val presenter: BookmarkedArticlesContract.Presenter) : androidx.recyclerview.widget.RecyclerView.Adapter<BookmarkedArticlesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_news_vertical, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = articles?.get(position)

        if (item?.originalImageUrl == null) {
            holder.image.visibility = View.GONE
        } else {
            Picasso.with(context)
                    .load(item?.originalImageUrl)
                    .fit()
                    .centerCrop()
                    .into(holder.image)
        }

        holder.likeButton.isLiked = true

        if(Utils.isDarkTheme()){
            holder.likeButton.setLikeDrawable(context?.resources?.getDrawable(R.drawable.bookmark_fill_white))
            holder.likeButton.setUnlikeDrawable(context?.resources?.getDrawable(R.drawable.bookmark_outline_white))
        }

        holder.likeButton.setOnLikeListener(object : OnLikeListener {
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
            context?.let { context ->
                item?.url?.let { url ->

                    val builder = Utils.webViewBuilder(context)
                    builder.show(url)
                }
            }
        }
    }

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return articles?.size ?: 0
    }

    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val image = view.image
        val title = view.title
        val category = view.category
        val date = view.date
        val length = view.length
        val likeButton = view.like_button
    }
}