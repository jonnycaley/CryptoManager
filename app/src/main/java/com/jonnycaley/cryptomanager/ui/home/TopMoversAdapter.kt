package com.jonnycaley.cryptomanager.ui.home

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Currency
import com.jonnycaley.cryptomanager.ui.currency.CurrencyArgs
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_top_mover.view.*
import java.text.DecimalFormat


class TopMoversAdapter(private val articles: List<Currency>?, val context: Context?) : RecyclerView.Adapter<TopMoversAdapter.ViewHolder>() {

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

        val price = toDecimals(item?.quote?.uSD?.price?.toDouble()!!, 8).toDouble()

        var priceText = ""

        priceText = if(price > 1)
            toDecimals(item?.quote?.uSD?.price?.toDouble()!!, 2)
        else
            "0${toDecimals(item?.quote?.uSD?.price?.toDouble()!!, 6)}"

        if(priceText.indexOf(".") == priceText.length -1)
            priceText += "0"

        holder.price.text = "$$priceText"

        val percentage2DP = String.format("%.2f", item?.quote?.uSD?.percentChange24h)

        when {
            percentage2DP == "0.00" -> {
                holder.percentage.text = "+$percentage2DP%"
                holder.movement.text = "-"
            }
            percentage2DP.toDouble() > 0 -> {
                holder.percentage.text = "+$percentage2DP%"
                holder.percentage.setBackgroundColor(Color.parseColor("#3300F900"))

                holder.movement.text = "▲"
                holder.movement.setTextColor(Color.parseColor("#6600F900"))
            }
            else -> {
                holder.percentage.text = "$percentage2DP%"
                holder.percentage.setBackgroundColor(Color.parseColor("#33FF2600"))

                holder.movement.text = "▼"
                holder.movement.setTextColor(Color.parseColor("#66FF2600"))
            }
        }

        holder.itemView.setOnClickListener {
            CurrencyArgs(item).launch(context!!)

        }
    }

    fun toDecimals(number : Double, decimalPlaces : Int) : String{
        val df = DecimalFormat("#")
        df.setMaximumFractionDigits(decimalPlaces)
        return df.format(number)
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