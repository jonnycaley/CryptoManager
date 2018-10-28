package com.jonnycaley.cryptomanager.ui.home

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Currency
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.Rate
import com.jonnycaley.cryptomanager.ui.crypto.CryptoArgs
import com.jonnycaley.cryptomanager.utils.Utils
import kotlinx.android.synthetic.main.item_top_mover.view.*


class TopMoversAdapter(var articles: ArrayList<Currency>?, var baseFiat : Rate, var context: Context?) : RecyclerView.Adapter<TopMoversAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_top_mover, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = articles?.get(position)

        holder.setIsRecyclable(false)

        holder.name.text = item?.name.toString()

//        Picasso.with(context)
//                .load(item?.thumbnail)
//                .fit()
//                .centerCrop()
//                .into(holder.image)

        val price = item?.quote?.uSD?.price?.toDouble()?.times(baseFiat.rate!!.toDouble())

        val priceText = Utils.formatPrice(price!!.toBigDecimal())

        holder.price.text = "${Utils.getFiatSymbol(baseFiat.fiat)}$priceText"

        val percentage2DP = Utils.formatPercentage(item?.quote?.uSD?.percentChange24h?.toBigDecimal())

        when {
            percentage2DP.substring(0,1) == "$" -> {
                holder.percentage.text = "$percentage2DP%"
//                holder.movement.text = "-"
            }
            percentage2DP.substring(0,1) == "+" -> {
//                holder.percentage.setBackgroundColor(Color.parseColor("#3300F900"))
                holder.percentage.setTextColor(context?.resources?.getColor(R.color.green)!!)

//                holder.movement.text = "▲"
                holder.movement.setTextColor(Color.parseColor("#6600F900"))
            }
            else -> {
//                holder.percentage.setBackgroundColor(Color.parseColor("#33FF2600"))
                holder.percentage.setTextColor(context?.resources?.getColor(R.color.red)!!)

//                holder.movement.text = "▼"
//                holder.movement.setTextColor(Color.parseColor("#66FF2600"))
            }
        }

        holder.percentage.text = "$percentage2DP"

        holder.itemView.setOnClickListener {
            CryptoArgs(item?.symbol!!).launch(context!!)

        }
    }

    override fun getItemId(position: Int): Long {
        //Return the stable ID for the item at position
        return articles?.get(position)?.id!!
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return articles?.size ?: 0
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val name = view.name
        val price = view.price
        val movement = view.movement
        val percentage = view.percentage

    }
}