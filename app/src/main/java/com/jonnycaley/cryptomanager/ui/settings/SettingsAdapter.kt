package com.jonnycaley.cryptomanager.ui.settings

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jonnycaley.cryptomanager.R
import kotlinx.android.synthetic.main.item_settings.view.*
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity
import com.jonnycaley.cryptomanager.ui.settings.bookmarkedArticles.BookmarkedArticlesActivity
import com.jonnycaley.cryptomanager.ui.settings.selectCurrency.SelectCurrencyActivity
import com.jonnycaley.cryptomanager.ui.settings.transactionHistory.TransactionHistoryActivity
import com.jonnycaley.cryptomanager.utils.Utils
import java.math.BigDecimal

class SettingsAdapter(val settings: ArrayList<String>, val fiatString: String?, val fiatRate: BigDecimal?, val version: String, val presenter: SettingsContract.Presenter, val context: Context?) : androidx.recyclerview.widget.RecyclerView.Adapter<SettingsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_settings, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val setting = settings[position]

        if((position != settings.size-1) || (settings.size == 1))
            holder.borderPartial.visibility = View.VISIBLE
        else
            holder.borderFull.visibility = View.VISIBLE

        holder.setIsRecyclable(false)

        if(Utils.isDarkTheme())
            holder.arrow.setImageResource(R.drawable.next_white)

        when(setting) {
            context?.resources?.getString(R.string.settings_select_base_currency) -> {
                holder.setting.text = "${context?.resources?.getString(R.string.settings_select_base_currency)} $fiatString ${Utils.getPriceTextAbs(fiatRate?.toDouble(), "")}"
            }
            context?.resources?.getString(R.string.settings_version) -> {
                holder.setting.text = "Version $version"
                holder.arrow.visibility = View.GONE
            }
            else -> {
                holder.setting.text = setting
            }
        }

        holder.itemView.setOnClickListener {
            when(setting){
                context?.resources?.getString(R.string.settings_saved_articles) -> {
                    context.let { context -> startActivity(context, Intent(context, BookmarkedArticlesActivity::class.java), null) }
                }
                context?.resources?.getString(R.string.settings_delete_all_articles) ->{
                    showDeletePArticlesConfirmation()
                }
                context?.resources?.getString(R.string.settings_delete_portfolio) ->{
                    showDeletePortfolioConfirmation()
                }
                context?.resources?.getString(R.string.settings_select_base_currency) ->{
                    context.let { context -> startActivity(context, Intent(context, SelectCurrencyActivity::class.java), null) }
                }
                context?.resources?.getString(R.string.settings_transaction_history) ->{
                    context.let { context -> startActivity(context, Intent(context, TransactionHistoryActivity::class.java), null) }
                }
                context?.resources?.getString(R.string.settings_send_feedback) ->{
                    val intent = Intent(Intent.ACTION_SEND)
                    val recipients = arrayOf("jonathancaley@hotmail.co.uk")
                    intent.putExtra(Intent.EXTRA_EMAIL, recipients)
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Crypot Manager Feedback")
                    intent.type = "text/html"
                    context.let { context -> startActivity(context, Intent.createChooser(intent, "Send mail"), null) }
                }
                context?.resources?.getString(R.string.settings_share_app) ->{
                    val sharingIntent = Intent(android.content.Intent.ACTION_SEND)
                    sharingIntent.type = "text/plain"
                    val shareBody = "Crypto Manager - Manager your protfolio, track live prices & follow the latest news all in one place! https://play.google.com/store/apps/details?id=com.jonnycaley.cryptomanager&hl=en_GB"
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody)
                    context.let { context -> startActivity(context, Intent.createChooser(sharingIntent, "Share via"), null) }
                }
                context?.resources?.getString(R.string.settings_predictor) ->{
                    val builder = Utils.webViewBuilder(context)
                    builder.show("https://jonnycaley.github.io/Crypto.html")
                }
                context?.resources?.getString(R.string.settings_review_app) ->{
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("market://details?id=com.jonnycaley.cryptomanager")
                    context.let { context -> startActivity(context, intent, null) }
                }
            }
        }
    }

    fun showDeletePArticlesConfirmation() {
        val diaBox = askDeleteArticles()
        diaBox.show()
    }


    fun askDeleteArticles(): AlertDialog {

        return AlertDialog.Builder(context)
                //set message, title, and icon
                .setTitle("Delete Articles")
                .setMessage("Are you sure you want to delete all articles?")
                .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, whichButton ->
                    //your deleting code
                    presenter.deleteSavedArticles()
                    dialog.dismiss()
                })
                .setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss() })
                .create()

    }

    fun showDeletePortfolioConfirmation() {
        val diaBox = askDeletePortfolio()
        diaBox.show()
    }

    fun askDeletePortfolio(): AlertDialog {

        return AlertDialog.Builder(context)
                //set message, title, and icon
                .setTitle("Delete Portfolio")
                .setMessage("Are you sure you want to delete your portfolio?")
                .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, whichButton ->
                    //your deleting code
                    presenter.deletePortfolio()
                    dialog.dismiss()
                })
                .setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss() })
                .create()

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
        val arrow = view.arrow
    }
}