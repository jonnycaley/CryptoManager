package com.jonnycaley.cryptomanager.ui.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.News
import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.SimilarArticle
import com.jonnycaley.cryptomanager.ui.article.ArticleArgs
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_related_news_horizontal.view.*

class SimilarArticlesHorizontalAdapter(private val articles : List<SimilarArticle>?, val context: Context?) : RecyclerView.Adapter<SimilarArticlesHorizontalAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_related_news_horizontal, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = articles?.get(position)

        holder.title.text = item?.title.toString()

        holder.setIsRecyclable(false)

        Picasso.with(context)
                .load(item?.thumbnail)
                .fit()
                .centerCrop()
                .into(holder.image)

        holder.itemView.setOnClickListener {

            val newNews = News()

            newNews.id = item?.id
            newNews.title = item?.title
            newNews.publishedAt = item?.publishedAt
            newNews.sourceDomain = item?.sourceDomain
            newNews.url = item?.url
            newNews.thumbnail = item?.thumbnail

            SimilarArticle()
            ArticleArgs(newNews).launch(context!!)
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
    }

}