package com.jonnycaley.cryptomanager.ui.crypto.viewpager.general

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.jonnycaley.cryptomanager.data.model.Utils.Chart
import java.text.SimpleDateFormat
import java.util.*

class XAxisValueFormatter(private val chart : Chart, private val aggregate : Int) : IAxisValueFormatter {

    override fun getFormattedValue(value: Float, axis: AxisBase): String {

//        return value.toString()

        return (indexToEpoch(value.toLong()))
    }

    /*
    Function converts index to epoch time
    */
    fun indexToEpoch(index: Long): String {

//      please note - index starts at 0 - 30 from candles left to right.
//      then have to subtract THAT from 30 to give us index's from the current time stamp.
//      then multiple by aggregate to

        val newIndex = GeneralFragment.numOfCandlesticks - index

        val tsSeconds = System.currentTimeMillis() / 1000

        when(chart.measure) {
            GeneralFragment.minuteString -> {

                val rounded = tsSeconds - (tsSeconds % 60) //gets us seconds rounded down to the nearest minute

                val minutesToSubtract = newIndex * 60 * aggregate

                val timeStamp = rounded - minutesToSubtract

                when(aggregate){
                    GeneralFragment.aggregate1H -> {
                        return epoch2DateString(timeStamp, "HH:mm")
                    }
                }

            }
            GeneralFragment.hourString -> {
                val rounded = tsSeconds - (tsSeconds % (60 * 60)) //gets us seconds rounded down to the nearest minute

                val minutesToSubtract = newIndex * (60 * 60) * aggregate

                val timeStamp = rounded - minutesToSubtract

                when(aggregate){
                    GeneralFragment.aggregate1D -> {
                        return epoch2DateString(timeStamp, "HH:mm")
                    }
                    GeneralFragment.aggregate3D -> {
                        return epoch2DateString(timeStamp, "dd MMM")
                    }
                    GeneralFragment.aggregate1W -> {
                        return epoch2DateString(timeStamp, "dd MMM")
                    }
                }
            }
            GeneralFragment.dayString -> {

                val rounded = tsSeconds - (tsSeconds % (60 * 60 * 24)) //gets us seconds rounded down to the nearest minute

                val minutesToSubtract = newIndex * (60 * 60 * 24) * aggregate

                val timeStamp = rounded - minutesToSubtract

                when(aggregate){
                    GeneralFragment.aggregate1M -> {
                        return epoch2DateString(timeStamp, "dd MMM")
                    }
                    GeneralFragment.aggregate3M -> {
                        return epoch2DateString(timeStamp, "MMM")
                    }
                    GeneralFragment.aggregate6M -> {
                        return epoch2DateString(timeStamp, "MMM")
                    }
                    GeneralFragment.aggregate1Y -> {
                        return epoch2DateString(timeStamp, "MMM yyyy")
                    }
                }
            }
        }
        return ""
    }

    /*
    Function converts epoch to date string
    */
    fun epoch2DateString(epochSeconds: Long, formatString: String): String {
        val updatedate = Date(epochSeconds * 1000)
        val format = SimpleDateFormat(formatString)
        return format.format(updatedate)
    }

}