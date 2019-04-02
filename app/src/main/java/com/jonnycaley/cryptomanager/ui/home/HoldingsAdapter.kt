package com.jonnycaley.cryptomanager.ui.home

import android.annotation.SuppressLint
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
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
import com.jonnycaley.cryptomanager.utils.Constants
import com.jonnycaley.cryptomanager.utils.Utils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_holding.view.*
import java.math.BigDecimal
import android.widget.RelativeLayout
import java.math.RoundingMode

class HoldingsAdapter(var holdings: ArrayList<Holding>, val prices: ArrayList<Price>, val baseFiat: Rate, val chosenCurrency: String, val allFiats: ArrayList<Rate>, val isPercentage: Boolean, val context: Context?) : androidx.recyclerview.widget.RecyclerView.Adapter<HoldingsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_holding, parent, false))
    }

    fun onSortChanged(sort : String){

        var tempHoldings = ArrayList<Holding>()
        tempHoldings.addAll(this.holdings)
        this.holdings.clear()

        Log.i(TAG, sort)

        val baseRate = baseFiat.rate ?: Constants.baseRate

        if((sort == HomeFragment.SORT_CHANGE_ASCENDING) || sort == HomeFragment.SORT_CHANGE_DESCENDING) {

            tempHoldings = ArrayList(tempHoldings.sortedBy { holding ->

                val price = baseRate.let { prices.first { it.symbol?.toUpperCase() == holding.symbol.toUpperCase() }.prices?.uSD?.times(it) }
                val value = price?.times(holding.quantity)
                val cost = baseRate.let { holding.costUsd.times(it) }
                val change = cost.let { value?.minus(it) }

                val costBtcHistorical = holding.costBtc
                val costBtcNow = holding.quantity * (prices.first { it.symbol?.toUpperCase() == holding.symbol.toUpperCase() }.prices?.uSD?.times(baseRate) ?: 0.toBigDecimal()) / (baseRate.let { prices.first { it.symbol?.toUpperCase() == "BTC" }.prices?.uSD?.times(it) }
                        ?: 1.toBigDecimal())

                val costEthHistorical = holding.costEth
                val costEthNow = holding.quantity * (prices.first { it.symbol?.toUpperCase() == holding.symbol.toUpperCase() }.prices?.uSD?.times(baseRate) ?: 0.toBigDecimal()) / (prices.first { it.symbol?.toUpperCase() == "ETH" }.prices?.uSD?.times(baseRate) ?: 1.toBigDecimal())

                var parameter = 0.toBigDecimal()

//                if (sort == HomeFragment.SORT_CHANGE_ASCENDING) {
                    if (isPercentage) {
                        when (chosenCurrency) {
                            HomeFragment.CURRENCY_BTC -> {
//                                Log.i(TAG, "BTC Percentage")
                                val absBalanceBtc = holding.costBtc.abs()
                                parameter = change?.div(absBalanceBtc) ?: 0.toBigDecimal()

                            }
                            HomeFragment.CURRENCY_ETH -> {
//                                Log.i(TAG, "ETH Percentage")
                                val absBalanceEth = holding.costEth.abs()
                                parameter = change?.div(absBalanceEth) ?: 0.toBigDecimal()
                            }
                            else -> { //FIAT
//                                Log.i(TAG, "FIAT Percentage")
                                parameter = cost.let { value?.minus(it) } ?: 0.toBigDecimal()
                            }
                        }
                    } else {
                        when (chosenCurrency) {
                            HomeFragment.CURRENCY_BTC -> {
                                parameter = costBtcNow - costBtcHistorical
                            }
                            HomeFragment.CURRENCY_ETH -> {
                                parameter = costEthNow - costEthHistorical
                            }
                            HomeFragment.CURRENCY_FIAT -> {
                                parameter = cost.let { value?.minus(it) } ?: 0.toBigDecimal()
                            }
                        }
                    }
//                }

                if(holding.type == Variables.Transaction.Type.fiat){
                    parameter = 0.toBigDecimal()
                }
                Log.i(TAG, "Parameter: $parameter")
                parameter
            })
        }
        if(sort == HomeFragment.SORT_CHANGE_ASCENDING) {
            Log.i(TAG, "Ascending")
            tempHoldings.forEach{
                Log.i(TAG, it.symbol)
            }
            this.holdings.addAll(tempHoldings.asReversed())
        }
        else if(sort == HomeFragment.SORT_NAME_DESCENDING) {
            this.holdings.addAll(tempHoldings.sortedBy { holding ->
                holding.symbol
            }.asReversed())
        }
        else if(sort == HomeFragment.SORT_NAME_ASCENDING) {
            this.holdings.addAll(tempHoldings.sortedBy { holding ->
                holding.symbol
            })
        }
        else if(sort == HomeFragment.SORT_HOLDINGS_DESCENDING) {
            this.holdings.addAll(tempHoldings.sortedBy { holding ->
                holding.quantity * (prices.first { it.symbol?.toUpperCase() == holding.symbol.toUpperCase() }.prices?.uSD?.times(baseRate) ?: 0.toBigDecimal())
            }.asReversed())
        }
        else if(sort == HomeFragment.SORT_HOLDINGS_ASCENDING) {
            this.holdings.addAll(tempHoldings.sortedBy { holding ->
                holding.quantity * (prices.first { it.symbol?.toUpperCase() == holding.symbol.toUpperCase() }.prices?.uSD?.times(baseRate) ?: 0.toBigDecimal())
            })
        } else {
            Log.i(TAG, "Descending")
            tempHoldings.asReversed().forEach{
                println(it.symbol)
            }
            this.holdings.addAll(tempHoldings)
        }
        notifyDataSetChanged()
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val holding = holdings.get(position)

        holder.setIsRecyclable(false)

        var symbol = Utils.getFiatSymbol(baseFiat.fiat)

        val baseRate = baseFiat.rate ?: Constants.baseRate

        var price = prices.first { it.symbol?.toUpperCase() == holding.symbol.toUpperCase() }.prices?.uSD?.times(baseRate)
        val cost = holding.costUsd.times(baseRate)
        var value = price?.times(holding.quantity)

        Log.i(TAG, "-------------")

        Log.i(TAG, "holding ${holding.symbol}")
        Log.i(TAG, "price = $price")
        Log.i(TAG, "quantity = ${holding.quantity}")
        Log.i(TAG, "value = $value")
        Log.i(TAG, "btcPrice: ${prices.first { it.symbol?.toUpperCase() == "BTC" }.prices?.uSD!!}")
        Log.i(TAG, "valueBtc: ${value?.div(prices.first { it.symbol?.toUpperCase() == "BTC" }.prices?.uSD!!.times(baseRate))}")

        Log.i(TAG, "-------------")

        var change = value?.minus(cost)

        if (holding.type == Variables.Transaction.Type.fiat) {

            value = holding.quantity.divide(allFiats.firstOrNull { it.fiat == holding.symbol }?.rate, 2, RoundingMode.HALF_UP)
            Log.i(TAG, "newPrice = $value")


            holder.price.visibility = View.GONE
            holder.value.text = value?.let { Utils.getPriceTextAbs(it.times(baseRate).toDouble(), symbol) }

            val layoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, 0)

            holder.value.layoutParams = layoutParams

            holder.currency.text = holding.symbol
            holder.change.visibility = View.GONE
//            holder.holding.visibility = View.GONE

            holder.holding.text = Utils.getPriceTextAbs(holding.quantity.toDouble(), "")

            when (chosenCurrency) {
                HomeFragment.CURRENCY_BTC -> {
                    symbol = "BTC"
                    holder.value.text = Utils.getPriceTextAbs(value?.divide((prices.first { it.symbol?.toUpperCase() == "BTC" }.prices?.uSD?.times(baseRate) ?: 1.toBigDecimal()), 8 , RoundingMode.HALF_UP)?.toDouble() ?: 0.toDouble(), symbol)
                }
                HomeFragment.CURRENCY_ETH -> {
                    symbol = "ETH"
                    holder.value.text = Utils.getPriceTextAbs(value?.divide((prices.first { it.symbol?.toUpperCase() == "ETH" }.prices?.uSD?.times(baseRate) ?: 1.toBigDecimal()), 8 , RoundingMode.HALF_UP)?.toDouble() ?: 0.toDouble(), symbol)
                }
            }
        } else {

            when (chosenCurrency) {
                HomeFragment.CURRENCY_BTC -> {
                    symbol = "BTC"
                    val costBtcHistorical = holding.costBtc
                    val costBtcNow = holding.quantity * (prices.first { it.symbol?.toUpperCase() == holding.symbol.toUpperCase() }.prices?.uSD?.times(baseRate) ?: 0.toBigDecimal()) / (prices.first { it.symbol?.toUpperCase() == "BTC" }.prices?.uSD?.times(baseRate) ?: 1.toBigDecimal())
//                    if (holding.quantity < 0)
//                        change = costBtcHistorical?.minus(costBtcNow!!)
//                    else
                    change = costBtcNow - costBtcHistorical

                    println("See hererere")
                    println(holding.symbol)
                    if (holding.symbol == "BTC")
                        change = 0.0.toBigDecimal()

                    price = price?.div(prices.first { it.symbol?.toUpperCase() == "BTC" }.prices?.uSD?.times(baseRate) ?: 1.toBigDecimal())
                    value = value?.div(prices.first { it.symbol?.toUpperCase() == "BTC" }.prices?.uSD?.times(baseRate) ?: 1.toBigDecimal())
                }
                HomeFragment.CURRENCY_ETH -> {

                    val costEthHistorical = holding.costEth
                    val costEthNow = holding.quantity * (prices.first { it.symbol?.toUpperCase() == holding.symbol.toUpperCase() }.prices?.uSD?.times(baseRate) ?: 1.toBigDecimal()) / (prices.first { it.symbol?.toUpperCase() == "ETH" }.prices?.uSD?.times(baseRate) ?: 1.toBigDecimal())
                    symbol = "Ξ"
//                    if (holding.quantity < 0)
//                        change = costBtcHistorical?.minus(costBtcNow!!)
//                    else
                    change = costEthNow - costEthHistorical

                    if (holding.symbol == "ETH")
                        change = 0.0.toBigDecimal()

                    price = prices.first { it.symbol?.toUpperCase() == "ETH" }.prices?.uSD?.times(baseRate)?.let { price?.div(it) }
                    value = prices.first { it.symbol?.toUpperCase() == "ETH" }.prices?.uSD?.times(baseRate)?.let { value?.div(it) }
                }
//                HomeFragment.CURRENCY_FIAT -> {
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

            val absBalanceBtc = holding.costBtc.abs()

            val absBalanceEth = holding.costEth.abs()

            if(isPercentage) {
                when (chosenCurrency) {
                    HomeFragment.CURRENCY_BTC -> {
                        if((absBalanceBtc == 0.0.toBigDecimal()) || (change == 0.0.toBigDecimal())){
                            holder.change.text = "-"
                            context?.resources?.getColor(R.color.text_grey)?.let { holder.change.setTextColor(it) }
                        } else {
                            Log.i(TAG, "$change")
                            Log.i(TAG, "$absBalanceBtc")
                            val changePct = change?.div(absBalanceBtc) //TODO: IF DIV BY 0 (WHEN PRESSED QUICKLY BETWEEN SORTS)
                            formatPercentage(changePct?.times(100.toBigDecimal()), holder.change)
                        }
                    }
                    HomeFragment.CURRENCY_ETH -> {
                        if((absBalanceEth == 0.0.toBigDecimal()) || (change == 0.0.toBigDecimal())){
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
                change?.let { mChange ->

                    if(change < 0.toBigDecimal())
                        context?.resources?.getColor(R.color.red)?.let { holder.change.setTextColor(it) }
                    else if (change > 0.toBigDecimal())
                        context?.resources?.getColor(R.color.green)?.let { holder.change.setTextColor(it) }
                    else
                        context?.resources?.getColor(R.color.text_grey)?.let { holder.change.setTextColor(it) }
                    holder.change.text = Utils.getPriceTextAbs(mChange.toDouble(), symbol)
                }
            }
            //TODO: GET THE CHANGE CORRECT & THINK ABOUT IT!!!!
//            if (change != null && change < 0.toBigDecimal()) {
//                holder.change.setTextColor(context?.resources?.getColor(R.color.red)!!)
//                if (isPercentage) {
//                    if (chosenCurrency == HomeFragment.CURRENCY_BTC){
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
//                    if (chosenCurrency == HomeFragment.CURRENCY_BTC)
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
            holder.holding.text = Utils.getPriceTextAbs(holding.quantity.toDouble(), "")
            holder.currency.text = holding.symbol
            holder.value.text = "${value?.let { priceDecimal -> Utils.getPriceTextAbs(priceDecimal.toDouble(), symbol) }}"
            holder.price.text = Utils.getPriceTextAbs(price?.toDouble(), symbol)

        }
        Picasso.with(context)
                .load(holding.imageUrl)
                .fit()
                .centerCrop()
                .transform(CircleTransform())
                .placeholder(R.drawable.circle)
                .into(holder.image)

        if (!(holding.imageUrl != null && (holding.imageUrl?.contains("null")) != true)) {
            holder.fiat.visibility = View.VISIBLE
            holder.fiat.text = Utils.getFiatSymbol(holding.symbol)
        }

//        if (holding.imageUrl != null && (holding.imageUrl?.contains("null")) != true) {
//            Picasso.with(context)
//                    .load(holding.imageUrl)
//                    .fit()
//                    .centerCrop()
//                    .transform(CircleTransform())
//                    .placeholder(R.drawable.circle)
//                    .into(holder.image)
//        } else {
//            Picasso.with(context)
//                    .load()
//                    .fit()
//                    .centerCrop()
//                    .transform(CircleTransform())
//                    .placeholder(R.drawable.circle)
//                    .into(holder.image)
//
//            holder.image.visibility = View.GONE
//        }

        holder.itemView.setOnClickListener {

            if (holding.type == Variables.Transaction.Type.fiat) {
                context?.let { it1 -> FiatArgs(holding.symbol).launch(it1) }
            } else if (allFiats.any { it.fiat?.toUpperCase() == holding.symbol }) {
                context?.let { it1 -> FiatArgs(holding.symbol).launch(it1) }
            } else {
                context?.let { context -> CryptoArgs(holding.symbol, holding.name).launch(context) }
            }
        }
    }

    fun formatPercentage(percentChange24h: BigDecimal?, view : TextView) {

        println("Percentage: $percentChange24h")

        if (percentChange24h == 0.toBigDecimal()) {
            context?.resources?.getColor(R.color.text_grey)?.let { view.setTextColor(it) }
            view.text = "-"
        } else {
            val percentage2dp = percentChange24h?.setScale(2, BigDecimal.ROUND_HALF_UP) ?: 0.toBigDecimal()

            println(percentage2dp)

            return when {
                percentage2dp > 0.toBigDecimal() -> {
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


//    fun formatPrice(priceAsDouble: BigDecimal, symbol : String, view : TextView) {
//
//        if(priceAsDouble == 0.toBigDecimal()){
//            context?.resources?.getColor(R.color.text_grey)?.let { view.setTextColor(it) }
//            view.text = "${symbol}0"
//        }
//
//        var absPrice = priceAsDouble
//
//        var priceSubtractor = false
//
//        if(priceAsDouble < 0.toBigDecimal() ) {
//            priceSubtractor = true
//            absPrice = priceAsDouble * (-1).toBigDecimal()
//        }
//
//        val price = Utils.toDecimals(absPrice, 8).toDouble()
//
//        var priceText: String
//
//        priceText = if(price > 1)
//            Utils.toDecimals(absPrice, 2)
//        else
//            "0${Utils.toDecimals(absPrice, 6)}"
//
//        if(priceText.indexOf(".") != -1 && (priceText.indexOf(".") + 1 == priceText.length -1))
//            priceText += "0"
//
//        if(priceSubtractor) {
//            context?.resources?.getColor(R.color.red)?.let { view.setTextColor(it) }
//            priceText = "-$priceText"
//        } else {
//            context?.resources?.getColor(R.color.green)?.let { view.setTextColor(it) }
//        }
//        view.text = "$symbol$priceText"
//    }


    override fun getItemCount(): Int {
        return holdings.size
    }

    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val currency = view.currency
        //        val textFiat = view.text_fiat
        val holding = view.holding
        val fiat = view.fiat
        val change = view.change
        val value = view.value
        val price = view.price
        val image = view.image
        val layoutChange = view.layout_change
    }

    companion object {
        val TAG = "HoldingsAdapter"
    }
}
