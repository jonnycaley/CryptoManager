package com.jonnycaley.cryptomanager.ui.settings

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jonnycaley.cryptomanager.R
import kotlinx.android.synthetic.main.item_settings.view.*
import android.content.Intent
import android.net.Uri
import android.support.v4.content.ContextCompat.startActivity
import com.jonnycaley.cryptomanager.ui.settings.savedArticles.SavedArticlesActivity
import com.jonnycaley.cryptomanager.ui.settings.selectCurrency.SelectCurrencyActivity
import android.support.v4.content.ContextCompat.startActivity
import com.jonnycaley.cryptomanager.ui.settings.transactionHistory.TransactionHistoryActivity
import android.support.v4.content.ContextCompat.startActivity
import android.support.v4.content.ContextCompat.startActivity

class SettingsAdapter(val settings: ArrayList<String>, val presenter: SettingsContract.Presenter, val context: Context?) : RecyclerView.Adapter<SettingsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_settings, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val setting = settings.get(position)

        holder.setIsRecyclable(false)

        holder.setting.text = setting

        holder.itemView.setOnClickListener {
            when(position){
                0 -> {
                    context?.let { context -> startActivity(context, Intent(context, SavedArticlesActivity::class.java), null) }
                }
                1 ->{
                    presenter.deleteSavedArticles()
                }
                2 ->{
                    presenter.deletePortfolio()
                }
                3 -> {
                    context?.let { context -> startActivity(context, Intent(context, SelectCurrencyActivity::class.java), null) }
                }
                4 -> {
                    val intent = Intent(Intent.ACTION_SEND)
                    val recipients = arrayOf("jonathancaley@hotmail.co.uk")
                    intent.putExtra(Intent.EXTRA_EMAIL, recipients)
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Crypot Manager Feedback")
                    intent.type = "text/html"
                    context?.let { context -> startActivity(context, Intent.createChooser(intent, "Send mail"), null) }
                }
                5 -> {
                    context?.let { context -> startActivity(context, Intent(context, TransactionHistoryActivity::class.java), null) }
                }
                6 -> {
                    val sharingIntent = Intent(android.content.Intent.ACTION_SEND)
                    sharingIntent.type = "text/plain"
                    val shareBody = "Crypto Manager - Manager your protfolio, track live prices & follow the latest news all in one place! https://play.google.com/store/apps/details?id=com.instagram.android&hl=en_GB"
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody)
                    context?.let { context -> startActivity(context, Intent.createChooser(sharingIntent, "Share via"), null) }
                }
                7 -> {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("market://details?id=com.instagram.android")
                    context?.let { context -> startActivity(context, intent, null) }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return settings.size
    }

    class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val setting = view.setting
    }
}