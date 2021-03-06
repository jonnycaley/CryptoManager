package com.jonnycaley.cryptomanager.ui.crypto.viewpager.transactions

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.Visibility
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.Rate
import com.jonnycaley.cryptomanager.ui.transactions.crypto.CryptoTransactionArgs
import com.jonnycaley.cryptomanager.utils.Utils
import kotlinx.android.synthetic.main.item_transaction_detail.view.*
import java.math.BigDecimal

class TransactionsAdapter(val transactions: List<Transaction>, val currency: String, val currentUSDPrice: BigDecimal?, val baseFiat: Rate, val context: Context?) : androidx.recyclerview.widget.RecyclerView.Adapter<TransactionsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_transaction_detail, parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val transaction = transactions[position]

        holder.setIsRecyclable(false)

        val symbol = Utils.getFiatSymbol(baseFiat.fiat)

        val priceUsd = getPrice(transaction)
        val priceConverted = priceUsd.toBigDecimal().times(baseFiat.rate?: 1.toBigDecimal())

        Log.i(TAG, "Exchange: ${transaction.exchange}   " + "symbol: ${transaction.symbol}   " + "name: ${transaction.name}   " + "pairSymbol: ${transaction.pairSymbol}   " + "quantity: ${transaction.quantity}   " + "price: ${transaction.price}   " + "priceUSD: ${transaction.priceUSD}   " + "date: ${transaction.date}   " + "isDeducted: ${transaction.isDeducted}   " + "isDeductedPriceUsd: ${transaction.isDeductedPriceUsd}   " + "btcPrice: ${transaction.btcPrice}   " + "ethPrice: ${transaction.ethPrice}   ")

        holder.titlePair.text = getTitlePair(transaction)
        holder.titleAmount.text = getTitleAmount(transaction)
        holder.titlePrice.text = getTitlePrice(transaction)

        holder.textPair.text = getTextPair(transaction)

        holder.textTime.text = Utils.formatDate(transaction.date)

        holder.layoutBottomSell.visibility = getSellTransactionVisibility(transaction)

        holder.textAmount.text = Utils.getPriceTextAbs(getTextAmount(transaction).toDouble(), "")

        holder.textPrice.text = Utils.getPriceTextAbs(priceConverted.toDouble(), symbol)

        holder.textCost.text = Utils.getPriceTextAbs((priceConverted * getTextAmount(transaction)).toDouble(), symbol)

        holder.textProceeds.text = Utils.getPriceTextAbs((priceConverted * getTextAmount(transaction)).toDouble(), symbol)

        holder.textWorth.text = Utils.getPriceTextAbs(getWorth(transaction)?.toDouble(), symbol)

        holder.textChange.text = Utils.formatPercentage(getChange(transaction))

        if(getChange(transaction)!! > 0.toBigDecimal())
            context?.resources?.getColor(R.color.green)?.let { holder.textChange.setTextColor(it) }
        else
            context?.resources?.getColor(R.color.red)?.let { holder.textChange.setTextColor(it) }

        if(transaction.symbol == currency){ //buy transaction

            if(transaction.quantity > 0.toBigDecimal()){ //positive quantity means its a buy transaction
                holder.type.background = context?.resources?.getDrawable(R.drawable.button_checked_green)
            }
            if(transaction.quantity < 0.toBigDecimal()){ //negative quantity means its actually a sell transaction
                holder.type.background = context?.resources?.getDrawable(R.drawable.button_checked_red)
            }
        }
        if(transaction.pairSymbol == currency){ //sell transaction

            if(transaction.quantity > 0.toBigDecimal()){ //positive quantity means its a sell transaction
                holder.type.background = context?.resources?.getDrawable(R.drawable.button_checked_red)
            }
            if(transaction.quantity < 0.toBigDecimal()){ //negative quantity means its actually a buy transaction
                holder.type.background = context?.resources?.getDrawable(R.drawable.button_checked_green)
            }

            if (currentUSDPrice != null) {
            }
        }

        if(currentUSDPrice == null) {
            holder.textWorth.visibility = View.GONE
            holder.titleWorth.visibility = View.GONE
            holder.textChange.visibility = View.GONE
            holder.titleChange.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            context?.let { context -> CryptoTransactionArgs(transaction, null, false).launch(context) }
        }
    }

    /*
    Function gets the worth of a transaction
    */
    private fun getWorth(transaction: Transaction): BigDecimal? {
        return currentUSDPrice?.times(baseFiat.rate ?: 1.toBigDecimal())?.times(getTextAmount(transaction))
    }

    /*
    Function gets the change of a transaction
    */
    private fun getChange(transaction: Transaction): BigDecimal? {
        return ((getWorth(transaction)?.minus(getCost(transaction)!!))?.div(getCost(transaction)))?.times(100.toBigDecimal())
    }

    /*
    Function gets the cost of a transaction
    */
    fun getCost(transaction: Transaction): BigDecimal {
        if(transaction.symbol == currency) { // btc/eth
            return transaction.isDeductedPriceUsd.times((transaction.price.times(baseFiat.rate?: 1.toBigDecimal()) * transaction.quantity)).abs()
        } else {// eth/btc
            return transaction.isDeductedPriceUsd.times((transaction.price.times(baseFiat.rate?: 1.toBigDecimal()) * transaction.quantity)).abs()
        }
    }

    /*
    Function gets the price of a transaction
    */
    fun getPrice(transaction: Transaction): Double {
        return if (transaction.symbol == currency) {
          transaction.isDeductedPriceUsd.times(transaction.price).toDouble()
//            transaction.priceUSD.toDouble()
        } else {
            transaction.isDeductedPriceUsd.toDouble()
        }
    }

    /*
    Function gets the amount of a transaction
    */
    private fun getTextAmount(transaction: Transaction): BigDecimal {
        if(transaction.symbol == currency) {
            return transaction.quantity.abs()
        } else {
            return transaction.quantity.times(transaction.price).abs()
        }
    }

    /*
    Function gets the sell transaction visibility
    */
    fun getSellTransactionVisibility(transaction: Transaction): Int {
        if(transaction.symbol == currency) {
            if(transaction.quantity > 0.toBigDecimal()){
                return View.GONE
            } else {
                return View.VISIBLE
            }
        } else {
            if(transaction.quantity > 0.toBigDecimal()){
                return View.VISIBLE
            } else {
                return View.GONE
            }
        }
    }

    /*
    Function gets the title for the pair title
    */
    fun getTitlePair(transaction: Transaction): CharSequence {
        return if (transaction.symbol == currency) {
            "Trading Pair"
        } else {
            if(transaction.quantity > 0.toBigDecimal()) {
                "Due to buy of"
            } else {
                "Due to sell of"
            }
        }
    }

    /*
    Function gets the title for the amount
    */
    fun getTitleAmount(transaction: Transaction): CharSequence {
        return if (transaction.symbol == currency) {
            if(transaction.quantity > 0.toBigDecimal()) {
                "Amount Bought"
            } else {
                "Amount Sold"
            }
        } else  {
            if(transaction.quantity > 0.toBigDecimal()) {
                "Amount Deducted"
            } else {
                "Amount Added"
            }
        }
    }

    /*
    Function gets the text for the pair title
    */
    fun getTextPair(transaction: Transaction): CharSequence {
        return if(transaction.symbol == currency) {
            "${transaction.symbol}/${transaction.pairSymbol}"
        } else {
            transaction.symbol
        }
    }

    /*
    Function gets the title for the price title
    */
    fun getTitlePrice(transaction: Transaction): CharSequence {
        if(transaction.symbol == currency) {
            if(transaction.quantity > 0.toBigDecimal()){
                return "${currency.toUpperCase()} Buy Price"
            } else {
                return "${currency.toUpperCase()} Sell Price"
            }
        } else {
            if(transaction.quantity > 0.toBigDecimal()){
                return "${currency.toUpperCase()} Sell Price"
            } else {
                return "${currency.toUpperCase()} Buy Price"
            }
        }
    }

    override fun getItemCount(): Int {
        return transactions.size
    }

    class ViewHolder (view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val titlePrice = view.title_price
        val textPrice = view.text_price

        val textTime = view.text_time

        val titlePair = view.title_pair
        val textPair = view.text_pair

        val titleAmount = view.title_amount
        val textAmount = view.text_amount

        val titleCost = view.title_cost
        val textCost = view.text_cost

        val titleWorth = view.title_worth
        val textWorth = view.text_worth

        val titleChange = view.title_change
        val textChange = view.text_change

        val layoutBottomSell = view.layout_bottom_sell

        val titleProceeds = view.title_proceeds
        val textProceeds = view.text_proceeds

        val type = view.view_transaction_type

    }

    companion object {
        val TAG = "TransactionsAdapter"
    }
}