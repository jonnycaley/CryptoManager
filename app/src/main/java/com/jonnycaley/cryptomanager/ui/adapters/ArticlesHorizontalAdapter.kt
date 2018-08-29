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
import kotlinx.android.synthetic.main.item_news_horizontal.view.*


class ArticlesHorizontalAdapter(private val articles : Array<News>?, val context: Context?) : RecyclerView.Adapter<ArticlesHorizontalAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_news_horizontal, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = articles?.get(position)

        holder.title.text = item?.title.toString()
        holder.category.text = item?.primaryCategory.toString()
        holder.date.text = Utils.getTimeFrom(item?.publishedAt)

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
        val title = view.title
        val category = view.category
        val date = view.date
    }

}