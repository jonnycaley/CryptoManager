package com.jonnycaley.cryptomanager.ui.pickers.exchange

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.Exchanges.Exchange
import kotlinx.android.synthetic.main.item_search_exchange.view.*

class ExchangesAdapter(var exchanges: List<Exchange>?, var view: PickerExchangeContract.View?, var context: Context?) : androidx.recyclerview.widget.RecyclerView.Adapter<ExchangesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_search_exchange, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val exchange = exchanges?.get(position)

        holder.setIsRecyclable(false)

        holder.name.text = exchange?.name

        holder.itemView.setOnClickListener {
            view?.onExchangeChosen(exchange?.name)
        }
    }

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return exchanges?.size ?: 0
    }

    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val name = view.name
    }
}
