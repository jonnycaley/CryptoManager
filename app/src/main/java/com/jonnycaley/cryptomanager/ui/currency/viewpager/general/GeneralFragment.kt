package com.jonnycaley.cryptomanager.ui.currency.viewpager.general

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.ceryle.radiorealbutton.RadioRealButtonGroup
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.components.XAxis
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

    private lateinit var presenter : GeneralContract.Presenter

    private var currency: Currency? = null

    lateinit var mView : View

    val candleStickChart : CandleStickChart by lazy { mView.findViewById<CandleStickChart>(R.id.chart_candlestick) }

    val radioGroup : RadioRealButtonGroup by lazy { mView.findViewById<RadioRealButtonGroup>(R.id.radio_group) }

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

        setUpCandleStick()

        radioGroup.setOnPositionChangedListener { button, currentPosition, lastPosition ->
            when(currentPosition){
                0 -> {
                    setChartBottomLableCount(6, true)
                    setChartMinimumZero(false)
                    presenter.clearChartDisposable()
                    presenter.getCurrencyData(GeneralPresenter.timeMeasure1H, getSymbol(), GeneralPresenter.conversionUSD, GeneralPresenter.numOfCandlesticks, GeneralPresenter.aggregate1H)
                }
                1 -> {
                    setChartBottomLableCount(6, true)
                    setChartMinimumZero(false)
                    presenter.clearChartDisposable()
                    presenter.getCurrencyData(GeneralPresenter.timeMeasure1D, getSymbol(), GeneralPresenter.conversionUSD, GeneralPresenter.numOfCandlesticks, GeneralPresenter.aggregate1D)
                }
                2 -> {
                    setChartBottomLableCount(4, true)
                    setChartMinimumZero(false)
                    presenter.clearChartDisposable()
                    presenter.getCurrencyData(GeneralPresenter.timeMeasure3D, getSymbol(), GeneralPresenter.conversionUSD, GeneralPresenter.numOfCandlesticks, GeneralPresenter.aggregate3D)
                }
                3 -> {
                    setChartBottomLableCount(8, true)
                    setChartMinimumZero(false)
                    presenter.clearChartDisposable()
                    presenter.getCurrencyData(GeneralPresenter.timeMeasure1W, getSymbol(), GeneralPresenter.conversionUSD, GeneralPresenter.numOfCandlesticks, GeneralPresenter.aggregate1W)
                }
                4 -> {
                    setChartBottomLableCount(6, true)
                    setChartMinimumZero(false)
                    presenter.clearChartDisposable()
                    presenter.getCurrencyData(GeneralPresenter.timeMeasure1M, getSymbol(), GeneralPresenter.conversionUSD, GeneralPresenter.numOfCandlesticks, GeneralPresenter.aggregate1M)
                }
                5 -> {
                    setChartBottomLableCount(4, true)
                    setChartMinimumZero(false)
                    presenter.clearChartDisposable()
                    presenter.getCurrencyData(GeneralPresenter.timeMeasur3M, getSymbol(), GeneralPresenter.conversionUSD, GeneralPresenter.numOfCandlesticks, GeneralPresenter.aggregate3M)
                }
                6 -> {
                    setChartBottomLableCount(7, true)
                    setChartMinimumZero(false)
                    presenter.clearChartDisposable()
                    presenter.getCurrencyData(GeneralPresenter.timeMeasure6M, getSymbol(), GeneralPresenter.conversionUSD, GeneralPresenter.numOfCandlesticks, GeneralPresenter.aggregate6M)
                }
                7 -> {
                    setChartBottomLableCount(5, true)
                    setChartMinimumZero(true)
                    presenter.clearChartDisposable()
                    presenter.getCurrencyData(GeneralPresenter.timeMeasure1Y, getSymbol(), GeneralPresenter.conversionUSD, GeneralPresenter.numOfCandlesticks, GeneralPresenter.aggregate1Y)
                }
                8 -> {
                    setChartBottomLableCount(6, true)
                    setChartMinimumZero(true)
                    presenter.clearChartDisposable()
//                    presenter.getCurrencyData(GeneralPresenter.timeMeasure1H, getSymbol(), GeneralPresenter.conversionUSD, GeneralPresenter.numOfCandlesticks, GeneralPresenter.aggregate1H)
                    //TODO: Determine how we are going to get the time scale to search over for new currencies
                }
            }
        }

//      in the presenter - compositeDisposable.clear to clear all requests and then start a new one...

        presenter = GeneralPresenter(GeneralDataManager.getInstance(context!!), this)
        presenter.attachView()
    }

    private fun setChartMinimumZero(isMinumumZero: Boolean) {
        if(isMinumumZero)
            candleStickChart.axisLeft.axisMinimum = 0F
        else
            candleStickChart.axisLeft.resetAxisMinimum()
    }

    private fun setChartBottomLableCount(count: Int, forced: Boolean) {
        candleStickChart.xAxis.setLabelCount(count, forced)
    }

    private fun setUpCandleStick() {

        candleStickChart.setDrawGridBackground(false)
        candleStickChart.legend.isEnabled = false
        candleStickChart.setPinchZoom(true)
        candleStickChart.isDragEnabled = true
        candleStickChart.setScaleEnabled(true)
        candleStickChart.setTouchEnabled(true)

        candleStickChart.description.isEnabled = false


        val xAxis = candleStickChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setLabelCount(6, false)

        val yAxisLeft = candleStickChart.axisLeft
        yAxisLeft.setDrawGridLines(true)
        yAxisLeft.setLabelCount(4, true)

        val yAxisRight = candleStickChart.axisRight
        yAxisRight.isEnabled = false

    }

    override fun showCandlestickChart(response: Data, timeUnit: String, aggregate: Int) {

        val entries = ArrayList<CandleEntry>()

        for(i in 0 until response.data?.size!!){
            val entry = response.data!![i]

            entries.add(CandleEntry(i.toFloat(), entry.high?.toFloat()!!, entry.low?.toFloat()!!, entry.open?.toFloat()!!, entry.close?.toFloat()!!))
        }

        val xAxis = candleStickChart.xAxis
        xAxis.valueFormatter = XAxisValueFormatter(timeUnit, aggregate)

        val dataSet = CandleDataSet(entries, "Label")

//        dataSet.setDrawIcons(false)
        dataSet.axisDependency = YAxis.AxisDependency.LEFT
        dataSet.shadowColor = Color.DKGRAY
        dataSet.decreasingColor = resources.getColor(R.color.candlestick_red)
        dataSet.decreasingPaintStyle = Paint.Style.FILL
        dataSet.increasingColor = resources.getColor(R.color.candlestick_green)
        dataSet.increasingPaintStyle = Paint.Style.FILL
        dataSet.neutralColor = Color.DKGRAY

        dataSet.setDrawHighlightIndicators(true)
        dataSet.setDrawValues(false)

        dataSet.shadowWidth = 0.7F
        dataSet.highlightLineWidth = 3F
        dataSet.formLineWidth = 2F
        dataSet.barSpace = 0.1F

        val candleData = CandleData(dataSet)

        candleStickChart.data = candleData
        candleStickChart.invalidate()
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
