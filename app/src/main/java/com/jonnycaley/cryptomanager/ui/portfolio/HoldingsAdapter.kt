package com.jonnycaley.cryptomanager.ui.portfolio

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.AllCurrencies.Currencies
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.MultiPrice.Price
import com.jonnycaley.cryptomanager.data.model.DataBase.Holding
import com.jonnycaley.cryptomanager.data.model.DataBase.Variables
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.Rate
import com.jonnycaley.cryptomanager.ui.crypto.CryptoArgs
import com.jonnycaley.cryptomanager.ui.fiat.FiatArgs
import com.jonnycaley.cryptomanager.utils.CircleTransform
import com.jonnycaley.cryptomanager.utils.Utils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_holding.view.*

class HoldingsAdapter(val holdings: ArrayList<Holding>?, val prices: ArrayList<Price>, val baseFiat : Rate, val chosenCurrency : String, val context: Context?) : RecyclerView.Adapter<HoldingsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_holding, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val holding = holdings?.get(position)

        holder.setIsRecyclable(false)

        var symbol = Utils.getFiatSymbol(baseFiat.fiat)

        var price = prices.filter { it.symbol?.toLowerCase() == holding?.symbol?.toLowerCase() }[0].prices?.uSD?.times(baseFiat.rate!!)?.toBigDecimal()
        val cost = holding?.costUsd?.times(baseFiat.rate!!)
        var value = price?.times(holding?.quantity!!)
        var change = value?.minus(cost?.toBigDecimal()!!)

        when(chosenCurrency){
            PortfolioFragment.CURRENCY_BTC -> {
                symbol = "BTC"

                val costBtcHistorical = holding?.costBtc?.toBigDecimal()
                val costBtcNow = holding?.quantity!! * prices.first { it.symbol?.toUpperCase() == holding.symbol.toUpperCase() }.prices?.uSD?.toBigDecimal()!! / prices.first { it.symbol?.toUpperCase() == "BTC" }.prices?.uSD!!.toBigDecimal()!!
                change = costBtcNow - costBtcHistorical!!

                price = price?.div(prices.first { it.symbol?.toUpperCase() == "BTC" }.prices?.uSD!!.toBigDecimal()!!)
                value = value?.div(prices.first { it.symbol?.toUpperCase() == "BTC" }.prices?.uSD!!.toBigDecimal()!!)
            }
            PortfolioFragment.CURRENCY_ETH -> {
                symbol = "ETH"

                val costBtcHistorical = holding?.costEth?.toBigDecimal()
                val costBtcNow = holding?.quantity!! * prices.first { it.symbol?.toUpperCase() == holding.symbol.toUpperCase() }.prices?.uSD?.toBigDecimal()!! / prices.first { it.symbol?.toUpperCase() == "ETH" }.prices?.uSD?.toBigDecimal()!!
                change = costBtcNow - costBtcHistorical!!

                price = price?.div(prices.first { it.symbol?.toUpperCase() == "ETH" }.prices?.uSD?.toBigDecimal()!!)
                value = value?.div(prices.first { it.symbol?.toUpperCase() == "ETH" }.prices?.uSD?.toBigDecimal()!!)
            }
        }

        if(holding?.imageUrl != null && !holding.imageUrl?.contains("null")!!) {
            Picasso.with(context)
                    .load(holding.imageUrl)
                    .fit()
                    .centerCrop()
                    .transform(CircleTransform())
                    .placeholder(R.drawable.circle)
                    .into(holder.image)
        }
        else{
            holder.image.visibility = View.GONE
        }

        if (change != null && change < 0.toBigDecimal()) {
            holder.change.setTextColor(context?.resources?.getColor(R.color.red)!!)
            holder.change.text = "-$symbol${Utils.formatPrice(change.toDouble()).substring(1)}"
        } else {
            holder.change.setTextColor(context?.resources?.getColor(R.color.green)!!)
            holder.change.text = "$symbol${Utils.formatPrice(change?.toDouble()!!)}"
        }

        holder.holding.text = holding?.quantity.toString()
        holder.currency.text = holding?.symbol.toString()
        holder.symbol.text = holding?.symbol.toString()
        holder.value.text = "($symbol${Utils.formatPrice(value?.toDouble()!!)})"
        holder.price.text = "$symbol${Utils.formatPrice(price!!.toDouble())}"

        if(holding?.type ==  Variables.Transaction.Type.fiat) {
            holder.textFiat.visibility = View.VISIBLE
            holder.price.visibility = View.GONE
            holder.change.text = "$symbol${Utils.formatPrice(value.toDouble())}"
            holder.change.setTextColor(context.resources.getColor(R.color.text_grey))
            holder.symbol.visibility = View.GONE
            holder.value.visibility = View.GONE
            holder.holding.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            println("HolidngType: ${holding?.type}")
            if(holding?.type ==  Variables.Transaction.Type.fiat) {
                FiatArgs(holding.symbol).launch(context)
            }
            else{
                CryptoArgs(holding?.symbol!!).launch(context)
            }
        }
    }

    override fun getItemCount(): Int {
        return holdings?.size ?: 0
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val currency = view.currency
        val symbol = view.symbol
        val textFiat = view.text_fiat
        val holding = view.holding
        val change = view.change
        val value = view.value
        val price = view.price
        val image = view.image
    }
}