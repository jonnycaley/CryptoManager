package com.jonnycaley.cryptomanager.ui.settings

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jonnycaley.cryptomanager.R


class SettingsAdapter(val settings: ArrayList<String>?, val context: Context?) : RecyclerView.Adapter<SettingsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_settings, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val setting = settings?.get(position)

        holder.setIsRecyclable(false)

        holder.itemView.setOnClickListener {

        }
    }

    override fun getItemCount(): Int {
        return settings?.size ?: 0
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
//        val currency = view.currency
    }
}