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
import java.util.*

class CurrenciesAdapter(var currencies: ArrayList<Currency>?, var baseFiat : Rate, val context: Context?) : RecyclerView.Adapter<CurrenciesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_currency_list, parent, false))
    }



    fun swap(currencies: ArrayList<Currency>?, baseFiat : Rate)
    {
        this.currencies?.clear()
        this.currencies?.addAll(currencies!!)
        this.baseFiat = baseFiat
        notifyDataSetChanged()
    }

    fun sort(filter : String)
    {
        var tempCurrencies : List<Currency>? = null
        when(filter){
            MarketsFragment.FILTER_RANK_DOWN ->{
                tempCurrencies = currencies?.sortedBy { it.cmcRank }
            }
            MarketsFragment.FILTER_RANK_UP ->{
                tempCurrencies = currencies?.sortedBy { it.cmcRank }?.asReversed()
            }
            MarketsFragment.FILTER_NAME_DOWN ->{
                tempCurrencies = currencies?.sortedBy { it.name }
            }
            MarketsFragment.FILTER_NAME_UP ->{
                tempCurrencies = currencies?.sortedBy { it.name }?.asReversed()
            }
            MarketsFragment.FILTER_PRICE_DOWN ->{
                tempCurrencies = currencies?.sortedBy { it.quote?.uSD?.price }
            }
            MarketsFragment.FILTER_PRICE_UP ->{
                tempCurrencies = currencies?.sortedBy { it.quote?.uSD?.price }?.asReversed()
            }
            MarketsFragment.FILTER_CHANGE_DOWN ->{
                tempCurrencies = currencies?.sortedBy { it.quote?.uSD?.percentChange24h }
            }
            MarketsFragment.FILTER_CHANGE_UP ->{
                tempCurrencies = currencies?.sortedBy { it.quote?.uSD?.percentChange24h }?.asReversed()
            }
            else -> {
                tempCurrencies = currencies?.sortedBy { it.cmcRank }
            }
        }
        this.currencies?.clear()
        this.currencies?.addAll(tempCurrencies!!)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = currencies?.get(position)

        holder.setIsRecyclable(false)

        val price = item?.quote?.uSD?.price?.times(baseFiat.rate!!.toDouble())

        holder.price.text = "${Utils.getFiatSymbol(baseFiat.fiat)}${getPriceText(price)}"

        val percentage2DP = String.format("%.2f", item?.quote?.uSD?.percentChange24h)

        when {
            (percentage2DP == "0.00" || percentage2DP == "nu") -> {
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

        val roundedPrice = Utils.toDecimals(price!!.toBigDecimal(), 8).toDouble()

        var priceText = ""

        priceText = if(roundedPrice > 1)
            Utils.toDecimals(roundedPrice.toBigDecimal(), 2)
        else
            "0${Utils.toDecimals(roundedPrice!!.toBigDecimal(), 6)}"

        if(priceText.indexOf("") == priceText.length -1)
            priceText += "0"

        return priceText
    }

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return currencies?.size ?: 0
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