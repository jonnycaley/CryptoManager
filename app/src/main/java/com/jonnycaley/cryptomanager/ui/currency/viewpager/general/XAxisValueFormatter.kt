package com.jonnycaley.cryptomanager.ui.currency.viewpager.general

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class XAxisValueFormatter(private val timeUnit : String, private val aggregate : Int) : IAxisValueFormatter {

    override fun getFormattedValue(value: Float, axis: AxisBase): String {

//        return value.toString()

        return (indexToEpoch(value.toLong()))
    }

    fun indexToEpoch(index: Long): String {

//      please note - index starts at 0 - 30 from candles left to right.
//      then have to subtract THAT from 30 to give us index's from the current time stamp.
//      then multiple by aggregate to

        val newIndex = GeneralPresenter.numOfCandlesticks - index

        val tsSeconds = System.currentTimeMillis() / 1000

        when(timeUnit) {
            GeneralPresenter.minuteString -> {

                val rounded = tsSeconds - (tsSeconds % 60) //gets us seconds rounded down to the nearest minute

                val minutesToSubtract = newIndex * 60 * aggregate

                val timeStamp = rounded - minutesToSubtract

                when(aggregate){
                    GeneralPresenter.aggregate1H -> {
                        return epoch2DateString(timeStamp, "HH:mm")
                    }
                }

            }
            GeneralPresenter.hourString -> {
                val rounded = tsSeconds - (tsSeconds % (60 * 60)) //gets us seconds rounded down to the nearest minute

                val minutesToSubtract = newIndex * (60 * 60) * aggregate

                val timeStamp = rounded - minutesToSubtract

                when(aggregate){
                    GeneralPresenter.aggregate1D -> {
                        return epoch2DateString(timeStamp, "HH:mm")
                    }
                    GeneralPresenter.aggregate3D -> {
                        return epoch2DateString(timeStamp, "dd MMM")
                    }
                    GeneralPresenter.aggregate1W -> {
                        return epoch2DateString(timeStamp, "dd MMM")
                    }
                }
            }
            GeneralPresenter.dayString -> {

                val rounded = tsSeconds - (tsSeconds % (60 * 60 * 24)) //gets us seconds rounded down to the nearest minute

                val minutesToSubtract = newIndex * (60 * 60 * 24) * aggregate

                val timeStamp = rounded - minutesToSubtract

                when(aggregate){
                    GeneralPresenter.aggregate1M -> {
                        return epoch2DateString(timeStamp, "dd MMM")
                    }
                    GeneralPresenter.aggregate3M -> {
                        return epoch2DateString(timeStamp, "MMM")
                    }
                    GeneralPresenter.aggregate6M -> {
                        return epoch2DateString(timeStamp, "MMM")
                    }
                    GeneralPresenter.aggregate1Y -> {
                        return epoch2DateString(timeStamp, "MMM yyyy")
                    }
//                    GeneralPresenter.aggregateAll -> {
//                        return epoch2DateString(timeStamp, "HH:mm")
//                    }
                }
            }
        }
        return ""
    }

    fun epoch2DateString(epochSeconds: Long, formatString: String): String {
        val updatedate = Date(epochSeconds * 1000)
        val format = SimpleDateFormat(formatString)
        return format.format(updatedate)
    }

}