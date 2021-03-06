package com.jonnycaley.cryptomanager.ui.fiat

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import com.jonnycaley.cryptomanager.ui.transactions.crypto.CryptoTransactionArgs
import com.jonnycaley.cryptomanager.ui.transactions.fiat.FiatTransactionArgs
import com.jonnycaley.cryptomanager.utils.Constants
import com.jonnycaley.cryptomanager.utils.Utils
import kotlinx.android.synthetic.main.item_fiat_transaction.view.*
import java.text.SimpleDateFormat
import java.util.*

class TransactionsAdapter(val fiat : String, val fiatSymbol : String, val transactions: List<Transaction>?, val context: Context?, val view: FiatContract.View) : androidx.recyclerview.widget.RecyclerView.Adapter<TransactionsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_fiat_transaction, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val transaction = transactions?.get(position)

        holder.setIsRecyclable(false)

        if(transaction?.symbol == fiat) {
            holder.amount.text = "${Utils.getPriceTextAbs(transaction.quantity.toDouble(), fiatSymbol)}"
            holder.toText.text = "To"
            holder.to.text = transaction?.exchange
        }
        else {
            holder.amount.text = "${Utils.getPriceTextAbs(Math.abs(transaction?.quantity?.times(transaction?.price!!)!!.toDouble()), fiatSymbol)}"

            holder.to.text = transaction?.symbol
            if(transaction.quantity > 0.toBigDecimal())
                holder.toText.text = "Due to buy of"
            else
                holder.toText.text = "Due to sell of"
        }
        holder.currency.text = transaction?.symbol.toString()
        holder.date.text = Utils.formatDate(transaction?.date)

        holder.itemView.setOnClickListener {
            if(transaction.pairSymbol != null){
                CryptoTransactionArgs(transaction,null,false).launch(context!!)
            } else {
                FiatTransactionArgs(transaction, null, false).launch(context!!)
            }
        }
    }

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return transactions?.size ?: 0
    }

    class ViewHolder (view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val amount = view.amount
        val to = view.to
        val toText = view.to_text
        val currency = view.currency
        val date = view.date
    }
}