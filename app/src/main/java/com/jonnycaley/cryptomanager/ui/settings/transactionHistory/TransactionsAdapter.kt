package com.jonnycaley.cryptomanager.ui.settings.transactionHistory

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import com.jonnycaley.cryptomanager.ui.transactions.crypto.CryptoTransactionArgs
import com.jonnycaley.cryptomanager.ui.transactions.fiat.FiatTransactionArgs
import com.jonnycaley.cryptomanager.utils.Utils
import kotlinx.android.synthetic.main.item_transaction_history.view.*

class TransactionsAdapter(val transactions: List<Transaction>?, val context: Context?) : androidx.recyclerview.widget.RecyclerView.Adapter<TransactionsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_transaction_history, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val transaction = transactions?.get(position)

        holder.setIsRecyclable(false)

        var pairStr = "${transaction?.symbol}"
        if(transaction?.pairSymbol != null)
            pairStr += "/${transaction.pairSymbol}"

        holder.date.text = Utils.formatDate(transaction?.date!!)
        holder.pair.text = pairStr + " - " + transaction.exchange

        var descriptionStart = ""

        if(transaction.pairSymbol == null) {
            holder.type.background = context?.resources?.getDrawable(R.drawable.button_checked)
            if(transaction.quantity > 0.toBigDecimal())
                descriptionStart = "Deposited "
            else if(transaction.quantity < 0.toBigDecimal())
                descriptionStart = "Withdrew "
        } else {
            if(transaction.quantity > 0.toBigDecimal()) {
                descriptionStart = "Bought "
                holder.type.background = context?.resources?.getDrawable(R.drawable.button_checked_green)
            }
            else if(transaction.quantity < 0.toBigDecimal()) {
                descriptionStart = "Sold "
                holder.type.background = context?.resources?.getDrawable(R.drawable.button_checked_red)
            }
        }

        var descriptionEnd = ""

        if(transaction.pairSymbol != null)
            descriptionEnd += "for ${Utils.getPriceTextAbs((transaction.price.abs() * transaction.quantity.abs()).toDouble(), "")} ${transaction.pairSymbol}"

        holder.description.text = descriptionStart + Utils.getPriceTextAbs(transaction?.quantity.abs().toDouble(), "")+ " ${transaction.symbol} $descriptionEnd"

        holder.itemView.setOnClickListener {
            if(transaction.pairSymbol == null)
                FiatTransactionArgs(transaction, null, false).launch(context!!)
            else
                CryptoTransactionArgs(transaction, null, false).launch(context!!)
        }
    }

    override fun getItemCount(): Int {
        return transactions?.size ?: 0
    }

    class ViewHolder (view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        val date = view.date
        val pair = view.pair
        val description = view.description
        val type = view.view_transaction_type

    }
}