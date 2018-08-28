package com.jonnycaley.cryptomanager.ui.home

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.News
import com.jonnycaley.cryptomanager.ui.article.ArticleArgs
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_news_list.view.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class NewsAdapter(val newsItems: Array<News>?, val context: Context?) : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_news_list, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = newsItems?.get(position)

        holder.title.text = item?.title.toString()
        holder.category.text = item?.primaryCategory.toString()
        holder.image.alpha = Float.MAX_VALUE
        holder.date.text = getTimeFrom(item?.publishedAt)
        holder.length.text = getReadTime(item?.words)

        holder.setIsRecyclable(false)

        Picasso.with(context)
                .load(item?.thumbnail)
                .fit()
                .centerCrop()
                .into(holder.image)

        holder.itemView.setOnClickListener {
            ArticleArgs(item!!).launch(context!!)
        }
    }

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return newsItems?.size ?: 0
    }

    private fun getReadTime(words: Int?): String {
        return "${Integer.valueOf(Math.ceil((words?.div(130)?.toDouble()!!)).toInt())} min read"
    }

    fun getTimeFrom(date: String?): String {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000'Z'")

        val articleTime: Long
        try {
            articleTime = format.parse(date).time
        } catch (e: ParseException) {
            e.printStackTrace()
            return ""
        }

        val currentTime = Date().time

        val diff = currentTime - articleTime

        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        if (seconds < 60)
            return "$seconds seconds ago • "
        else if (minutes < 60)
            return "$minutes minutes ago • "
        else if (hours < 24)
            return "$hours hours ago • "
        else
            return "$days days ago • "

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