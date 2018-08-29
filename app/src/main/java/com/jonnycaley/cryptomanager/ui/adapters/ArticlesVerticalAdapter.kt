package com.jonnycaley.cryptomanager.ui.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.News
import com.jonnycaley.cryptomanager.ui.article.ArticleArgs
import com.jonnycaley.cryptomanager.utils.Utils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_news_vertical.view.*
import kotlin.collections.ArrayList

class ArticlesVerticalAdapter(val newsItems: ArrayList<News>?, val context: Context?) : RecyclerView.Adapter<ArticlesVerticalAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_news_vertical, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = newsItems?.get(position)

        if(item?.thumbnail == null){
            holder.image.visibility = View.GONE
        } else {
            Picasso.with(context)
                    .load(item?.thumbnail)
                    .fit()
                    .centerCrop()
                    .into(holder.image)
        }

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
        return newsItems?.size ?: 0
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val image = view.image
        val title = view.title
        val category = view.category
        val date = view.date
        val length = view.length
    }
}