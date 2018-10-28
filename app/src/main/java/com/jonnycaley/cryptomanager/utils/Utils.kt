package com.jonnycaley.cryptomanager.utils

import android.content.Context
import android.net.ConnectivityManager
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import java.math.BigDecimal
import java.text.NumberFormat

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

    fun formatPrice(priceAsDouble: BigDecimal): String {

        if(priceAsDouble == 0.toBigDecimal())
            return "0"

        var absPrice = priceAsDouble

        var priceSubtractor = false

        if(priceAsDouble < 0.toBigDecimal() ) {
            priceSubtractor = true
            absPrice = priceAsDouble * (-1).toBigDecimal()
        }

        val price = Utils.toDecimals(absPrice, 8).toDouble()

        var priceText: String

        priceText = if(price > 1)
            Utils.toDecimals(absPrice, 2)
        else
            "0${Utils.toDecimals(absPrice, 6)}"

        if(priceText.indexOf(".") != -1 && (priceText.indexOf(".") + 1 == priceText.length -1))
            priceText += "0"

        if(priceSubtractor)
            priceText = "-$priceText"

        return priceText
    }


    fun getReadTime(words: Int?): String {
        if(words != null)
            return "${Integer.valueOf(Math.ceil((words?.div(130)?.toDouble()!!)).toInt())} min read • "
        else
            return "1 min read • "
    }

    fun toDecimals(number : BigDecimal, decimalPlaces : Int) : String{
        val df = DecimalFormat("#")
        df.maximumFractionDigits = decimalPlaces
        return df.format(number)
    }

    fun formatPercentage(percentChange24h: BigDecimal?): String {
        val percentage2DP = String.format("%.2f", percentChange24h)

//        val percentage2DP = DecimalFormat("#0.00").format(percentChange24h)

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


    fun getFiatName(fiat: String?): String? {
        when(fiat){
            "AUD" ->{return "Australian Dollar"}
            "BGN" ->{return "Bulgarian Lev"}
            "BRL" ->{return "Brazilian Real"}
            "CAD" ->{return "Canadian Dollar"}
            "CHF" ->{return "Swiss Franc"}
            "CNY" ->{return "Chinese Yuan"}
            "CZK" ->{return "Czech Koruna"}
            "DKK" ->{return "Danish Krone"}
            "GBP" ->{return "Pound sterling"}
            "HKD" ->{return "Hong Kong Dollar"}
            "HRK" ->{return "Croatian Kuna"}
            "HUF" ->{return "Hungarian Forint"}
            "IDR" ->{return "Indonesian Rupiah"}
            "ILS" ->{return "Israeli New Shekel"}
            "INR" ->{return "Indian Rupee"}
            "ISK" ->{return "Icelandic Króna"}
            "JPY" ->{return "Japanese Yen"}
            "KRW" ->{return "South Korean won"}
            "MXN" ->{return "Mexican Peso"}
            "MYR" ->{return "Malaysian Ringgit"}
            "NOK" ->{return "Norwegian Krone"}
            "NZD" ->{return "New Zealand Dollar"}
            "PHP" ->{return "Philippine Piso"}
            "PLN" ->{return "Poland złoty"}
            "RON" ->{return "Romanian Leu"}
            "RUB" ->{return "Russian Ruble"}
            "SEK" ->{return "Swedish Krona"}
            "SGD" ->{return "Singapore Dollar"}
            "THB" ->{return "Thai Baht"}
            "TRY" ->{return "Turkish lira"}
            "ZAR" ->{return "South African Rand"}
            "USD" ->{return "United States Dollar"}
            "EUR" ->{return "Euro"}
        }
        return ""
    }

    fun getFiatSymbol(fiat: String?): String {
        when(fiat){
            "AUD" ->{return "A$"}
            "BGN" ->{return "Лв."}
            "BRL" ->{return "R$"}
            "CAD" ->{return "C$"}
            "CHF" ->{return "CHF"}
            "CNY" ->{return "¥"}
            "CZK" ->{return "Kč"}
            "DKK" ->{return "Kr."}
            "GBP" ->{return "£"}
            "HKD" ->{return "HK$"}
            "HRK" ->{return "kn"}
            "HUF" ->{return "Ft"}
            "IDR" ->{return "Rp"}
            "ILS" ->{return "₪"}
            "INR" ->{return "₹"}
            "ISK" ->{return "ISK"}
            "JPY" ->{return "¥"}
            "KRW" ->{return "₩"}
            "MXN" ->{return "Mex$"}
            "MYR" ->{return "RM"}
            "NOK" ->{return "kr"}
            "NZD" ->{return "NZ$"}
            "PHP" ->{return "₱"}
            "PLN" ->{return "zł"}
            "RON" ->{return "lei"}
            "RUB" ->{return "₽"}
            "SEK" ->{return "kr"}
            "SGD" ->{return "SG$"}
            "THB" ->{return "฿"}
            "TRY" ->{return "₺"}
            "ZAR" ->{return "R"}
            "EUR" ->{return "€"}
            "USD" ->{return "$"}
        }
        return ""
    }


    fun formatDate(date: Date): CharSequence? {

        val format = SimpleDateFormat(Constants.dateFormat)

        return format.format(date)
    }
}