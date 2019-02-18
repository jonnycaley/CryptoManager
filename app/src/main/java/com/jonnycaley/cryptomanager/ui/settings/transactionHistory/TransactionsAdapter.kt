package com.jonnycaley.cryptomanager.ui.settings.transactionHistory

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import com.jonnycaley.cryptomanager.ui.transactions.crypto.update.UpdateCryptoTransactionArgs
import com.jonnycaley.cryptomanager.ui.transactions.fiat.update.UpdateFiatTransactionArgs
import com.jonnycaley.cryptomanager.utils.Utils
import kotlinx.android.synthetic.main.item_transaction_history.view.*

class TransactionsAdapter(val transactions: List<Transaction>?, val context: Context?) : RecyclerView.Adapter<TransactionsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_transaction_history, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val transaction = transactions?.get(position)

        holder.setIsRecyclable(false)

        var pairStr = "${transaction?.symbol}"
        if(transaction?.pairSymbol != null)
            pairStr += "/${transaction?.pairSymbol}"

        holder.date.text = Utils.formatDate(transaction?.date!!)
        holder.pair.text = pairStr
        holder.exchange.text = transaction?.exchange

        var str = ""

        if(transaction.pairSymbol == null) {
            if(transaction?.quantity!! > 0.toBigDecimal())
                str = "Deposited "
            else if(transaction?.quantity!! < 0.toBigDecimal())
                str = "Withdrew "

        } else {
            if(transaction?.quantity!! > 0.toBigDecimal())
                str = "Bought "
            else if(transaction?.quantity!! < 0.toBigDecimal())
                str = "Sold "
        }

        holder.description.text = str + transaction?.quantity.abs().toString() + " ${transaction.symbol} for ${transaction.price.abs() * transaction.quantity.abs()} ${transaction.pairSymbol}"

        holder.itemView.setOnClickListener {
            if(transaction.pairSymbol == null)
                UpdateFiatTransactionArgs(transaction!!).launch(context!!)
            else
                UpdateCryptoTransactionArgs(transaction!!).launch(context!!)
        }
    }

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return transactions?.size ?: 0
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val date = view.date
        val pair = view.pair
        val exchange = view.exchange
        val description = view.description

    }
}