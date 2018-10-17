package com.jonnycaley.cryptomanager.ui.settings

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jonnycaley.cryptomanager.R
import kotlinx.android.synthetic.main.item_settings.view.*
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import com.jonnycaley.cryptomanager.ui.settings.savedArticles.SavedArticlesActivity
import com.jonnycaley.cryptomanager.ui.settings.selectCurrency.SelectCurrencyActivity

class SettingsAdapter(val settings: ArrayList<String>?, val presenter: SettingsContract.Presenter, val context: Context?) : RecyclerView.Adapter<SettingsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_settings, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val setting = settings?.get(position)

        holder.setIsRecyclable(false)

        holder.setting.text = setting

        holder.itemView.setOnClickListener {
            when(position){
                0 -> {
                    startActivity(context!!, Intent(context, SavedArticlesActivity::class.java), null)
                }
                1 ->{
                    presenter.deleteSavedArticles()
                }
                2 ->{
                    presenter.deletePortfolio()
                }
                3 -> {
                    startActivity(context!!, Intent(context, SelectCurrencyActivity::class.java), null)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return settings?.size ?: 0
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val setting = view.setting
    }
}