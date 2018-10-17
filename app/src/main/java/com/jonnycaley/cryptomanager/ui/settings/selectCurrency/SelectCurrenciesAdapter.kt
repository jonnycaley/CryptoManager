package com.jonnycaley.cryptomanager.ui.settings.selectCurrency

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.AllCurrencies.Datum
import kotlinx.android.synthetic.main.item_select_fiat.view.*


class SelectCurrenciesAdapter(var currencies: List<Datum>?, var presenter: SelectCurrencyContract.Presenter,  var context: Context?) : RecyclerView.Adapter<SelectCurrenciesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_select_fiat, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = currencies?.get(position)

        holder.setIsRecyclable(false)

        holder.name.text = item?.coinName
        holder.symbol.text = item?.symbol

        holder.itemView.setOnClickListener {
            presenter.saveBaseCurrency(item?.symbol)
        }

    }

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return currencies?.size ?: 0
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val symbol = view.symbol
        val name = view.name
    }
}
