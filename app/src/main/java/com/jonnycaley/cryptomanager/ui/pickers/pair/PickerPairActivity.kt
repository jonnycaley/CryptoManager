package com.jonnycaley.cryptomanager.ui.pickers.pair

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.ui.transactions.crypto.create.CreateCryptoTransactionActivity

class PickerPairActivity : AppCompatActivity(), PickerPairContract.View {

    private lateinit var presenter: PickerPairContract.Presenter

    val exchangeArg by lazy { this.intent.getSerializableExtra(CreateCryptoTransactionActivity.EXCHANGE_KEY) as String? }
    val crypto by lazy { this.intent.getSerializableExtra(CreateCryptoTransactionActivity.CRYPTO_KEY) as String? }

    val recyclerView by lazy { findViewById<RecyclerView>(R.id.recycler_view) }
    lateinit var pairAdapter : PairAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.darktheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picker_pair)

        setupToolbar()

        presenter = PickerPairPresenter(PickerPairDataManager.getInstance(this), this)
        presenter.attachView()
    }

    override fun showPairs(pairs: List<String>?) {

        val mLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = mLayoutManager
        pairAdapter = PairAdapter(pairs, crypto!!, this, this)
        recyclerView.adapter = pairAdapter

    }

    override fun onPairChosen(pair: String?) {
        val intent = Intent()
        intent.putExtra("data", pair)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun showProgressBar() {

    }

    override fun hideProgressBar() {

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
        supportActionBar?.title = "PickerPairActivity"
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
