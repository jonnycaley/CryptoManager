package com.jonnycaley.cryptomanager.utils

import android.content.Context
import android.net.ConnectivityManager
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


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

        if (seconds < 60)
            return "$seconds seconds ago"
        else if (minutes < 60)
            return "$minutes minutes ago"
        else if (hours < 24)
            return "$hours hours ago"
        else
            return "$days days ago"

    }

    fun getReadTime(words: Int?): String {
        return "${Integer.valueOf(Math.ceil((words?.div(130)?.toDouble()!!)).toInt())} min read • "
    }

}