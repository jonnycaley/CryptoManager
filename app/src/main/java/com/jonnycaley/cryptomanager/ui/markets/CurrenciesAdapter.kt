package com.jonnycaley.cryptomanager.ui.markets

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Currency
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.Rate
import com.jonnycaley.cryptomanager.ui.crypto.CryptoArgs
import com.jonnycaley.cryptomanager.utils.Utils
import kotlinx.android.synthetic.main.item_currency_list.view.*

class CurrenciesAdapter(val newsItems: ArrayList<Currency>?, val baseFiat : Rate, val context: Context?) : RecyclerView.Adapter<CurrenciesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_currency_list, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = newsItems?.get(position)

        val price = item?.quote?.uSD?.price?.times(baseFiat.rate!!)

        holder.price.text = "${Utils.getFiatSymbol(baseFiat.fiat)}${getPriceText(price)}"

        val percentage2DP = String.format("%.2f", item?.quote?.uSD?.percentChange24h)

        when {
            percentage2DP == "0.00" -> {
                holder.percentage.text = "+$percentage2DP%"
//                holder.movement.text = "-"
            }
            percentage2DP.toDouble() > 0 -> {
                holder.percentage.text = "+$percentage2DP%"
                holder.percentage.setTextColor(context?.resources?.getColor(R.color.green)!!)
//                holder.percentage.setBackgroundColor(context?.resources?.getColor(R.color.stock_green)!!)

//                holder.movement.text = "▲"
                holder.movement.setTextColor(context?.resources?.getColor(R.color.arrow_green)!!)
            }
            else -> {
                holder.percentage.text = "$percentage2DP%"
                holder.percentage.setTextColor(context?.resources?.getColor(R.color.red)!!)
//                holder.percentage.setBackgroundColor(context?.resources?.getColor(R.color.stock_red)!!)

//                holder.movement.text = "▼"
                holder.movement.setTextColor(context?.resources?.getColor(R.color.arrow_red)!!)
            }
        }

        holder.rank.text = item?.cmcRank.toString()
        holder.name.text = item?.name.toString()
        holder.symbol.text = " (${item?.symbol.toString()})"

        holder.setIsRecyclable(false)

        holder.itemView.setOnClickListener {
            CryptoArgs(item?.symbol!!).launch(context!!)
        }
    }

    private fun getPriceText(price: Double?): CharSequence? {

        val roundedPrice = Utils.toDecimals(price!!, 8).toDouble()

        var priceText = ""

        priceText = if(roundedPrice > 1)
            Utils.toDecimals(roundedPrice, 2)
        else
            "0${Utils.toDecimals(roundedPrice!!, 6)}"

        if(priceText.indexOf("") == priceText.length -1)
            priceText += "0"

        return priceText
    }

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return newsItems?.size ?: 0
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val rank = view.rank
        val name = view.name
        val symbol = view.symbol
        val price = view.price
        val movement = view.movement
        val percentage = view.percentage
    }
}