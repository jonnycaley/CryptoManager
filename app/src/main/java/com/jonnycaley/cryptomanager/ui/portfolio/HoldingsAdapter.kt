package com.jonnycaley.cryptomanager.ui.portfolio

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.jonnycaley.cryptomanager.R
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
import java.math.BigDecimal
import java.text.DecimalFormat

class HoldingsAdapter(val holdings: ArrayList<Holding>?, val prices: ArrayList<Price>, val baseFiat: Rate, val chosenCurrency: String, val allFiats: ArrayList<Rate>, val isPercentage: Boolean, val context: Context?) : RecyclerView.Adapter<HoldingsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_holding, parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val holding = holdings?.get(position)

        holder.setIsRecyclable(false)

        var symbol = Utils.getFiatSymbol(baseFiat.fiat)

        var price = prices.first { it.symbol?.toUpperCase() == holding?.symbol?.toUpperCase() }.prices?.uSD?.times(baseFiat.rate!!)
        val cost = holding?.costUsd?.times(baseFiat.rate!!)
        var value = price?.times(holding?.quantity!!)

        Log.i(TAG, "-------------")

        Log.i(TAG, "holding ${holding?.symbol}")
        Log.i(TAG, "price = $price")
        Log.i(TAG, "quantity = ${holding?.quantity!!}")
        Log.i(TAG, "value = $value")
        Log.i(TAG, "btcPrice: ${prices.first { it.symbol?.toUpperCase() == "BTC" }.prices?.uSD!!}")
        Log.i(TAG, "valueBtc: ${value?.div(prices.first { it.symbol?.toUpperCase() == "BTC" }.prices?.uSD!!.times(baseFiat.rate!!))}")

        Log.i(TAG, "-------------")

        var change = value?.minus(cost!!)


        if (holding?.type == Variables.Transaction.Type.fiat) {

            holder.price.visibility = View.GONE
            holder.change.text = "$symbol${Utils.formatPrice(value!!)}"
            holder.change.setTextColor(context?.resources?.getColor(R.color.text_grey)!!)
            holder.currency.text = holding.symbol
            holder.symbol.visibility = View.GONE
            holder.value.visibility = View.GONE
            holder.holding.visibility = View.GONE

            when (chosenCurrency) {
                PortfolioFragment.CURRENCY_BTC -> {
                    symbol = "B"
                    holder.change.text = "$symbol${Utils.formatPrice(value / prices.first { it.symbol?.toUpperCase() == "BTC" }.prices?.uSD!!.times(baseFiat.rate!!))}"
                }
                PortfolioFragment.CURRENCY_ETH -> {
                    symbol = "E"

                    holder.change.text = "$symbol${Utils.formatPrice(value / prices.first { it.symbol?.toUpperCase() == "ETH" }.prices?.uSD!!.times(baseFiat.rate!!))}"
                }
            }

        } else {

            when (chosenCurrency) {
                PortfolioFragment.CURRENCY_BTC -> {
                    symbol = "B"

                    Log.i(TAG, holding?.symbol)
                    val costBtcHistorical = holding?.costBtc
                    val costBtcNow = holding?.quantity!! * prices.first { it.symbol?.toUpperCase() == holding.symbol.toUpperCase() }.prices?.uSD!!.times(baseFiat.rate!!) / prices.first { it.symbol?.toUpperCase() == "BTC" }.prices?.uSD!!.times(baseFiat.rate!!)
                    Log.i(TAG, "costBtcHistorical: ${costBtcHistorical}")
                    Log.i(TAG, "costBtcNow: ${costBtcNow}")

//                    if (holding.quantity < 0)
//                        change = costBtcHistorical?.minus(costBtcNow!!)
//                    else
                    change = costBtcNow - costBtcHistorical!!

                    if (holding.symbol == "BTC")
                        change = 0.toBigDecimal()

                    price = price?.div(prices.first { it.symbol?.toUpperCase() == "BTC" }.prices?.uSD!!.times(baseFiat.rate!!))
                    value = value?.div(prices.first { it.symbol?.toUpperCase() == "BTC" }.prices?.uSD!!.times(baseFiat.rate!!))

                    Log.i(TAG, "ValueBtc = $value")

                }
                PortfolioFragment.CURRENCY_ETH -> {

                    val costBtcHistorical = holding?.costEth
                    val costBtcNow = holding?.quantity!! * prices.first { it.symbol?.toUpperCase() == holding.symbol.toUpperCase() }.prices?.uSD!!.times(baseFiat.rate!!) / prices.first { it.symbol?.toUpperCase() == "ETH" }.prices?.uSD!!.times(baseFiat.rate!!)

                    symbol = "E"

//                    if (holding.quantity < 0)
//                        change = costBtcHistorical?.minus(costBtcNow!!)
//                    else
                    change = costBtcNow - costBtcHistorical!!

                    if (holding.symbol == "ETH")
                        change = 0.toBigDecimal()

                    price = price?.div(prices.first { it.symbol?.toUpperCase() == "ETH" }.prices?.uSD!!.times(baseFiat.rate!!))
                    value = value?.div(prices.first { it.symbol?.toUpperCase() == "ETH" }.prices?.uSD!!.times(baseFiat.rate!!))
                }
//                PortfolioFragment.CURRENCY_FIAT -> {
//
//                    Log.i(TAG, holding?.symbol)
//                    val costBtcHistorical = holding?.costBtc?
//                    val costBtcNow = value?.div(prices.first { it.symbol?.toUpperCase() == "BTC" }.prices?.uSD!!)
//                    Log.i(TAG, "costBtcHistorical: ${costBtcHistorical}")
//                    Log.i(TAG, "costBtcNow: ${costBtcNow}")
//
//                    change = costBtcNow?.minus(costBtcHistorical!!)
//
//                    if (holding?.symbol == "BTC")
//                        change = 0
//
//                    price = price?.div(prices.first { it.symbol?.toUpperCase() == "BTC" }.prices?.uSD!!)
//                    value = value?.div(prices.first { it.symbol?.toUpperCase() == "BTC" }.prices?.uSD!!)
//
//                }
            }

            var absBalanceBtc = holding.costEth.abs()

            var absBalanceEth = holding.costEth.abs()

            if(isPercentage) {
                when (chosenCurrency) {
                    PortfolioFragment.CURRENCY_BTC -> {
                        if((absBalanceBtc == 0.toBigDecimal()) || (change == 0.toBigDecimal())){
                            holder.change.text = "-"
                            context?.resources?.getColor(R.color.text_grey)?.let { holder.change.setTextColor(it) }
                        } else {
                            val changePct = change?.div(absBalanceBtc)
                            formatPercentage(changePct?.times(100.toBigDecimal()), holder.change)
                        }
                    }
                    PortfolioFragment.CURRENCY_ETH -> {
                        if((absBalanceEth == 0.toBigDecimal()) || (change == 0.toBigDecimal())){
                            holder.change.text = "-"
                            context?.resources?.getColor(R.color.text_grey)?.let { holder.change.setTextColor(it) }
                        } else {
                            val changePct = change?.div(absBalanceEth)
                            formatPercentage(changePct?.times(100.toBigDecimal()), holder.change)
                        }
                    }
                    else -> {
                        formatPercentage(change, holder.change)
                    }
                }
            } else {
                formatPrice(change!!, symbol, holder.change)
            }




            //TODO: GET THE CHANGE CORRECT & THINK ABOUT IT!!!!

//            if (change != null && change < 0.toBigDecimal()) {
//                holder.change.setTextColor(context?.resources?.getColor(R.color.red)!!)
//                if (isPercentage) {
//                    if (chosenCurrency == PortfolioFragment.CURRENCY_BTC){
//                        holder.change.text = Utils.formatPercentage((change / value!!)).substring(1)
//                        }
//                    else {
//
//                        holder.change.text = Utils.formatPercentage((change / value!!)).substring(1)
//                    }
//                } else {
//                    holder.change.text = "-$symbol${Utils.formatPrice(change).substring(1)}"
//                }
//            } else if (change != 0.toBigDecimal()) {
//                holder.change.setTextColor(context?.resources?.getColor(R.color.green)!!)
////                holder.change.text = "$symbol${Utils.formatPrice(change?.toDouble()!!)}"
//
//                if (isPercentage) {
//                    if (chosenCurrency == PortfolioFragment.CURRENCY_BTC)
//                        holder.change.text = Utils.formatPercentage(change?.div(value!!)!!).substring(1)
//                    else
//                        holder.change.text = Utils.formatPercentage(change?.div(value!!)!!).substring(1)
//                } else {
//                    holder.change.text = "$symbol${Utils.formatPrice(change!!)}"
//                }
//            } else {
//                holder.change.setTextColor(context?.resources?.getColor(R.color.text_grey)!!)
//                holder.change.text = "-"
//            }

            holder.holding.text = holding?.quantity.toString()
            holder.currency.text = holding?.symbol.toString()
            holder.symbol.text = holding?.symbol.toString()
            holder.value.text = "($symbol${value})"
            holder.price.text = "$symbol${Utils.formatPrice(price!!)}"

        }

        if (holding?.imageUrl != null && !holding.imageUrl?.contains("null")!!) {
            Picasso.with(context)
                    .load(holding.imageUrl)
                    .fit()
                    .centerCrop()
                    .transform(CircleTransform())
                    .placeholder(R.drawable.circle)
                    .into(holder.image)
        } else {
            holder.image.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            println("HolidngType: ${holding?.type}")
            if (holding?.type == Variables.Transaction.Type.fiat) {
                FiatArgs(holding.symbol).launch(context!!)
            } else if (allFiats.any { it.fiat?.toUpperCase() == holding?.symbol }) {
                FiatArgs(holding?.symbol!!).launch(context!!)
            } else {
                CryptoArgs(holding?.symbol!!).launch(context!!)
            }
        }
    }

    fun formatPercentage(percentChange24h: BigDecimal?, view : TextView) {

        println("Percentage: $percentChange24h")

        if (percentChange24h == 0.toBigDecimal()) {
            context?.resources?.getColor(R.color.text_grey)?.let { view.setTextColor(it) }
            view.text = "-"
        } else {
            val percentage2dp = percentChange24h?.setScale(2, BigDecimal.ROUND_HALF_UP)
            println(percentage2dp)

            return when {
                percentage2dp!! > 0.toBigDecimal() -> {
                    context?.resources?.getColor(R.color.green)?.let { view.setTextColor(it) }
                    view.text = "+$percentage2dp%"
                }
                else -> {
                    context?.resources?.getColor(R.color.red)?.let { view.setTextColor(it) }
                    view.text = "$percentage2dp%"
                }
            }
        }
    }


    fun formatPrice(priceAsDouble: BigDecimal, symbol : String, view : TextView) {

        if(priceAsDouble == 0.toBigDecimal()){
            context?.resources?.getColor(R.color.text_grey)?.let { view.setTextColor(it) }
            view.text = "${symbol}0"
        }

        var absPrice = priceAsDouble

        var priceSubtractor = false

        if(priceAsDouble < 0.toBigDecimal() ) {
            priceSubtractor = true
            absPrice = priceAsDouble * (-1).toBigDecimal()
        }

        val price = Utils.toDecimals(absPrice, 8).toDouble()

        var priceText: String

        priceText = if(price > 1)
            Utils.toDecimals(absPrice, 2)
        else
            "0${Utils.toDecimals(absPrice, 6)}"

        if(priceText.indexOf(".") != -1 && (priceText.indexOf(".") + 1 == priceText.length -1))
            priceText += "0"

        if(priceSubtractor) {
            context?.resources?.getColor(R.color.red)?.let { view.setTextColor(it) }
            priceText = "-$priceText"
        } else {
            context?.resources?.getColor(R.color.green)?.let { view.setTextColor(it) }
        }
        view.text = "$symbol$priceText"
    }


    override fun getItemCount(): Int {
        return holdings?.size ?: 0
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val currency = view.currency
        val symbol = view.symbol
        //        val textFiat = view.text_fiat
        val holding = view.holding
        val change = view.change
        val value = view.value
        val price = view.price
        val image = view.image
    }

    companion object {
        val TAG = "HoldingsAdapter"
    }
}