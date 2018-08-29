package com.jonnycaley.cryptomanager.ui.markets

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Currency
import com.jonnycaley.cryptomanager.ui.article.ArticleArgs
import com.jonnycaley.cryptomanager.utils.Utils
import com.squareup.picasso.Picasso

class NewsAdapter(val newsItems: ArrayList<Currency>?, val context: Context?) : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_currency_list, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = newsItems?.get(position)

//        holder.title.text = item?.title.toString()
//        holder.category.text = item?.primaryCategory.toString()
//        holder.image.alpha = Float.MAX_VALUE
//        holder.date.text = Utils.getTimeFrom(item?.publishedAt)
//        holder.length.text = Utils.getReadTime(item?.words)

        holder.setIsRecyclable(false)

//        holder.itemView.setOnClickListener {
//            ArticleArgs(item!!).launch(context!!)
//        }
    }

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return newsItems?.size ?: 0
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
//        val image = view.image
//        val title = view.title
//        val category = view.category
//        val date = view.date
//        val length = view.length
    }
}