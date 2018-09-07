package com.jonnycaley.cryptomanager.utils

import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import com.jonnycaley.cryptomanager.R
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager


object Utils {

    fun isNetworkConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }

    fun getTimeFrom(date: String?): String {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000'Z'")

        val articleTime: Long
        try {
            articleTime = format.parse(date).time
        } catch (e: ParseException) {
            e.printStackTrace()
            return ""
        }

        val currentTime = Date().time

        val diff = currentTime - articleTime

        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        when {
            seconds < 60 -> return if(seconds.toInt() == 1){
                "$seconds second ago"
            } else {
                "$seconds seconds ago"
            }
            minutes < 60 -> return if(minutes.toInt() == 1){
                "$minutes minute ago"
            } else {
                "$minutes minutes ago"
            }
            hours < 24 -> return if(hours.toInt() == 1){
                "$hours hour ago"
            } else {
                "$hours hours ago"
            }
            else -> return if(days.toInt() == 1){
                "$days day ago"
            } else {
                "$days days ago"
            }
        }

    }

    fun formatPrice(priceAsDouble: Double): String {

        val price = Utils.toDecimals(priceAsDouble, 8).toDouble()

        var priceText: String

        priceText = if(price > 1)
            Utils.toDecimals(priceAsDouble, 2)
        else
            "0${Utils.toDecimals(priceAsDouble, 6)}"

        if(priceText.indexOf(".") != -1 && (priceText.indexOf(".") + 1 == priceText.length -1))
            priceText += "0"

        return priceText
    }


    fun getReadTime(words: Int?): String {
        return "${Integer.valueOf(Math.ceil((words?.div(130)?.toDouble()!!)).toInt())} min read • "
    }

    fun toDecimals(number : Double, decimalPlaces : Int) : String{
        val df = DecimalFormat("#")
        df.maximumFractionDigits = decimalPlaces
        return df.format(number)
    }

    fun formatPercentage(percentChange24h: Float?): String {
        val percentage2DP = String.format("%.2f", percentChange24h)

        return when {
            percentage2DP == "0.00" -> {
                "$percentage2DP%"
//                holder.movement.text = "-"
            }
            percentage2DP.toDouble() > 0 -> {
                "+$percentage2DP%"
            }
            else -> {
                "$percentage2DP%"
            }
        }
    }

    fun hideKeyboardFromActivity(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun hideKeyboardFromFragment(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}