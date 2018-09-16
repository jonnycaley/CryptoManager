package com.jonnycaley.cryptomanager.ui.crypto.viewpager.transactions

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction

class TransactionsAdapter(val transactions: List<Transaction>?, val context: Context?) : RecyclerView.Adapter<TransactionsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val transaction = transactions?.get(position)

        holder.setIsRecyclable(false)

//        TODO: BIND THE INFORMATION OF BOTH BUY FROM AND SELL TO THE CURRENCY

//        holder.amount.text = transaction?.quantity.toString()
//        holder.currency.text = transaction?.currency.toString()
//

        holder.itemView.setOnClickListener {
//            if(transaction?.type ==  Variables.Transaction.Type.fiat) {
//                FiatArgs(transaction?.currency!!).launch(context!!)
//            }
//            else{
//                CryptoArgs(transaction?.currency!!).launch(context!!)
//            }
        }
    }

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return transactions?.size ?: 0
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
//        val currency = view.currency
//        val textFiat = view.text_fiat
//        val amount = view.amount
    }
}