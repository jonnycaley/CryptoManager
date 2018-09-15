package com.jonnycaley.cryptomanager.ui.fiat

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import com.jonnycaley.cryptomanager.ui.transactions.update.fiat.UpdateFiatTransactionArgs
import com.jonnycaley.cryptomanager.utils.Constants
import kotlinx.android.synthetic.main.item_fiat_transaction.view.*
import java.text.SimpleDateFormat
import java.util.*

class TransactionsAdapter(val fiatSymbol : String, val transactions: List<Transaction>?, val context: Context?, val view: FiatContract.View) : RecyclerView.Adapter<TransactionsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_fiat_transaction, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val transaction = transactions?.get(position)

        holder.setIsRecyclable(false)

        holder.amount.text = "$fiatSymbol${transaction?.quantity.toString()}"
        holder.to.text = transaction?.exchange
        holder.currency.text = transaction?.currency.toString()
        holder.date.text = formatDate(transaction?.date)

        holder.itemView.setOnClickListener {
            UpdateFiatTransactionArgs(transaction!!).launch(context!!)
        }
    }

    private fun formatDate(date: Date?): CharSequence? {

        val format = SimpleDateFormat(Constants.dateFormat)

        return format.format(date)
    }

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return transactions?.size ?: 0
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val amount = view.amount
        val to = view.to
        val currency = view.currency
        val date = view.date
    }
}