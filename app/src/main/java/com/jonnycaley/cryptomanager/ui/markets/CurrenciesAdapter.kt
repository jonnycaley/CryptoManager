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
import kotlin.collections.ArrayList

class CurrenciesAdapter(var currencies: ArrayList<Currency>, var baseFiat: Rate, val context: Context?, var timeFrame: String) : RecyclerView.Adapter<CurrenciesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_currency_list, parent, false))
    }


    fun swap(currencies: ArrayList<Currency>, baseFiat: Rate) {

        this.currencies.clear()
        this.currencies.addAll(currencies)
        this.baseFiat = baseFiat

        notifyDataSetChanged()

    }

    fun sort(filter: String) {

        //TODO: IDEALLY THIS NEEDS TO BE DONE ON COMPUTATION THREAD (MAYBE IN PRESENTER) BUT LOOOOOOOOONG

        var tempCurrencies : List<Currency>? = null

        when (filter) {
            MarketsFragment.FILTER_RANK_UP -> {
                tempCurrencies = currencies.sortedBy { it.cmcRank }.asReversed()
            }
            MarketsFragment.FILTER_NAME_DOWN -> {
                tempCurrencies = currencies.sortedBy { it.name }
            }
            MarketsFragment.FILTER_NAME_UP -> {
                tempCurrencies = currencies.sortedBy { it.name }.asReversed()
            }
            MarketsFragment.FILTER_PRICE_DOWN -> {
                tempCurrencies = currencies.sortedBy { it.quote?.uSD?.price }.asReversed()
            }
            MarketsFragment.FILTER_PRICE_UP -> {
                tempCurrencies = currencies.sortedBy { it.quote?.uSD?.price }
            }
            MarketsFragment.FILTER_CHANGE_DOWN -> {
                when (timeFrame) {
                    MarketsFragment.TIMEFRAME_1H -> {
                        tempCurrencies = currencies.sortedBy { it.quote?.uSD?.percentChange1h }.asReversed()
                    }
                    MarketsFragment.TIMEFRAME_1D -> {
                        tempCurrencies = currencies.sortedBy { it.quote?.uSD?.percentChange24h }.asReversed()
                    }
                    MarketsFragment.TIMEFRAME_1W -> {
                        tempCurrencies = currencies.sortedBy { it.quote?.uSD?.percentChange7d }.asReversed()
                    }
                }
            }
            MarketsFragment.FILTER_CHANGE_UP -> {
                when (timeFrame) {
                    MarketsFragment.TIMEFRAME_1H -> {
                        tempCurrencies = currencies.sortedBy { it.quote?.uSD?.percentChange1h }
                    }
                    MarketsFragment.TIMEFRAME_1D -> {
                        tempCurrencies = currencies.sortedBy { it.quote?.uSD?.percentChange24h }
                    }
                    MarketsFragment.TIMEFRAME_1W -> {
                        tempCurrencies = currencies.sortedBy { it.quote?.uSD?.percentChange7d }
                    }
                }
            }
            else -> {
                tempCurrencies = currencies.sortedBy { it.cmcRank }
            }
        }
        this.currencies.clear()
        tempCurrencies?.let { this.currencies.addAll(it) }
        notifyDataSetChanged()

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = currencies.get(position)

        holder.setIsRecyclable(false)

        val price = item.quote?.uSD?.price?.times(baseFiat.rate?.toDouble() ?: 1.toDouble())

        holder.price.text = "${Utils.getPriceTextAbs(price, Utils.getFiatSymbol(baseFiat.fiat))}"

        var percentage2DP = ""


        when (timeFrame) {
            MarketsFragment.TIMEFRAME_1H -> {
                if (item.quote?.uSD?.percentChange1h == null)
                    percentage2DP = "0.00"
                else
                    percentage2DP = String.format("%.2f", item?.quote?.uSD?.percentChange1h)
            }
            MarketsFragment.TIMEFRAME_1D -> {
                if (item.quote?.uSD?.percentChange24h == null)
                    percentage2DP = "0.00"
                else
                    percentage2DP = String.format("%.2f", item?.quote?.uSD?.percentChange24h)
            }
            MarketsFragment.TIMEFRAME_1W -> {
                if (item.quote?.uSD?.percentChange7d == null)
                    percentage2DP = "0.00"
                else
                    percentage2DP = String.format("%.2f", item?.quote?.uSD?.percentChange7d)
            }
        }

        when {
            percentage2DP.toDouble() > 0 -> {
                holder.percentage.text = "+$percentage2DP%"
                context?.resources?.getColor(R.color.green)?.let { holder.percentage.setTextColor(it) }
//                holder.percentage.setBackgroundColor(context?.resources?.getColor(R.color.stock_green)!!)

//                holder.movement.text = "▲"
                context?.resources?.getColor(R.color.arrow_green)?.let { holder.movement.setTextColor(it) }
            }
            percentage2DP.toDouble() < 0 -> {
                holder.percentage.text = "$percentage2DP%"
                context?.resources?.getColor(R.color.red)?.let { holder.percentage.setTextColor(it) }
//                holder.percentage.setBackgroundColor(context?.resources?.getColor(R.color.stock_red)!!)

//                holder.movement.text = "▼"
                context?.resources?.getColor(R.color.arrow_red)?.let { holder.movement.setTextColor(it) }
            }
            else -> {
                holder.percentage.text = "0.00%"
            }
        }

        holder.rank.text = item.cmcRank.toString()
        holder.name.text = item.name.toString()
        holder.symbol.text = " (${item?.symbol.toString()})"

        holder.setIsRecyclable(false)

        holder.itemView.setOnClickListener {
            item.symbol?.let { it1 -> CryptoArgs(it1).launch(context!!) }
        }
    }

    override fun getItemCount(): Int {
        return currencies.size ?: 0
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val rank = view.rank
        val name = view.name
        val symbol = view.symbol
        val price = view.price
        val movement = view.movement
        val percentage = view.percentage
    }
}