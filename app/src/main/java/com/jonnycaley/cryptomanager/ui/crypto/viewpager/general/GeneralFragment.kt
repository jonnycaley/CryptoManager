package com.jonnycaley.cryptomanager.ui.crypto.viewpager.general

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import co.ceryle.radiorealbutton.RadioRealButtonGroup
import com.appyvet.rangebar.RangeBar
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.HistoricalData.HistoricalData
import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.Article
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.Rate
import com.jonnycaley.cryptomanager.utils.Utils
import java.math.BigDecimal
import java.text.DecimalFormat
import kotlin.collections.ArrayList
import kotlin.math.absoluteValue

class GeneralFragment : Fragment(), GeneralContract.View {

    private lateinit var presenter : GeneralContract.Presenter

    private var currencySymbol: String? = null

    lateinit var mView : View

    lateinit var articlesVerticalAdapter : GeneralArticlesVerticalAdapter

    val price : TextView by lazy { mView.findViewById<TextView>(R.id.price) }
    val change : TextView by lazy { mView.findViewById<TextView>(R.id.change) }

    val recyclerViewNews : RecyclerView by lazy { mView.findViewById<RecyclerView>(R.id.recycler_view_news) }

    val radioGroup : RadioRealButtonGroup by lazy { mView.findViewById<RadioRealButtonGroup>(R.id.radio_group) }

    val candleStickChart : CandleStickChart by lazy { mView.findViewById<CandleStickChart>(R.id.chart_candlestick) }

    val textMarketCap : TextView by lazy { mView.findViewById<TextView>(R.id.text_market_cap) }
    val text24hHigh : TextView by lazy { mView.findViewById<TextView>(R.id.text_24h_high) }
    val text24hLow : TextView by lazy { mView.findViewById<TextView>(R.id.text_24h_low) }
    val text24hChange : TextView by lazy { mView.findViewById<TextView>(R.id.text_24h_change) }
    val textCirculatingSupply : TextView by lazy { mView.findViewById<TextView>(R.id.text_circulating_supply) }

    val rangeBar : RangeBar by lazy { mView.findViewById<RangeBar>(R.id.rangebar) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currencySymbol = it.getSerializable(ARG_PARAM1) as String
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

        presenter = GeneralPresenter(GeneralDataManager.getInstance(context!!), this)
        presenter.attachView()
    }

    override fun showDaysRange(lOW24HOUR: String?, hIGH24HOUR: String?, pRICE: String?, baseFiat: Rate) {

        println(lOW24HOUR)
        println(hIGH24HOUR)
        println(pRICE)

        //TODO
    }

    override fun showCirculatingSupply(sUPPLY: String?) {

        val formatter = DecimalFormat("#,###,###")
        val formattedString = formatter.format(sUPPLY?.toDouble())

        textCirculatingSupply.text = formattedString
    }

    override fun show24High(hIGH24HOUR: String?, baseFiat: Rate) {

        val high = hIGH24HOUR?.toBigDecimal()?.times(baseFiat.rate!!)
        val formattedHigh = formatChange(high!!, baseFiat)

        text24hHigh.text = "$formattedHigh"
    }

    override fun show24Low(lOW24HOUR: String?, baseFiat: Rate) {
        val low = lOW24HOUR?.toBigDecimal()?.times(baseFiat.rate!!)
        val formattedLow = formatChange(low!!, baseFiat)

        text24hLow.text = "$formattedLow"
    }

    override fun show24Change(cHANGEPCT24HOUR: String?) {
        text24hChange.text = "${String.format("%.2f",cHANGEPCT24HOUR?.toDouble()!!)}%"
    }

    override fun loadCurrencyNews(news: Array<Article>, savedArticles: ArrayList<Article>) {

        val arrayNews = ArrayList<Article>()

        if(news.size > 9)

        for(i in 0 .. 9){
            arrayNews.add(news[i])
        }

        val mLayoutManager = LinearLayoutManager(context)
        recyclerViewNews.layoutManager = mLayoutManager
        articlesVerticalAdapter = GeneralArticlesVerticalAdapter(arrayNews, savedArticles, context, presenter)
        recyclerViewNews.adapter = articlesVerticalAdapter
    }

    override fun getName(): String {
//        return symbol?.name.toString().replace(" ", "-").toLowerCase()
        return "TODO: getName()"
    }


    override fun showCurrentPrice(close: BigDecimal?, baseFiat: Rate) {
        price.text = "${formatChange(close?.times(baseFiat.rate!!)!!, baseFiat)}"
    }

    override fun showMarketCap(marketCap: String?, baseFiat : Rate) {

        val formattedMarketCap = marketCap?.toBigDecimal()?.times(baseFiat.rate!!)

        val formattedString = formatPrice(formattedMarketCap)

        textMarketCap.text = "${Utils.getFiatSymbol(baseFiat.fiat)}$formattedString"
    }

    fun formatPrice(price : BigDecimal?) : String {

        val formatter = DecimalFormat("#,###,###")
        val formattedString = formatter.format(price)

        return formattedString
    }

    override fun showPriceChange(open: BigDecimal?, close: BigDecimal?, baseFiat: Rate) {
        when{
            close!! > open!! -> {
                change.setTextColor(context?.resources?.getColor(R.color.green)!!)
            }
            close < open -> {
                change.setTextColor(context?.resources?.getColor(R.color.red)!!)
            }
        }

        val priceChange = (close!! - open!!) * baseFiat.rate!!
        var priceText = ""
        if(priceChange > 0.toBigDecimal())
            priceText = "+${Utils.getFiatSymbol(baseFiat.fiat)}"+String.format("%.2f",priceChange.toDouble().absoluteValue)
        else
            priceText = "-${Utils.getFiatSymbol(baseFiat.fiat)}"+String.format("%.2f",priceChange.toDouble().absoluteValue)

        change.text = priceText + " (" + Utils.formatPercentage((((close - open)/open)*100.toBigDecimal())) + ")"
    }

    override fun showGeneralDataError() {
        //TODO
    }

    private fun setUpTextViews() {
//        symbol.text = symbol?.symbol
//        when {
//            symbol?.quote?.uSD?.percentChange24h!! > 0 -> {
//                change.setTextColor(context?.resources?.getColor(R.color.green)!!)
//            }
//            symbol?.quote?.uSD?.percentChange24h!! < 0 -> {
//                change.setTextColor(context?.resources?.getColor(R.color.red)!!)
//            }
//            else -> {
//
//            }
//        }
//        price.text = "$${Utils.formatPrice(symbol?.quote?.uSD?.price?.toDouble()!!)}"
//
//        val percentage2DP = Utils.formatPercentage(symbol!!.quote?.uSD?.percentChange24h)
//
//        when {
//            percentage2DP.substring(0, 1) == "$" -> {
//            }
//            percentage2DP.substring(0, 1) == "+" -> {
//                change.setTextColor(context?.resources?.getColor(R.color.green)!!)
//            }
//            else -> {
//                change.setTextColor(context?.resources?.getColor(R.color.red)!!)
//            }
//        }
//        change.text = "${getPriceChange(symbol?.quote?.uSD?.percentChange24h, symbol?.quote?.uSD?.price)} ($percentage2DP)"

    }

//    private fun getPriceChange(percentChange24h: Float?, price: Float?): String {
//
//        val priceChange = price?.minus(price.div((1 + (percentChange24h?.div(100)!!))))
//
//        return formatChange(priceChange?.toDouble()!!)
//    }

    fun formatChange(priceAsDouble: BigDecimal, baseFiat : Rate): String {

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

        if(priceText.indexOf(".") != -1 && (priceText.indexOf(".") + 1 == priceText.length -1))
            priceText += "0"

        return priceText
    }

    private fun setUpGraphTimeChoices() {
        radioGroup.setOnPositionChangedListener { button, currentPosition, lastPosition ->
            presenter.clearChartDisposable()
            when(currentPosition){
                0 -> {
                    setChartBottomLabelCount(6, true)
                    setChartMinimumZero(false)
                    presenter.getCurrencyChart(GeneralPresenter.timeMeasure1H, getSymbol(), GeneralPresenter.conversionUSD, GeneralPresenter.numOfCandlesticks, GeneralPresenter.aggregate1H)
                }
                1 -> {
                    setChartBottomLabelCount(6, true)
                    setChartMinimumZero(false)
                    presenter.getCurrencyChart(GeneralPresenter.timeMeasure1D, getSymbol(), GeneralPresenter.conversionUSD, GeneralPresenter.numOfCandlesticks, GeneralPresenter.aggregate1D)
                }
                2 -> {
                    setChartBottomLabelCount(4, true)
                    setChartMinimumZero(false)
                    presenter.getCurrencyChart(GeneralPresenter.timeMeasure3D, getSymbol(), GeneralPresenter.conversionUSD, GeneralPresenter.numOfCandlesticks, GeneralPresenter.aggregate3D)
                }
                3 -> {
                    setChartBottomLabelCount(8, true)
                    setChartMinimumZero(false)
                    presenter.getCurrencyChart(GeneralPresenter.timeMeasure1W, getSymbol(), GeneralPresenter.conversionUSD, GeneralPresenter.numOfCandlesticks, GeneralPresenter.aggregate1W)
                }
                4 -> {
                    setChartBottomLabelCount(6, true)
                    setChartMinimumZero(false)
                    presenter.getCurrencyChart(GeneralPresenter.timeMeasure1M, getSymbol(), GeneralPresenter.conversionUSD, GeneralPresenter.numOfCandlesticks, GeneralPresenter.aggregate1M)
                }
                5 -> {
                    setChartBottomLabelCount(4, true)
                    setChartMinimumZero(false)
                    presenter.getCurrencyChart(GeneralPresenter.timeMeasur3M, getSymbol(), GeneralPresenter.conversionUSD, GeneralPresenter.numOfCandlesticks, GeneralPresenter.aggregate3M)
                }
                6 -> {
                    setChartBottomLabelCount(7, true)
                    setChartMinimumZero(false)
                    presenter.getCurrencyChart(GeneralPresenter.timeMeasure6M, getSymbol(), GeneralPresenter.conversionUSD, GeneralPresenter.numOfCandlesticks, GeneralPresenter.aggregate6M)
                }
                7 -> {
                    setChartBottomLabelCount(5, true)
                    setChartMinimumZero(true)
                    presenter.getCurrencyChart(GeneralPresenter.timeMeasure1Y, getSymbol(), GeneralPresenter.conversionUSD, GeneralPresenter.numOfCandlesticks, GeneralPresenter.aggregate1Y)
                }
                8 -> {
                    setChartBottomLabelCount(6, true)
                    setChartMinimumZero(true)
//                    presenter.getCurrencyGraph(GeneralPresenter.timeMeasure1H, getSymbol(), GeneralPresenter.conversionUSD, GeneralPresenter.numOfCandlesticks, GeneralPresenter.aggregate1H)
                    //TODO: Determine how we are going to get the time scale to search over for new currencies
                }
            }
        }
    }

    private fun setChartMinimumZero(isMinimumZero: Boolean) {
        if(isMinimumZero)
            candleStickChart.axisLeft.axisMinimum = 0F
        else
            candleStickChart.axisLeft.resetAxisMinimum()
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
        candleStickChart.setTouchEnabled(true)

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

    override fun loadCandlestickChart(response: HistoricalData, timeUnit: String, aggregate: Int, baseFiat: Rate) {

        val entries = ArrayList<CandleEntry>()

        for(i in 0 until response.data?.size!!){
            val entry = response.data!![i]

            entries.add(CandleEntry(i.toFloat(), (entry.high?.times(baseFiat.rate!!))?.toFloat()!!, entry.low?.times(baseFiat.rate!!)?.toFloat()!!, entry.open?.times(baseFiat.rate!!)?.toFloat()!!, entry.close?.times(baseFiat.rate!!)?.toFloat()!!))
        }

        val xAxis = candleStickChart.xAxis
        xAxis.valueFormatter = XAxisValueFormatter(timeUnit, aggregate)

        val dataSet = CandleDataSet(entries, "Label")

//        dataSet.setDrawIcons(false)
        dataSet.axisDependency = YAxis.AxisDependency.LEFT
        dataSet.shadowColor = Color.DKGRAY
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

    override fun getSymbol(): String {
        return currencySymbol!!
    }

    override fun setPresenter(presenter: GeneralContract.Presenter) {
        this.presenter = checkNotNull(presenter)
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String) =
                GeneralFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable(ARG_PARAM1, param1)
                    }
                }

        private const val ARG_PARAM1 = "symbol"
    }
}
