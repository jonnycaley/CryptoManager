package com.jonnycaley.cryptomanager.ui.search

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.AllCurrencies.Datum
import com.jonnycaley.cryptomanager.ui.transactions.currency.CurrencyTransactionArgs
import com.jonnycaley.cryptomanager.ui.transactions.fiat.FiatTransactionArgs
import com.jonnycaley.cryptomanager.utils.CircleTransform
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_search_currencies.view.*

class SearchCurrenciesAdapter(var currencies: List<Datum>?, var baseImageUrl: String?, var baseUrl: String?, var context: Context?) : RecyclerView.Adapter<SearchCurrenciesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_search_currencies, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = currencies?.get(position)

        holder.setIsRecyclable(false)

        holder.name.text = item?.coinName
        holder.symbol.text = item?.symbol

        Picasso.with(context)
                .load(baseImageUrl + item?.imageUrl)
                .fit()
                .centerCrop()
                .transform(CircleTransform())
                .placeholder(R.drawable.circle)
                .into(holder.image)

        if(item?.imageUrl == null){
            holder.imageText.visibility = View.VISIBLE
            holder.imageText.text = item?.symbol
            holder.imageText.setBackgroundResource(android.R.color.transparent)
        }

        if (baseUrl == null) {
            holder.itemView.setOnClickListener {
                FiatTransactionArgs(item?.symbol!!).launch(context!!)
            }

        } else {
            holder.itemView.setOnClickListener {
                CurrencyTransactionArgs(item!!, baseImageUrl, baseUrl).launch(context!!)
            }
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