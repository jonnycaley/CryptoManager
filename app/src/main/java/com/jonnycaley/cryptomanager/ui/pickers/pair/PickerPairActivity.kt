package com.jonnycaley.cryptomanager.ui.pickers.pair

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.ui.transactions.crypto.CryptoTransactionActivity
import com.jonnycaley.cryptomanager.utils.Utils
import kotlinx.android.synthetic.main.activity_picker_pair.*

class PickerPairActivity : AppCompatActivity(), PickerPairContract.View {

    private lateinit var presenter: PickerPairContract.Presenter

    val exchangeArg by lazy { this.intent.getSerializableExtra(CryptoTransactionActivity.EXCHANGE_KEY) as String? }
    val crypto by lazy { this.intent.getSerializableExtra(CryptoTransactionActivity.CRYPTO_KEY) as String? }

    val progressBarLayout by lazy { findViewById<ConstraintLayout>(R.id.progress_bar_layout) }

    val layout by lazy { findViewById<LinearLayout>(R.id.layout) }

    val recyclerView by lazy { findViewById<RecyclerView>(R.id.recycler_view) }
    lateinit var pairAdapter : PairAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        if(Utils.isDarkTheme()) {
            setTheme(R.style.darktheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picker_pair)


        if(!Utils.isDarkTheme()) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        }

        setupToolbar()

        presenter = PickerPairPresenter(PickerPairDataManager.getInstance(this), this)
        presenter.attachView()
    }

    override fun showError() {
        showSnackBar(resources.getString(R.string.error_occurred))
    }

    var snackBar : Snackbar? = null

    fun showSnackBar(message: String) {

        snackBar = Snackbar.make(layout, message, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry) {
                    presenter.getPairs(exchangeArg,crypto)
                }
        snackBar.let { it?.show() }
    }

    override fun showPairs(pairs: List<String>?) {

        val mLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = mLayoutManager
        pairAdapter = PairAdapter(pairs?.sortedBy { it }, crypto!!, this, this)
        recyclerView.adapter = pairAdapter

    }

    override fun onPairChosen(pair: String?) {
        val intent = Intent()
        intent.putExtra("data", pair)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun showProgressBar() {
        progressBarLayout.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        progressBarLayout.visibility = View.GONE
    }

    override fun getExchange(): String? {
        if(exchangeArg != null)
            return exchangeArg as String
        return null
    }

    override fun getCryproSymbol(): String? {
        if(crypto != null)
            return crypto as String
        return null
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Trading Pair"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                super.onBackPressed()
                return false
            }
        }
        return false
    }

    override fun setPresenter(presenter: PickerPairContract.Presenter) {
        this.presenter = checkNotNull(presenter)
    }
}
