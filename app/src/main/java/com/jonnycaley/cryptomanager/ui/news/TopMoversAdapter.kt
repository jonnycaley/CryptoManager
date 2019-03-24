package com.jonnycaley.cryptomanager.ui.news

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Currency
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.Rate
import com.jonnycaley.cryptomanager.ui.crypto.CryptoArgs
import com.jonnycaley.cryptomanager.utils.Utils
import kotlinx.android.synthetic.main.item_top_mover.view.*


class TopMoversAdapter(var articles: ArrayList<Currency>, var baseFiat : Rate, var context: Context?) : androidx.recyclerview.widget.RecyclerView.Adapter<TopMoversAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_top_mover, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = articles.get(position)

        holder.setIsRecyclable(false)

        holder.name.text = item.name.toString()

//        Picasso.with(context)
//                .load(item?.thumbnail)
//                .fit()
//                .centerCrop()
//                .into(holder.image)

        val price = baseFiat.rate?.toDouble()?.let { item.quote?.uSD?.price?.toDouble()?.times(it) }

        val symbol = Utils.getFiatSymbol(baseFiat.fiat)

        val priceText = price?.toBigDecimal()?.let { Utils.getPriceTextAbs(it.toDouble(), symbol) }

        holder.price.text = "${Utils.getFiatSymbol(baseFiat.fiat)}$priceText"

        val percentage2DP = Utils.formatPercentage(item.quote?.uSD?.percentChange24h?.toBigDecimal())

        val animRed = ObjectAnimator.ofInt(holder.layout, "backgroundColor", Color.WHITE, Color.RED,
                Color.WHITE)
        val animGreen = ObjectAnimator.ofInt(holder.layout, "backgroundColor", Color.WHITE, Color.GREEN,
                Color.WHITE)

        animRed.duration = 1000
        animRed.setEvaluator(ArgbEvaluator())
        animRed.repeatMode = ValueAnimator.REVERSE

        animGreen.duration = 1000
        animGreen.setEvaluator(ArgbEvaluator())
        animGreen.repeatMode = ValueAnimator.REVERSE

        when {
            percentage2DP.substring(0,1) == "$" -> {
                holder.percentage.text = "$percentage2DP%"
//                holder.movement.text = "-"
            }
            percentage2DP.substring(0,1) == "+" -> {
//                holder.percentage.setBackgroundColor(Color.parseColor("#3300F900"))
                context?.resources?.getColor(R.color.green)?.let { holder.percentage.setTextColor(it) }
                animGreen.start()
//                holder.movement.text = "▲"
                holder.movement.setTextColor(Color.parseColor("#6600F900"))
            }
            else -> {
//                holder.percentage.setBackgroundColor(Color.parseColor("#33FF2600"))
                context?.resources?.getColor(R.color.red)?.let { holder.percentage.setTextColor(it) }
                animRed.start()
//                holder.movement.text = "▼"
//                holder.movement.setTextColor(Color.parseColor("#66FF2600"))
            }
        }

        holder.percentage.text = "$percentage2DP"

        holder.itemView.setOnClickListener {
            context?.let { context -> item.symbol?.let { symbol -> item.name?.let { name -> CryptoArgs(symbol, name).launch(context) } } }

        }
    }

    override fun getItemId(position: Int): Long {
        //Return the stable ID for the item at position
        return articles[position].id ?: 0
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return articles.size
    }

    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val name = view.name
        val price = view.price
        val movement = view.movement
        val percentage = view.percentage
        val layout = view.layout

    }
}