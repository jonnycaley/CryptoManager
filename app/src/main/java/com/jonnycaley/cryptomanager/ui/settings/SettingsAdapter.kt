package com.jonnycaley.cryptomanager.ui.settings

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jonnycaley.cryptomanager.R
import kotlinx.android.synthetic.main.item_settings.view.*
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity
import com.jonnycaley.cryptomanager.ui.settings.savedArticles.SavedArticlesActivity
import com.jonnycaley.cryptomanager.ui.settings.selectCurrency.SelectCurrencyActivity
import androidx.core.content.ContextCompat.startActivity
import com.jonnycaley.cryptomanager.ui.settings.transactionHistory.TransactionHistoryActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.ContextCompat.startActivity
import java.math.BigDecimal

class SettingsAdapter(val settings: ArrayList<String>, val fiatString: String?, val fiatRate: BigDecimal?, val version: String, val presenter: SettingsContract.Presenter, val context: Context?) : androidx.recyclerview.widget.RecyclerView.Adapter<SettingsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_settings, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val setting = settings[position]

        if(position != settings.size-1)
            holder.borderPartial.visibility = View.VISIBLE
        else
            holder.borderFull.visibility = View.VISIBLE

        holder.setIsRecyclable(false)

        when(setting) {
            context?.resources?.getString(R.string.settings_select_base_currency) -> {
                holder.setting.text = "${context?.resources?.getString(R.string.settings_select_base_currency)} $fiatString $fiatRate"
            }
            context?.resources?.getString(R.string.settings_version) -> {
                holder.setting.text = "Version $version"
            }
            else -> {
                holder.setting.text = setting
            }
        }

        holder.itemView.setOnClickListener {
            when(setting){
                context?.resources?.getString(R.string.settings_saved_articles) -> {
                    context.let { context -> startActivity(context, Intent(context, SavedArticlesActivity::class.java), null) }
                }
                context?.resources?.getString(R.string.settings_delete_all_articles) ->{
                    presenter.deleteSavedArticles()
                }
                context?.resources?.getString(R.string.settings_delete_portfolio) ->{
                    presenter.deletePortfolio()
                }
                context?.resources?.getString(R.string.settings_select_base_currency) ->{
                    context.let { context -> startActivity(context, Intent(context, SelectCurrencyActivity::class.java), null) }
                }
                context?.resources?.getString(R.string.settings_send_feedback) ->{
                    val intent = Intent(Intent.ACTION_SEND)
                    val recipients = arrayOf("jonathancaley@hotmail.co.uk")
                    intent.putExtra(Intent.EXTRA_EMAIL, recipients)
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Crypot Manager Feedback")
                    intent.type = "text/html"
                    context.let { context -> startActivity(context, Intent.createChooser(intent, "Send mail"), null) }
                }
                context?.resources?.getString(R.string.settings_transaction_history) ->{
                    context.let { context -> startActivity(context, Intent(context, TransactionHistoryActivity::class.java), null) }
                }
                context?.resources?.getString(R.string.settings_share_app) ->{
                    val sharingIntent = Intent(android.content.Intent.ACTION_SEND)
                    sharingIntent.type = "text/plain"
                    val shareBody = "Crypto Manager - Manager your protfolio, track live prices & follow the latest news all in one place! https://play.google.com/store/apps/details?id=com.instagram.android&hl=en_GB"
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody)
                    context.let { context -> startActivity(context, Intent.createChooser(sharingIntent, "Share via"), null) }
                }
                context?.resources?.getString(R.string.settings_review_app) ->{
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("market://details?id=com.instagram.android")
                    context.let { context -> startActivity(context, intent, null) }
                }
            }
        }
    }

    fun refresh(){
        var temSettings = ArrayList<String>()
        settings.forEach { temSettings.add(it) }
        settings.clear()
        settings.addAll(temSettings)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return settings.size
    }

    class ViewHolder (view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val setting = view.setting
        val borderFull = view.layout_border
        val borderPartial = view.layout_border_partial
    }
}