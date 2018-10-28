package com.jonnycaley.cryptomanager.ui.crypto.viewpager.transactions

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.Rate
import com.jonnycaley.cryptomanager.ui.transactions.crypto.update.UpdateCryptoTransactionArgs
import com.jonnycaley.cryptomanager.utils.Utils
import kotlinx.android.synthetic.main.item_transaction_detail.view.*
import java.math.BigDecimal

class TransactionsAdapter(val transactions: List<Transaction>?, val currency: String, val currentUSDPrice: BigDecimal?, val baseFiat: Rate, val context: Context?) : RecyclerView.Adapter<TransactionsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_transaction_detail, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val transaction = transactions?.get(position)

        holder.setIsRecyclable(false)

        val convertedPrice = transaction?.price!!.times(baseFiat.rate!!)

        var symbol = Utils.getFiatSymbol(baseFiat.fiat)

        if(transaction.symbol == currency){

            holder.textPrice.text = symbol + Utils.formatPrice(transaction.isDeductedPriceUsd.times(convertedPrice))
            holder.titlePair.text = "Trading Pair"
            holder.textPair.text = "${transaction.symbol}/${transaction.pairSymbol}"

            holder.textCost.text = symbol + Utils.formatPrice(transaction.isDeductedPriceUsd.times((convertedPrice * transaction.quantity)))

            var multiplier = 1.toBigDecimal()

            if(transaction.quantity > 0.toBigDecimal()){
                holder.titlePrice.text = "${currency.toUpperCase()} Buy Price"
                holder.titleAmount.text = "Amount Bought"

                holder.layoutBottomSell.visibility = View.GONE
            }
            if(transaction.quantity < 0.toBigDecimal()){
                multiplier = (-1).toBigDecimal()
                holder.titlePrice.text = "${currency.toUpperCase()} Sell Price"
                holder.titleAmount.text = "Amount Sold"
                holder.textProceeds.text = symbol + Utils.formatPrice(transaction.isDeductedPriceUsd.times((transaction.quantity * multiplier * convertedPrice)))
                holder.layoutBottomSell.visibility = View.VISIBLE
            }
            holder.textAmount.text = (transaction.quantity * multiplier).toString()

            if (currentUSDPrice != null) {
                holder.textWorth.text = symbol + (currentUSDPrice * (transaction.quantity * multiplier)).toString()

                val change = ((((transaction.isDeductedPriceUsd?.times((convertedPrice * transaction.quantity))).minus((currentUSDPrice * (transaction.quantity * multiplier)))).div((transaction.isDeductedPriceUsd.times((convertedPrice * transaction.quantity))))).times((-100).toBigDecimal()))
                holder.textChange.text = Utils.formatPercentage(change)

                if(change > 0.toBigDecimal())
                    holder.textChange.setTextColor(context?.resources?.getColor(R.color.green)!!)
                else
                    holder.textChange.setTextColor(context?.resources?.getColor(R.color.red)!!)

            } else {
                holder.textWorth.visibility = View.GONE
                holder.titleWorth.visibility = View.GONE
                holder.textChange.visibility = View.GONE
                holder.titleChange.visibility = View.GONE
            }

        }
        if(transaction.pairSymbol == currency){

            holder.textPair.text = "${transaction.symbol}"

            holder.textPrice.text = symbol + Utils.formatPrice(transaction.isDeductedPriceUsd)

            var multiplier = 1.toBigDecimal()

            if(transaction.quantity > 0.toBigDecimal()){

                holder.titlePrice.text = "${currency.toUpperCase()} Sell Price"
                holder.titlePair.text = "Due to buy of"
                holder.titleAmount.text = "Amount Deducted"

                holder.layoutBottomSell.visibility = View.VISIBLE
            }
            if(transaction.quantity < 0.toBigDecimal()){
                multiplier = -1.toBigDecimal()

                holder.titlePrice.text = "${currency.toUpperCase()} Buy Price"
                holder.titlePair.text = "Due to sell of"
                holder.titleAmount.text = "Amount Added"

                holder.layoutBottomSell.visibility = View.GONE

            }


            holder.textProceeds.text = symbol + Utils.formatPrice((transaction.isDeductedPriceUsd.times((transaction.quantity * convertedPrice * multiplier))))
            holder.textAmount.text = Utils.formatPrice(((transaction.quantity * transaction.price * multiplier)))
            holder.textCost.text = symbol + Utils.formatPrice(transaction.isDeductedPriceUsd.times((transaction.quantity * convertedPrice * multiplier)))

            if (currentUSDPrice != null) {
                holder.textWorth.text = symbol + Utils.formatPrice((currentUSDPrice * (transaction.quantity * convertedPrice * multiplier)))

                val change = (((transaction.isDeductedPriceUsd.times((transaction.quantity * convertedPrice * multiplier))).minus((currentUSDPrice * (transaction.quantity * convertedPrice * multiplier)))).div((transaction.isDeductedPriceUsd.times((transaction.quantity * convertedPrice * multiplier))))).times(-100.toBigDecimal())
                holder.textChange.text = Utils.formatPercentage(change)

                if(change > 0.toBigDecimal())
                    holder.textChange.setTextColor(context?.resources?.getColor(R.color.green)!!)
                else
                    holder.textChange.setTextColor(context?.resources?.getColor(R.color.red)!!)
            } else {
                holder.textWorth.visibility = View.GONE
                holder.titleWorth.visibility = View.GONE
                holder.textChange.visibility = View.GONE
                holder.titleChange.visibility = View.GONE
            }

        }

        holder.itemView.setOnClickListener {
            UpdateCryptoTransactionArgs(transaction!!).launch(context!!)
        }
    }

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return transactions?.size ?: 0
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val titlePrice = view.title_price
        val textPrice = view.text_price

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
    }
}