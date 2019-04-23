package com.jonnycaley.cryptomanager.ui.pickers.exchange

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.SearchView
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.Exchanges.Exchange
import com.jonnycaley.cryptomanager.ui.transactions.crypto.CryptoTransactionActivity
import com.jonnycaley.cryptomanager.utils.Utils
import com.r0adkll.slidr.Slidr
import kotlinx.android.synthetic.main.activity_picker_exchange.*

class PickerExchangeActivity : AppCompatActivity(), PickerExchangeContract.View, SearchView.OnQueryTextListener {

    private lateinit var presenter: PickerExchangeContract.Presenter

    lateinit var exchangesAdapter : ExchangesAdapter

    val recyclerView by lazy { findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recycler_view) }

    val layout by lazy { findViewById<LinearLayout>(R.id.layout) }

    val searchBar by lazy { findViewById<SearchView>(R.id.search_view) }

    val progressBarLayout by lazy { findViewById<ConstraintLayout>(R.id.progress_bar_layout) }

    val args by lazy { this.intent.getSerializableExtra(CryptoTransactionActivity.CRYPTO_KEY) }

    override fun onCreate(savedInstanceState: Bundle?) {

        if(Utils.isDarkTheme()) {
            setTheme(R.style.darktheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picker_exchange)

        Slidr.attach(this)

        if(!Utils.isDarkTheme()) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp) //change icon
        }

        setupToolbar() //set toolbar
        setupSearchBar() //set search bar

        recyclerView.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() { //add listener
            override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(dy != 0)
                    Utils.hideKeyboardFromActivity(this@PickerExchangeActivity) //hide keyboard
            }
        })

        presenter = PickerExchangePresenter(PickerExchangeDataManager.getInstance(this), this) //init presenter
        presenter.attachView()
    }

    private fun setupSearchBar() {
        searchBar.setOnQueryTextListener(this) //set listener
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        Utils.hideKeyboardFromActivity(this)
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        query?.let {
            presenter.filterExchanges(it.trim()) //filter exchanges

        }
        return true
    }

    override fun showSearchBar() {
        searchBar.visibility = View.VISIBLE //change visibility
    }

    override fun getCrypto(): String? {
        if(args != null)
            return args as String
        return null
    }

    override fun hideProgressBar() {
        progressBarLayout.visibility = View.GONE //change visibility
    }

    override fun showProgressBar() {
        progressBarLayout.visibility = View.VISIBLE //change visibility
    }

    override fun showError() {
        showSnackBar(resources.getString(R.string.error_occurred))
    }

    var snackBar : Snackbar? = null

    fun showSnackBar(message: String) {

        snackBar = Snackbar.make(layout, message, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry) {
                    presenter.getExchanges(getCrypto())
                }
        snackBar.let { it?.show() }
    }

    override fun onExchangeChosen(name: String?) {
        val intent = Intent()
        intent.putExtra("data", name)
        setResult(RESULT_OK, intent)
        finish()
    }

    /*
    Function shows the exchanges
    */
    override fun showExchanges(exchanges: List<Exchange>?) {

        println("Showing exchanges")
        println(exchanges?.size.toString())

        val mLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        recyclerView.layoutManager = mLayoutManager
        exchangesAdapter = ExchangesAdapter(exchanges?.sortedBy { it.name?.toLowerCase() }, this, this)
        recyclerView.adapter = exchangesAdapter

    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
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

    override fun setPresenter(presenter: PickerExchangeContract.Presenter) {
        this.presenter = checkNotNull(presenter)
    }
}
