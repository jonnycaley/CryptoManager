package com.jonnycaley.cryptomanager.ui.crypto.viewpager.general

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import co.ceryle.radiorealbutton.RadioRealButtonGroup
import com.appyvet.rangebar.RangeBar
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.GeneralData.Data
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.HistoricalData.HistoricalData
import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.News.Article
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.Rate
import com.jonnycaley.cryptomanager.data.model.Utils.Chart
import com.jonnycaley.cryptomanager.utils.Utils
import java.math.BigDecimal
import java.text.DecimalFormat
import kotlin.collections.ArrayList
import kotlin.math.absoluteValue

class GeneralFragment : Fragment(), GeneralContract.View, SwipeRefreshLayout.OnRefreshListener {

    private lateinit var presenter: GeneralContract.Presenter

    private var currencySymbol: String? = null

    lateinit var mView: View

    lateinit var articlesVerticalAdapter: GeneralArticlesVerticalAdapter

    val price: TextView by lazy { mView.findViewById<TextView>(R.id.price) }
    val change: TextView by lazy { mView.findViewById<TextView>(R.id.change) }

    val scrollLayout by lazy { mView.findViewById<android.support.v4.widget.SwipeRefreshLayout>(R.id.swipelayout) }

    val recyclerViewNews: RecyclerView by lazy { mView.findViewById<RecyclerView>(R.id.recycler_view_news) }

    val radioGroup: RadioRealButtonGroup by lazy { mView.findViewById<RadioRealButtonGroup>(R.id.radio_group) }

    val candleStickChart: CandleStickChart by lazy { mView.findViewById<CandleStickChart>(R.id.chart_candlestick) }

    val textMarketCap: TextView by lazy { mView.findViewById<TextView>(R.id.text_market_cap) }
    val text24hHigh: TextView by lazy { mView.findViewById<TextView>(R.id.text_24h_high) }
    val text24hLow: TextView by lazy { mView.findViewById<TextView>(R.id.text_24h_low) }
    val text24hChange: TextView by lazy { mView.findViewById<TextView>(R.id.text_24h_change) }
    val textCirculatingSupply: TextView by lazy { mView.findViewById<TextView>(R.id.text_circulating_supply) }
    val text24hVolume: TextView by lazy { mView.findViewById<TextView>(R.id.text_24h_volume) }

    val rangeBar: RangeBar by lazy { mView.findViewById<RangeBar>(R.id.rangebar) }

    var selectedChartFrame = Chart(numOfCandlesticks, aggregate1H, minuteString)

    var choseGraphPeriod = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currencySymbol = it.getSerializable(ARG_PARAM1) as String
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    var mLayoutManager : LinearLayoutManager? = null

    override fun updateSavedArticles(articles: ArrayList<Article>) {

        if(mLayoutManager != null){
            articlesVerticalAdapter.savedArticles = articles
            articlesVerticalAdapter.notifyDataSetChanged()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_general, container, false)
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        setUpTextViews()
        setUpGraphTimeChoices()
        setUpCandleStick()

        scrollLayout.setOnRefreshListener(this)

        presenter = GeneralPresenter(GeneralDataManager.getInstance(context!!), this)
        presenter.attachView()
    }

    override fun onRefresh() {
        presenter.getData()
    }

    override fun hideRefreshing() {
        scrollLayout.isRefreshing = false
    }

    override fun getSelectedChartTimeFrame(): Chart {
        return selectedChartFrame
    }

    override fun showVolume(vOLUME24HOUR: String, baseFiat: Rate) {

        val rate = baseFiat.rate ?: 1.toBigDecimal()

        val volume = vOLUME24HOUR?.toBigDecimal()?.times(rate)

        val formattedString = formatPrice(volume)

        text24hVolume.text = "${Utils.getFiatSymbol(baseFiat.fiat)}$formattedString"
    }

    @SuppressLint("SetTextI18n")
    override fun showGlobalData(data: Data?, baseFiat: Rate) {

        val rate = baseFiat.rate ?: 1.toBigDecimal()

        textCirculatingSupply.text = DecimalFormat("#,###,###").format(data?.uSD?.sUPPLY?.toDouble())

        textMarketCap.text = "${Utils.getFiatSymbol(baseFiat.fiat)}${formatPrice(data?.uSD?.mKTCAP?.toBigDecimal()?.times(rate))}"

        text24hVolume.text = "${Utils.getFiatSymbol(baseFiat.fiat)}${formatPrice(data?.uSD?.vOLUME24HOUR?.toBigDecimal()?.times(rate))}"

        text24hHigh.text = "${Utils.getFiatSymbol(baseFiat.fiat)}${getPriceText(data?.uSD?.hIGH24HOUR?.toBigDecimal()?.times(rate)?.times(rate)?.toDouble())}"

        text24hLow.text = "${Utils.getFiatSymbol(baseFiat.fiat)}${getPriceText(data?.uSD?.lOW24HOUR?.toBigDecimal()?.times(rate)?.times(rate)?.toDouble())}"

        text24hChange.text = "${String.format("%.2f", data?.uSD?.cHANGEPCT24HOUR?.toDouble())}%"
    }

    override fun loadCurrencyNews(news: Array<Article>, savedArticles: ArrayList<Article>) {

        val arrayNews = ArrayList<Article>()

        if (news.size > 9)

            for (i in 0..9) {
                arrayNews.add(news[i])
            }

        if(mLayoutManager == null){

            mLayoutManager = LinearLayoutManager(context)
            recyclerViewNews.layoutManager = mLayoutManager
            articlesVerticalAdapter = GeneralArticlesVerticalAdapter(arrayNews, savedArticles, context, presenter)
            recyclerViewNews.adapter = articlesVerticalAdapter
        } else {

            articlesVerticalAdapter.newsItems = arrayNews
            articlesVerticalAdapter.savedArticles = savedArticles
            articlesVerticalAdapter.notifyDataSetChanged()
        }
    }

    override fun getName(): String {
//        return symbol?.name.toString().replace(" ", "-").toLowerCase()
        return "TODO: getName()"
    }


    @SuppressLint("SetTextI18n")
    override fun showCurrentPrice(close: BigDecimal?, baseFiat: Rate) {

        val rate = baseFiat.rate ?: 1.toBigDecimal()

        price.text = "${Utils.getFiatSymbol(baseFiat.fiat)}${getPriceText(close?.times(rate)?.toDouble())}"
    }

    fun getPriceText(price: Double?): CharSequence? {

        var text = ""

        if (price != null) {
            when {
                price < 0.00000001 -> text =  "0.00000001"
                price < 1 -> text =  String.format("%.8f", price)
                else -> text = String.format("%.2f", price)
            }
        } else {
            text = "?"
        }
        return text
    }


    fun formatPrice(price: BigDecimal?): String {

        val formatter = DecimalFormat("#,###,###")
        val formattedString = formatter.format(price)

        return formattedString
    }

    override fun showPriceChange(open: BigDecimal, close: BigDecimal, baseFiat: Rate) {
        when {
            close > open -> {
                context?.resources?.getColor(R.color.green)?.let { change.setTextColor(it) }
            }
            close < open -> {
                context?.resources?.getColor(R.color.red)?.let { change.setTextColor(it) }
            }
        }

        val rate = baseFiat.rate ?: 1.toBigDecimal()

        val priceChange = (close - open) * rate
        var priceText = ""
        if (priceChange > 0.toBigDecimal())
            priceText = "+${Utils.getFiatSymbol(baseFiat.fiat)}" + Utils.formatPrice(priceChange.toDouble().absoluteValue.toBigDecimal()) + " ("
        else
            priceText = "-${Utils.getFiatSymbol(baseFiat.fiat)}" + Utils.formatPrice(priceChange.toDouble().absoluteValue.toBigDecimal()) + " (-"

        if (open != 0.toBigDecimal()) {
            change.text = priceText + formatPercentage((((close - open) / open) * 100.toBigDecimal())) + ")"
        }
    }

    fun formatPercentage(percentChange24h: BigDecimal?): String {

        if(percentChange24h == null || percentChange24h == 0.toBigDecimal())
            return "0.00%"

        val percentage2DP = String.format("%.2f", percentChange24h)

        return when {
            percentage2DP.toDouble() == 0.toDouble() -> {
                "0.01%"
            }
            percentage2DP.toDouble() > 0 -> {
                "+$percentage2DP%"
            }
            else -> {
                "$percentage2DP%"
            }
        }
    }

    override fun showGeneralDataError() {
        //TODO
    }

    private fun setUpTextViews() {
//        symbol.text = symbol?.symbol
//        when {
//            symbol?.quote?.uSD?.percentChange24h > 0 -> {
//                change.setTextColor(context?.resources?.getColor(R.color.green))
//            }
//            symbol?.quote?.uSD?.percentChange24h < 0 -> {
//                change.setTextColor(context?.resources?.getColor(R.color.red))
//            }
//            else -> {
//
//            }
//        }
//        price.text = "$${Utils.formatPrice(symbol?.quote?.uSD?.price?.toDouble())}"
//
//        val percentage2DP = Utils.formatPercentage(symbol.quote?.uSD?.percentChange24h)
//
//        when {
//            percentage2DP.substring(0, 1) == "$" -> {
//            }
//            percentage2DP.substring(0, 1) == "+" -> {
//                change.setTextColor(context?.resources?.getColor(R.color.green))
//            }
//            else -> {
//                change.setTextColor(context?.resources?.getColor(R.color.red))
//            }
//        }
//        change.text = "${getPriceChange(symbol?.quote?.uSD?.percentChange24h, symbol?.quote?.uSD?.price)} ($percentage2DP)"
    }
//    private fun getPriceChange(percentChange24h: Float?, price: Float?): String {
//
//        val priceChange = price?.minus(price.div((1 + (percentChange24h?.div(100)))))
//
//        return formatChange(priceChange?.toDouble())
//    }
    fun formatChange(priceAsDouble: BigDecimal, baseFiat: Rate): String {

        val price = Utils.toDecimals(priceAsDouble, 8).toDouble()

        var priceText: String

        priceText = when {
            price >= 0.01 -> "${Utils.getFiatSymbol(baseFiat.fiat)}${Utils.toDecimals(priceAsDouble, 2)}"
            price > -0.01 -> "-${Utils.getFiatSymbol(baseFiat.fiat)}0${Utils.toDecimals(priceAsDouble, 6).substring(1)}"
            price > -1 -> "-0${Utils.toDecimals(priceAsDouble, 2).substring(1)}" //.substring(1)
            else -> {
                "${Utils.getFiatSymbol(baseFiat.fiat)}-${Utils.toDecimals(priceAsDouble, 2).substring(1)}"
            }
        }

        if (priceText.indexOf(".") != -1 && (priceText.indexOf(".") + 1 == priceText.length - 1))
            priceText += "0"

        return priceText
    }

    private fun setUpGraphTimeChoices() {
        radioGroup.setOnPositionChangedListener { button, currentPosition, lastPosition ->
            presenter.clearChartDisposable()
            when (currentPosition) {
                0 -> {
                    setChartBottomLabelCount(6, true)
                    selectedChartFrame = chart1H
                }
                1 -> {
                    setChartBottomLabelCount(6, true)
                    selectedChartFrame = chart1D
                }
                2 -> {
                    setChartBottomLabelCount(4, true)
                    selectedChartFrame = chart3D
                }
                3 -> {
                    setChartBottomLabelCount(8, true)
                    selectedChartFrame = chart1W
                }
                4 -> {
                    setChartBottomLabelCount(6, true)
                    selectedChartFrame = chart1M
                }
                5 -> {
                    setChartBottomLabelCount(4, true)
                    selectedChartFrame = chart3M
                }
                6 -> {
                    setChartBottomLabelCount(7, true)
                    selectedChartFrame = chart6M
                }
                7 -> {
                    setChartBottomLabelCount(5, true)
                    selectedChartFrame = chart1Y
                }
            }
            presenter.getCurrencyChart(selectedChartFrame, getSymbol(), GeneralPresenter.conversionUSD)
        }
    }

    private fun setChartBottomLabelCount(count: Int, forced: Boolean) {
        candleStickChart.xAxis.setLabelCount(count, forced)
    }

    private fun setUpCandleStick() {

        candleStickChart.setDrawGridBackground(false)
        candleStickChart.legend.isEnabled = false
        candleStickChart.setPinchZoom(false)
        candleStickChart.isDragEnabled = true
        candleStickChart.setScaleEnabled(true)
        candleStickChart.setTouchEnabled(false)
        candleStickChart.description.isEnabled = false

        val xAxis = candleStickChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(true)
        xAxis.gridLineWidth = 0.5F
        xAxis.gridColor = R.color.light_grey
        xAxis.setLabelCount(6, false)

        val yAxisLeft = candleStickChart.axisLeft
        yAxisLeft.setDrawGridLines(true)
        yAxisLeft.gridLineWidth = 0.5F
        yAxisLeft.gridColor = R.color.light_grey
        yAxisLeft.setLabelCount(4, true)

        val yAxisRight = candleStickChart.axisRight
        yAxisRight.isEnabled = false
    }

    override fun loadCandlestickChart(response: HistoricalData, chart: Chart, aggregate: Int, baseFiat: Rate) {

        val rate = baseFiat.rate ?: 1.toBigDecimal()

        val entries = ArrayList<CandleEntry>()

        var lowest: Float? = null
        var highest: Float? = null

        for (i in 0 until (response.data?.size ?: 0)) {
            val entry = response.data?.get(i)
            (entry?.high?.times(rate))?.toFloat()?.let { high -> entry.low?.times(rate)?.toFloat()?.let { low -> entry.open?.times(rate)?.toFloat()?.let { open -> entry.close?.times(rate)?.toFloat()?.let { close -> CandleEntry(i.toFloat(), high, low, open, close) } } } }?.let { entry -> entries.add(entry) }

            val low = entry?.low?.times(rate)?.toFloat()
            val high = entry?.high?.times(rate)?.toFloat()

            if (lowest == null)
                lowest = low
            else if (low != null) {
                if (low < lowest)
                    lowest = low
            }

            if (highest == null)
                highest = high
            else if (high != null) {
                if (high > highest)
                    highest = high
            }
        }

        lowest?.let { low -> highest?.let { high -> setChartMinMax(low, high, chart) } }

        val xAxis = candleStickChart.xAxis
        xAxis.valueFormatter = XAxisValueFormatter(chart, aggregate)

        val dataSet = CandleDataSet(entries, "Label")

//        dataSet.setDrawIcons(false)
        dataSet.axisDependency = YAxis.AxisDependency.LEFT
//        dataSet.shadowColor = Color.DKGRAY
        dataSet.shadowColorSameAsCandle = true
        dataSet.decreasingColor = resources.getColor(R.color.red)
        dataSet.decreasingPaintStyle = Paint.Style.FILL
        dataSet.increasingColor = resources.getColor(R.color.green)
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

    private fun setChartMinMax(lowest: Float, highest: Float, chart: Chart) {


        val difference = (highest - lowest)
        val differenceBigDecimal = (highest.toBigDecimal() - lowest.toBigDecimal())

        Log.i(TAG, "difference: $difference")


        val a = 35F
        val b = 5

        val multiplesOf = when {
            difference <= a -> b
            difference <= a * 2 -> b * 2
            difference <= a * 10 -> b * 10
            difference <= a * 15 -> b * 20
            difference <= a * 40 -> b * 40
            difference <= a * 100 -> b * 100
            difference <= a * 200 -> b * 200
            difference <= a * 400 -> b * 400
            else -> b * 1000
        }

        candleStickChart.axisLeft.resetAxisMinimum()

        if (differenceBigDecimal > 1.toBigDecimal()) {

            val labelCount = ((roundToHighest(highest, multiplesOf) - roundToLowest(lowest, multiplesOf)) / multiplesOf).toInt() + 1


            candleStickChart.axisLeft.setLabelCount(labelCount, true)

            candleStickChart.axisLeft.axisMinimum = roundToLowest(lowest, multiplesOf)
            candleStickChart.axisLeft.axisMaximum = roundToHighest(highest, multiplesOf)
        } else {
//            candleStickChart.axisLeft.resetAxisMinimum()
//            if(candleStickChart.axisLeft.axisMinimum < 0)
            if(chart.aggregate == aggregate1Y)
                candleStickChart.axisLeft.axisMinimum = 0F
        }
    }

//    private fun getLabelCount(lowest: Float, highest: Float): Int {
//
//        var difference = highest - lowest
//
//        var multiplesOf = 1
//
//        when {
//            difference < 71F -> multiplesOf = 10
//            difference < 120F -> multiplesOf = 20
//            difference < 1000F -> multiplesOf = 100
//            else -> multiplesOf = 200
//        }
//
//        (roundToHighest(difference, multiplesOf).toInt() / multiplesOf) + 2
//
//    }

    private fun roundToLowest(lowest: Float, multiplesOf: Int): Float {
        var lowest10 = ((lowest.toInt() + (multiplesOf / 2)) / multiplesOf) * multiplesOf
        if (lowest10 > lowest.toInt())
            lowest10 -= multiplesOf
        return lowest10.toFloat()
    }

    private fun roundToHighest(highest: Float, multiplesOf: Int): Float {

        val highestInt = Math.round(highest)

        var highest10 = ((highestInt + (multiplesOf / 2)) / multiplesOf) * multiplesOf

        if (highest10 < highestInt)
            highest10 += multiplesOf

        return highest10.toFloat()
    }

    override fun getSymbol(): String {
        return currencySymbol ?: ""
    }

    override fun setPresenter(presenter: GeneralContract.Presenter) {
        this.presenter = checkNotNull(presenter)
    }

    var chart1H = Chart(numOfCandlesticks, aggregate1H, minuteString)
    var chart1D = Chart(numOfCandlesticks, aggregate1D, hourString)
    var chart3D = Chart(numOfCandlesticks, aggregate3D, hourString)
    var chart1W = Chart(numOfCandlesticks, aggregate1W, hourString)

    var chart1M = Chart(numOfCandlesticks, aggregate1M, dayString)
    var chart3M = Chart(numOfCandlesticks, aggregate3M, dayString)
    var chart6M = Chart(numOfCandlesticks, aggregate6M, dayString)
    var chart1Y = Chart(numOfCandlesticks, aggregate1Y, dayString)

    companion object {

        var numOfCandlesticks = 30

        val minuteString = "minute"
        val hourString = "hour"
        val dayString = "day"

        val aggregate1H = 2
        val aggregate1D = 1
        val aggregate3D = 3
        val aggregate1W = 6
        val aggregate1M = 1
        val aggregate3M = 3
        val aggregate6M = 6
        val aggregate1Y = 12

//        val limit1H = 30
//        val aggregate1H = 2
//        val timeMeasure1H = "minute"
//
//        val limit1D = 30
//        val aggregate1D = 1
//        val timeMeasure1D = "hour"
//
//        val limit3D = 30
//        val aggregate3D = 3
//        val timeMeasure3D = "hour"
//
//        val limit1W = 30
//        val aggregate1W = 6
//        val timeMeasure1W = "hour"
//
//        val limit1M = 30
//        val aggregate1M = 1
//        val timeMeasure1M = "day"
//
//        val limit3M = 30
//        val aggregate3M = 3
//        val timeMeasur3M = "day"
//
//        val limit6M = 30
//        val aggregate6M = 6
//        val timeMeasure6M = "day"
//
//        val limit1Y = 30
//        val aggregate1Y = 12
//        val timeMeasure1Y = "day"

        @JvmStatic
        fun newInstance(param1: String) =
                GeneralFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable(ARG_PARAM1, param1)
                    }
                }
        private const val ARG_PARAM1 = "symbol"
        private const val TAG = "GeneralFragment"
    }
}
