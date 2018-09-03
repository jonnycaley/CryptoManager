package com.jonnycaley.cryptomanager.ui.currency.viewpager.general

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Currency
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.HistoricalData.Data
import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import java.text.SimpleDateFormat
import java.util.*


class GeneralFragment : Fragment(), GeneralContract.View {

    lateinit var presenter : BasePresenter

    private var currency: Currency? = null

    lateinit var mView : View

    val candleStickChart : CandleStickChart by lazy { mView.findViewById<CandleStickChart>(R.id.chart_candlestick) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currency = it.getSerializable(ARG_PARAM1) as Currency
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_general, container, false)
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        println("Attaching presenter")
        presenter = GeneralPresenter(GeneralDataManager.getInstance(context!!), this)
        presenter.attachView()
    }

    override fun showCandlestickChart(response: Data) {

        val entries = ArrayList<CandleEntry>()

//        for (i in 0 until response.data?.size!!){
//            var entry = response.data!![i]
//            entries.add(CandleEntry(i.toFloat(), entry.high?.toFloat()!!, entry.low?.toFloat()!!, entry.open?.toFloat()!!, entry.close?.toFloat()!!))
//        }

        response.data?.forEach { entry ->
            entries.add(CandleEntry(entry.time!!.toFloat(), entry.high?.toFloat()!!, entry.low?.toFloat()!!, entry.open?.toFloat()!!, entry.close?.toFloat()!!))
        }

        candleStickChart.setDrawGridBackground(false)
        candleStickChart.legend.isEnabled = false


        val dataSet = CandleDataSet(entries, "Label")

        dataSet.setDrawIcons(false)
        dataSet.axisDependency = YAxis.AxisDependency.LEFT
        dataSet.shadowColor = Color.DKGRAY
        dataSet.shadowWidth = 0.7f
        dataSet.decreasingColor = Color.RED
        dataSet.decreasingPaintStyle = Paint.Style.FILL
        dataSet.increasingColor = Color.GREEN
        dataSet.increasingPaintStyle = Paint.Style.FILL
        dataSet.neutralColor = Color.BLUE

        val candleData = CandleData(dataSet)

        candleStickChart.setPinchZoom(true)

        candleStickChart.data = candleData
        candleStickChart.invalidate()
    }


    fun getDate(milliSeconds : Long, dateFormat : String) : String
    {
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat, Locale.UK)

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }




    override fun getSymbol(): String {
        return currency?.symbol!!
    }

    override fun setPresenter(presenter: GeneralContract.Presenter) {
        this.presenter = checkNotNull(presenter)
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: Currency) =
                GeneralFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable(ARG_PARAM1, param1)
                    }
                }

        private const val ARG_PARAM1 = "currency"
    }
}
