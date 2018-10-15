package com.jonnycaley.cryptomanager.ui.transactions.crypto.create

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.ui.pickers.exchange.PickerExchangeActivity
import com.jonnycaley.cryptomanager.ui.pickers.pair.PickerPairActivity
import com.jonnycaley.cryptomanager.utils.CircleTransform
import com.jonnycaley.cryptomanager.utils.Utils
import com.squareup.picasso.Picasso
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import java.util.*

class CreateCryptoTransactionActivity : AppCompatActivity(), CreateCryptoTransactionContract.View, View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private lateinit var presenter: CreateCryptoTransactionContract.Presenter

    val args by lazy { CreateCryptoTransactionArgs.deserializeFrom(intent) }

    val headerImage by lazy { findViewById<ImageView>(R.id.header_image)}
    val headerName by lazy { findViewById<TextView>(R.id.header_text_name)}
    val headerSymbol by lazy { findViewById<TextView>(R.id.header_text_symbol)}

    val layoutDate by lazy { findViewById<RelativeLayout>(R.id.layout_date)}
    val requiredDate by lazy { findViewById<TextView>(R.id.date)}

    val layoutEmptyPair by lazy { findViewById<RelativeLayout>(R.id.layout_empty_trading_pair) }
    val layoutFilledPair by lazy { findViewById<RelativeLayout>(R.id.layout_filled_trading_pair) }
    val requiredPair by lazy { findViewById<TextView>(R.id.trading_pair) }

    val layoutEmptyExchange by lazy { findViewById<RelativeLayout>(R.id.layout_empty_exchange) }
    val layoutFilledExchange by lazy { findViewById<RelativeLayout>(R.id.layout_filled_exchange) }
    val requiredExchange by lazy { findViewById<TextView>(R.id.exchange) }

    val requiredPrice by lazy { findViewById<EditText>(R.id.edit_text_price) }
    val requiredQuantity by lazy { findViewById<EditText>(R.id.edit_text_quantity) }

    val textDeduction by lazy { findViewById<TextView>(R.id.text_deducation) }

    val textSellAll by lazy { findViewById<TextView>(R.id.text_sell_all) }

    val radioButtonWithdrawl by lazy { findViewById<RadioButton>(R.id.radio_button_withdrawl) }
    val radioButtonDeposit by lazy { findViewById<RadioButton>(R.id.radio_button_deposit) }

    val switchDeduct by lazy { findViewById<Switch>(R.id.switch_deduct) }

    val edittextNotes by lazy { findViewById<EditText>(R.id.edit_text_notes) }

    val textSubmit by lazy { findViewById<TextView>(R.id.text_view_submit) }

    var chosenDate = Calendar.getInstance().time

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_crypto_transaction)

        setupToolbar()
        setupHeader()
        setupBody()

        presenter = CreateCryptoTransactionPresenter(CreateCryptoTransactionDataManager.getInstance(this), this)
        presenter.attachView()
    }

    private fun setupBody() {
        requiredDate.text = Utils.formatDate(chosenDate)

        layoutDate.setOnClickListener(this)
        layoutEmptyPair.setOnClickListener(this)
        layoutFilledPair.setOnClickListener(this)
        layoutEmptyExchange.setOnClickListener(this)
        layoutFilledExchange.setOnClickListener(this)
        radioButtonDeposit.setOnClickListener(this)
        radioButtonWithdrawl.setOnClickListener(this)
        textSellAll.setOnClickListener(this)
        textSubmit.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        lateinit var i: Intent

        when(view?.id){
            layoutDate.id ->{
                showDatePicker()
            }
            layoutEmptyPair.id -> {
                i = Intent(this, PickerPairActivity::class.java)
                i.putExtra(EXCHANGE_KEY, requiredExchange.text)
                i.putExtra(CRYPTO_KEY, args.currency.symbol)
                startActivityForResult(i, REQUEST_CODE_PAIR)
            }
            layoutFilledPair.id -> {
                i = Intent(this, PickerPairActivity::class.java)
                i.putExtra(EXCHANGE_KEY, requiredExchange.text)
                i.putExtra(CRYPTO_KEY, args.currency.symbol)
                startActivityForResult(i, REQUEST_CODE_PAIR)
            }
            layoutEmptyExchange.id -> {
                i = Intent(this, PickerExchangeActivity::class.java)
                i.putExtra(CRYPTO_KEY, args.currency.symbol)
                startActivityForResult(i, REQUEST_CODE_EXCHANGE)
            }
            layoutFilledExchange.id -> {
                i = Intent(this, PickerExchangeActivity::class.java)
                i.putExtra(CRYPTO_KEY, args.currency.symbol)
                startActivityForResult(i, REQUEST_CODE_EXCHANGE)
            }
            radioButtonDeposit.id -> {
                textDeduction.text = "Deduct from ${getPairText()}holdings"
                textSellAll.visibility = View.GONE
            }
            radioButtonWithdrawl.id -> {
                textDeduction.text = "Add to ${getPairText()}holdings"
                textSellAll.visibility = View.VISIBLE
            }
            textSellAll.id -> {
                val allHoldings = presenter.getAllHoldings(args.currency.symbol)
                if(allHoldings > 0)
                    requiredQuantity.setText(allHoldings.toInt())
            }
            textSubmit.id -> {
                if(checkFields()){
                    preventChanges()
                    presenter.saveCryptoTransaction(radioButtonDeposit.isChecked, requiredExchange.text.trim().toString(), requiredPair.text.trim().toString(), java.lang.Float.parseFloat(requiredPrice.text.trim().toString()), java.lang.Float.parseFloat(requiredQuantity.text.trim().toString()), chosenDate, switchDeduct.isChecked, edittextNotes.text.trim().toString())
                }
            }
        }
    }

    override fun getSymbol(): String {
        return args.currency.symbol.toString()
    }

    private fun preventChanges() {

    }

    override fun onTransactionComplete() {
        super.onBackPressed()
    }


    private fun checkFields(): Boolean {

        var canSubmit = true

        val shake = AnimationUtils.loadAnimation(this, R.anim.shake)

        if (requiredExchange.text.toString().trim() == "") {
            layoutEmptyExchange.startAnimation(shake)
            println("No exchange chosen!")
            canSubmit = false
        }
        if (requiredPair.text.toString().trim() == "") {
            layoutEmptyPair.startAnimation(shake)
            println("No pair chosen!")
            canSubmit = false
        }
        if (requiredPrice.text.toString().trim() == "") {
            requiredPrice.startAnimation(shake)
            println("No price chosen!")
            canSubmit = false
        }

        if (requiredQuantity.text.toString().trim() == "") {
            requiredQuantity.startAnimation(shake)
            println("No quantity chosen!")
            canSubmit = false
        }

        return canSubmit
    }

    private fun getPairText(): CharSequence? {
        if(requiredPair.text == "")
            return ""
        return "${requiredPair.text} "
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CreateCryptoTransactionActivity.REQUEST_CODE_EXCHANGE) {
            if (resultCode == Activity.RESULT_OK) {
                val exchange = data?.getStringExtra("data")
                showExchange(exchange)
                resetTradingPair()
            }
        }
        if (requestCode == CreateCryptoTransactionActivity.REQUEST_CODE_PAIR) {
            if (resultCode == Activity.RESULT_OK) {
                val pair = data?.getStringExtra("data")
                showPair(pair)
                showPairChanged(pair)
                requiredPrice.hint = "Price in $pair"
            }
        }
    }

    private fun showPairChanged(pair : String?) {
        if(radioButtonDeposit.isChecked)
            textDeduction.text = "Deduct from $pair holdings"
        else
            textDeduction.text = "Add to $pair holdings"
    }

    private fun showPair(pair: String?) {
        layoutEmptyPair.visibility = View.GONE
        layoutFilledPair.visibility = View.VISIBLE
        requiredPair.text = pair
    }

    private fun showExchange(exchange: String?) {
        layoutEmptyExchange.visibility = View.GONE
        layoutFilledExchange.visibility = View.VISIBLE
        requiredExchange.text = exchange
    }

    private fun resetTradingPair() {
        requiredPair.text = ""
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        chosenDate.year = year - 1900
        chosenDate.month = monthOfYear
        chosenDate.date = dayOfMonth

        showTimePicker()
    }

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        chosenDate.hours = hourOfDay
        chosenDate.minutes = minute
        chosenDate.seconds = second
        requiredDate.text = Utils.formatDate(chosenDate)
    }

    private fun showDatePicker() {

        val now = Calendar.getInstance()
        val dpd = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR), // Initial year selection
                now.get(Calendar.MONTH), // Initial month selection
                now.get(Calendar.DAY_OF_MONTH) // Inital day selection
        )
        dpd.show(fragmentManager, "Datepickerdialog")
    }

    private fun showTimePicker() {
        val now = Calendar.getInstance()
        val dpd = TimePickerDialog.newInstance(
                this,
                now.get(Calendar.HOUR_OF_DAY), // Initial year selection
                now.get(Calendar.MINUTE), // Initial month selection
                false
        )
        dpd.show(fragmentManager, "Timepickerdialog")
    }

    private fun setupHeader() {

        Picasso.with(this)
                .load(args.baseUrl + args.currency.imageUrl)
                .fit()
                .centerCrop()
                .transform(CircleTransform())
//                .placeholder(R.drawable.circle)
                .into(headerImage)

        headerName.text = args.currency.coinName
        headerSymbol.text = args.currency.symbol
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = args.currency.coinName
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

    override fun setPresenter(presenter: CreateCryptoTransactionContract.Presenter) {
        this.presenter = checkNotNull(presenter)
    }


    companion object {
        val REQUEST_CODE_PAIR = 1
        val REQUEST_CODE_EXCHANGE = 2

        val EXCHANGE_KEY = "key_exchange"
        val PAIR_KEY = "key_pair"
        val CRYPTO_KEY = "key_crypto"

        val TAG = "CreateCryptoTransactionActivity"
    }

}