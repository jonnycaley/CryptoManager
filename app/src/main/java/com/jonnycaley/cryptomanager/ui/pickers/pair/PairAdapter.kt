package com.jonnycaley.cryptomanager.ui.pickers.pair

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jonnycaley.cryptomanager.R
import kotlinx.android.synthetic.main.item_pair.view.*

class PairAdapter(var pairs: List<String>?, var baseSymbol: String, var view: PickerPairContract.View?, var context: Context?) : RecyclerView.Adapter<PairAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_pair, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val pair =  pairs?.get(position)

        holder.setIsRecyclable(false)

        holder.name.text = "$baseSymbol/$pair"

        holder.itemView.setOnClickListener {
            view?.onPairChosen(pair)
        }
    }

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return pairs?.size ?: 0
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val name = view.name
    }
}
