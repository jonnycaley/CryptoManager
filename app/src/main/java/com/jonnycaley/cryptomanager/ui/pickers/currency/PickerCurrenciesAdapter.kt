package com.jonnycaley.cryptomanager.ui.pickers.currency

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.AllCurrencies.Datum
import com.jonnycaley.cryptomanager.utils.CircleTransform
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_search_currencies.view.*


class PickerCurrenciesAdapter(var currencies: List<Datum>?, var context: Context?, var view: PickerCurrencyContract.View) : RecyclerView.Adapter<PickerCurrenciesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_search_currencies, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = currencies?.get(position)

        holder.setIsRecyclable(false)

        Picasso.with(context)
                .load(R.drawable.circle)
                .fit()
                .centerCrop()
                .transform(CircleTransform())
                .placeholder(R.drawable.circle)
                .into(holder.image)

        holder.name.text = item?.coinName
        holder.symbol.text = item?.symbol

        holder.imageText.visibility = View.VISIBLE
        holder.imageText.text = item?.symbol
        holder.imageText.setBackgroundResource(android.R.color.transparent)

        holder.itemView.setOnClickListener {
            view.onPickerChosen(item?.symbol)
        }
    }

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return currencies?.size ?: 0
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val image = view.image
        val name = view.name
        val symbol = view.symbol
        val imageText = view.image_text
    }
}