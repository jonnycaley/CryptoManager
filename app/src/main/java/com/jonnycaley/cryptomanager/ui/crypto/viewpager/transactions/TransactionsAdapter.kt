package com.jonnycaley.cryptomanager.ui.crypto.viewpager.transactions

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import kotlinx.android.synthetic.main.item_transaction_detail.view.*

class TransactionsAdapter(val transactions: List<Transaction>?, val currency: String, val context: Context?) : RecyclerView.Adapter<TransactionsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_transaction_detail, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val transaction = transactions?.get(position)

        holder.setIsRecyclable(false)


        if(transaction?.symbol == currency){
            holder.textAmount.text = (transaction?.quantity!!).toString()
            holder.textPrice.text = transaction.price.toString()

            holder.titlePair.text = "Trading Pair"
            holder.textPair.text = "${transaction.symbol}/${transaction.pairSymbol}"

            if(transaction.quantity > 0){
                holder.titlePrice.text = "${currency.toUpperCase()} Buy Price"
                holder.titleAmount.text = "Amount Bought"

                holder.layoutBottomSell.visibility = View.GONE

            }
            if(transaction.quantity < 0){
                holder.titlePrice.text = "${currency.toUpperCase()} Sell Price"
                holder.titleAmount.text = "Amount Sold"

                holder.layoutBottomSell.visibility = View.VISIBLE
                holder.textProceeds.text = "Proceeds here"
            }
        }
        if(transaction?.pairSymbol == currency){

            holder.textAmount.text = (transaction.quantity * transaction.price).toString()
            holder.textPair.text = "${transaction.symbol}"

            holder.textPrice.text = transaction.price.toString()

            if(transaction.quantity > 0){
                holder.titlePrice.text = "${currency.toUpperCase()} Sell Price"
                holder.titlePair.text = "Due to buy of"
                holder.titleAmount.text = "Amount Deducted"

                holder.layoutBottomSell.visibility = View.VISIBLE
                holder.textProceeds.text = "Proceeds here"
            }
            if(transaction.quantity < 0){
                holder.titlePrice.text = "${currency.toUpperCase()} Buy Price"
                holder.titlePair.text = "Due to sell of"
                holder.titleAmount.text = "Amount Added"

                holder.layoutBottomSell.visibility = View.GONE

            }
        }

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