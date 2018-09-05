package com.jonnycaley.cryptomanager.ui.currency.viewpager.general

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
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Currency
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.HistoricalData.Data
import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.News
import com.jonnycaley.cryptomanager.ui.home.ArticlesVerticalAdapter
import com.jonnycaley.cryptomanager.utils.Utils
import java.util.*

class GeneralFragment : Fragment(), GeneralContract.View {

    private lateinit var presenter : GeneralContract.Presenter

    private var currency: Currency? = null

    lateinit var mView : View

    lateinit var articlesVerticalAdapter : ArticlesVerticalAdapter

    val symbol : TextView by lazy { mView.findViewById<TextView>(R.id.symbol) }
    val price : TextView by lazy { mView.findViewById<TextView>(R.id.price) }
    val change : TextView by lazy { mView.findViewById<TextView>(R.id.change) }

    val recyclerViewNews : RecyclerView by lazy { mView.findViewById<RecyclerView>(R.id.recycler_view_news) }

    val radioGroup : RadioRealButtonGroup by lazy { mView.findViewById<RadioRealButtonGroup>(R.id.radio_group) }

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

        setUpTextViews()
        setUpGraphTimeChoices()
        setUpCandleStick()

        presenter = GeneralPresenter(GeneralDataManager.getInstance(context!!), this)
        presenter.attachView()
    }

    override fun loadCurrencyNews(news: Array<News>) {

        val arrayNews = ArrayList<News>()

        if(news.size > 9)

        for(i in 0 .. 9){
            arrayNews.add(news[i])
        }

        val mLayoutManager = LinearLayoutManager(context)
        recyclerViewNews.layoutManager = mLayoutManager
        articlesVerticalAdapter = ArticlesVerticalAdapter(arrayNews, context)
        recyclerViewNews.adapter = articlesVerticalAdapter
    }

    override fun getName(): String {
        return currency?.name.toString().replace(" ", "-").toLowerCase()
    }

    private fun setUpTextViews() {
        symbol.text = currency?.symbol
        when {
            currency?.quote?.uSD?.percentChange24h!! > 0 -> {
                change.setTextColor(context?.resources?.getColor(R.color.green)!!)
            }
            currency?.quote?.uSD?.percentChange24h!! < 0 -> {
                change.setTextColor(context?.resources?.getColor(R.color.red)!!)
            }
            else -> {

            }
        }
        price.text = "$${Utils.formatPrice(currency?.quote?.uSD?.price?.toDouble()!!)}"

        val percentage2DP = Utils.formatPercentage(currency!!.quote?.uSD?.percentChange24h)

        when {
            percentage2DP.substring(0, 1) == "$" -> {
            }
            percentage2DP.substring(0, 1) == "+" -> {
                change.setTextColor(context?.resources?.getColor(R.color.green)!!)
            }
            else -> {
                change.setTextColor(context?.resources?.getColor(R.color.red)!!)
            }
        }
        change.text = "${getPriceChange(currency?.quote?.uSD?.percentChange24h, currency?.quote?.uSD?.price)} ($percentage2DP)"

    }

    private fun getPriceChange(percentChange24h: Float?, price: Float?): String {

        val priceChange = price?.minus(price.div((1 + (percentChange24h?.div(100)!!))))

        return formatChange(priceChange?.toDouble()!!)
    }

    fun formatChange(priceAsDouble: Double): String {

        val price = Utils.toDecimals(priceAsDouble, 8).toDouble()

        var priceText: String

        priceText = when {
            price >= 0.01 -> "$${Utils.toDecimals(priceAsDouble, 2)}"
            price > -0.01 -> "-$0${Utils.toDecimals(priceAsDouble, 6).substring(1)}"
            price > -1 -> "-$0${Utils.toDecimals(priceAsDouble, 2).substring(1)}" //.substring(1)
            else -> {
                "$-${Utils.toDecimals(priceAsDouble, 2).substring(1)}"
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

    override fun loadCandlestickChart(response: Data, timeUnit: String, aggregate: Int) {

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
