package com.jonnycaley.cryptomanager.ui.portfolio

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.MultiPrice.MultiPrices
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.MultiPrice.Price
import com.jonnycaley.cryptomanager.data.model.DataBase.Holding
import com.jonnycaley.cryptomanager.data.model.DataBase.Variables
import com.jonnycaley.cryptomanager.ui.crypto.CryptoArgs
import com.jonnycaley.cryptomanager.ui.fiat.FiatArgs
import com.jonnycaley.cryptomanager.utils.Utils
import kotlinx.android.synthetic.main.item_holding.view.*

class HoldingsAdapter(val transactions: ArrayList<Holding>?, val prices: ArrayList<Price>, val context: Context?) : RecyclerView.Adapter<HoldingsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_holding, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val transaction = transactions?.get(position)

        holder.setIsRecyclable(false)

        val price = prices.filter { it.symbol?.toLowerCase() == transaction?.symbol?.toLowerCase() }[0].prices?.uSD
        val value = price?.times(transaction?.quantity!!)
        val change = value?.minus(transaction?.cost!!)

        if (change != null && change < 0) {
            holder.change.setTextColor(context?.resources?.getColor(R.color.red)!!)
            holder.change.text = "-$${Utils.formatPrice(change!!).substring(1)}"
        } else {
            holder.change.setTextColor(context?.resources?.getColor(R.color.green)!!)
            holder.change.text = "$${Utils.formatPrice(change!!)}"
        }

        holder.amount.text = transaction?.quantity.toString()
        holder.currency.text = transaction?.symbol.toString()
        holder.symbol.text = transaction?.symbol.toString()
        holder.value.text = "($${Utils.formatPrice(value!!)})"
        holder.price.text = "$$price"

        if(transaction?.type ==  Variables.Transaction.Type.fiat) {
            holder.textFiat.visibility = View.VISIBLE
            holder.price.visibility = View.GONE
            holder.change.text = "$${Utils.formatPrice(value!!)}"
            holder.change.setTextColor(context.resources.getColor(R.color.text_grey))
            holder.symbol.visibility = View.GONE
            holder.value.visibility = View.GONE
            holder.amount.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            if(transaction?.type ==  Variables.Transaction.Type.fiat) {
                FiatArgs(transaction.symbol).launch(context!!)
            }
            else{
                CryptoArgs(transaction?.symbol!!).launch(context!!)
            }
        }
    }

    override fun getItemCount(): Int {
        return transactions?.size ?: 0
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val currency = view.currency
        val symbol = view.symbol
        val textFiat = view.text_fiat
        val amount = view.amount
        val change = view.change
        val value = view.value
        val price = view.price
    }
}