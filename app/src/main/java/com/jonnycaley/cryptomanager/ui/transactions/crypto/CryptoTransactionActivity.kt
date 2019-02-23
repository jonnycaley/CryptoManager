package com.jonnycaley.cryptomanager.ui.transactions.crypto

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.DataBase.NotTransaction
import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import com.jonnycaley.cryptomanager.ui.base.BaseActivity
import com.jonnycaley.cryptomanager.ui.pickers.exchange.PickerExchangeActivity
import com.jonnycaley.cryptomanager.ui.pickers.pair.PickerPairActivity
import com.jonnycaley.cryptomanager.utils.CircleTransform
import com.jonnycaley.cryptomanager.utils.Utils
import com.squareup.picasso.Picasso
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import java.math.BigDecimal
import java.util.*

class CryptoTransactionActivity : AppCompatActivity(), CryptoTransactionContract.View, View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private lateinit var presenter: CryptoTransactionContract.Presenter

    val args by lazy { CryptoTransactionArgs.deserializeFrom(intent) }

    val buttonDelete by lazy { findViewById<Button>(R.id.button_delete) }

    val headerImage by lazy { findViewById<ImageView>(R.id.header_image) }
    val headerName by lazy { findViewById<TextView>(R.id.header_text_name) }
    val headerSymbol by lazy { findViewById<TextView>(R.id.header_text_symbol) }

    val layoutDate by lazy { findViewById<RelativeLayout>(R.id.layout_date) }
    val requiredDate by lazy { findViewById<TextView>(R.id.date) }

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

    val textSubmit by lazy { findViewById<TextView>(R.id.text_view_update) }

    var transactionDate = Calendar.getInstance().time

    var isUpdateTransaction = false
    var isDateChanged = false
    var backPressToPortfolio = false

    override fun onCreate(savedInstanceState: Bundle?) {

        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.darktheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_crypto_transaction)

        args.transaction?.let { transaction ->

            Log.i(TAG, transaction.date.time.toString())

            isUpdateTransaction = true
//
//            transactionDate = transaction.date

            setupUpdate(transaction)
            setupToolbarUpdate(transaction)
            setupHeaderUpdate(transaction)
            requiredDate.text = transaction.date.let { Utils.formatDate(it) }
        }

        Utils.hideKeyboardFromActivity(this)

        args.notTransactions?.let { transaction ->
//                transactionPairImageUrl = transaction.imageUrl
            backPressToPortfolio = transaction.backpressToPortfolio

            setupCreate(transaction)
            setupToolbarCreate(transaction)
            setupHeaderCreate(transaction)
            requiredDate.text = Utils.formatDate(transactionDate)
        }

        setupBody()

        presenter = CryptoTransactionPresenter(CryptoTransactionDataManager.getInstance(this), this)
        presenter.attachView()
    }


    private fun setupHeaderUpdate(transaction: Transaction) {

        Picasso.with(this)
                .load(transaction.baseImageUrl)
                .fit()
                .centerCrop()
                .transform(CircleTransform())
//                .placeholder(R.drawable.circle)
                .into(headerImage)
        headerName.text = transaction.symbol
//        headerSymbol.text = transactionPairSymbol

    }

    private fun setupHeaderCreate(transaction: NotTransaction) {

        Picasso.with(this)
                .load(transaction.baseUrl + transaction.currency.imageUrl)
                .fit()
                .centerCrop()
                .transform(CircleTransform())
//                .placeholder(R.drawable.circle)
                .into(headerImage)
        headerName.text = transaction.currency.symbol
//        headerSymbol.text = transactionPairSymbol

    }

    private fun setupCreate(transaction: NotTransaction) {
        buttonDelete.visibility = View.GONE
        textSubmit.text = "Create"
    }


    private fun setupBody() {

        layoutDate.setOnClickListener(this)
        layoutEmptyPair.setOnClickListener(this)
        layoutFilledPair.setOnClickListener(this)
        layoutEmptyExchange.setOnClickListener(this)
        layoutFilledExchange.setOnClickListener(this)
        radioButtonDeposit.setOnClickListener(this)
        radioButtonWithdrawl.setOnClickListener(this)
        textSellAll.setOnClickListener(this)
        textSubmit.setOnClickListener(this)
        buttonDelete.setOnClickListener(this)
    }

    private fun setupToolbarUpdate(transaction: Transaction) {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        supportActionBar?.title = "Edit ${transaction.symbol} Transaction"
    }

    private fun setupToolbarCreate(transaction: NotTransaction) {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        supportActionBar?.title = "Create ${transaction.currency.symbol} Transaction"
    }

    private fun setupUpdate(transaction: Transaction) {
        if (transaction.quantity > 0.toBigDecimal())
            radioButtonDeposit.isChecked = true
        else
            radioButtonWithdrawl.isChecked = true

        requiredExchange.text = transaction.exchange
        requiredPair.text = transaction.pairSymbol
        requiredPrice.setText(transaction.price.toString())
        if (transaction.quantity < 0.toBigDecimal())
            requiredQuantity.setText((transaction.quantity?.times((-1).toBigDecimal())).toString())
        else
            requiredQuantity.setText(transaction.quantity.toString())
        if (transaction.isDeducted!!)
            switchDeduct.isChecked = true

        edittextNotes.setText(transaction.notes)

        layoutEmptyExchange.visibility = View.GONE
        layoutFilledExchange.visibility = View.VISIBLE
        layoutEmptyPair.visibility = View.GONE
        layoutFilledPair.visibility = View.VISIBLE
    }


    override fun onClick(view: View?) {
        lateinit var i: Intent

        when (view?.id) {
            buttonDelete.id -> {
                presenter.deleteTransaction(args.transaction!!)
            }
            layoutDate.id -> {
                showDatePicker()
            }
            layoutEmptyPair.id -> {
                i = Intent(this, PickerPairActivity::class.java)
                i.putExtra(EXCHANGE_KEY, requiredExchange.text)
                i.putExtra(CRYPTO_KEY, getSymbol())
                startActivityForResult(i, REQUEST_CODE_PAIR)
            }
            layoutFilledPair.id -> {
                i = Intent(this, PickerPairActivity::class.java)
                i.putExtra(EXCHANGE_KEY, requiredExchange.text)
                i.putExtra(CRYPTO_KEY, getSymbol())
                startActivityForResult(i, REQUEST_CODE_PAIR)
            }
            layoutEmptyExchange.id -> {
                i = Intent(this, PickerExchangeActivity::class.java)
                i.putExtra(CRYPTO_KEY, getSymbol())
                startActivityForResult(i, REQUEST_CODE_EXCHANGE)
            }
            layoutFilledExchange.id -> {
                i = Intent(this, PickerExchangeActivity::class.java)
                i.putExtra(CRYPTO_KEY, getSymbol())
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
                presenter.getAllHoldings(getSymbol())

            }
            textSubmit.id -> {
                if (checkFields()) {
                    preventChanges()
                    if(transactionDate > Calendar.getInstance().time) {
                        transactionDate = Calendar.getInstance().time
                        Log.i(TAG, "1:aa    "+transactionDate)
                    }
                    if (isUpdateTransaction) {

                        val tempDate : Date
                        if(isDateChanged)
                            tempDate = transactionDate
                        else
                            tempDate = args.transaction?.date!!

                        Log.i(TAG, "1:a    "+args.transaction!!.date.time.toString())
                        Log.i(TAG, "1:b    "+transactionDate)
                        presenter.updateCryptoTransaction(args.transaction!!, radioButtonDeposit.isChecked, requiredExchange.text.trim().toString(), requiredPair.text.trim().toString(), java.lang.Float.parseFloat(requiredPrice.text.trim().toString()), java.lang.Float.parseFloat(requiredQuantity.text.trim().toString()), tempDate, switchDeduct.isChecked, edittextNotes.text.trim().toString())

                    }else
                        presenter.createCryptoTransaction(radioButtonDeposit.isChecked, requiredExchange.text.trim().toString(), requiredPair.text.trim().toString(), java.lang.Float.parseFloat(requiredPrice.text.trim().toString()), java.lang.Float.parseFloat(requiredQuantity.text.trim().toString()), transactionDate!!, switchDeduct.isChecked, edittextNotes.text.trim().toString())

                }
            }
        }
    }

    override fun showSellAllAmount(amount: BigDecimal) {
        requiredQuantity.setText(String.format(amount.toString()))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_EXCHANGE) {
            if (resultCode == Activity.RESULT_OK) {
                val exchange = data?.getStringExtra("data")
                showExchange(exchange)
                resetTradingPair()
            }
        }
        if (requestCode == REQUEST_CODE_PAIR) {
            if (resultCode == Activity.RESULT_OK) {
                val pair = data?.getStringExtra("data")
                showPair(pair)
                showPairChanged(pair)
                presenter.getCurrentPrice(getSymbol(), pair)
                requiredPrice.hint = "Price in $pair"
            }
        }
    }

    override fun showCurrentPrice(price: String) {
        Log.i("showCurrentPrice", price)
        requiredPrice.setText(price)
    }

    private fun showPairChanged(pair: String?) {
        if (radioButtonDeposit.isChecked)
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

    private fun getPairText(): CharSequence? {
        if (requiredPair.text.isBlank() )
            return ""
        return "${requiredPair.text} "
    }


    private fun checkFields(): Boolean {

        var canSubmit = true

        val shake = AnimationUtils.loadAnimation(this, R.anim.shake)

        if (requiredExchange.text.toString().trim().isBlank()) {
            layoutEmptyExchange.startAnimation(shake)
            println("No exchange chosen!")
            canSubmit = false
        }
        if (requiredPair.text.toString().trim().isBlank()) {
            layoutEmptyPair.startAnimation(shake)
            println("No pair chosen!")
            canSubmit = false
        }
        if (requiredPrice.text.toString().trim().isBlank()) {
            requiredPrice.startAnimation(shake)
            println("No price chosen!")
            canSubmit = false
        }

        if (requiredQuantity.text.toString().trim().isBlank()) {
            requiredQuantity.startAnimation(shake)
            println("No quantity chosen!")
            canSubmit = false
        }

        return canSubmit
    }


    private fun preventChanges() {

    }

    override fun getSymbol(): String {
        return args.transaction?.symbol ?: args.notTransactions?.currency?.symbol.toString()
    }

    override fun onTransactionComplete() {
        if (isUpdateTransaction) {
            super.onBackPressed()
        } else {
            if (args.backPressOnEnd)
                super.onBackPressed()
            else
                loadBaseActivityWithoutRestart()
        }
    }

    private fun loadBaseActivityWithoutRestart() {

        val bundle = Bundle()
        bundle.putInt("fragment_key", 2)
        val intent = Intent(this, BaseActivity::class.java)
        intent.putExtras(bundle)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)

    }

    override fun onTransactionDeleted() {
        super.onBackPressed()
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {


        val temp = args.transaction?.date?.time

        transactionDate?.year = year - 1900
        transactionDate?.month = monthOfYear
        transactionDate?.date = dayOfMonth

        if(temp != null)
            args.transaction!!.date.time = temp

        showTimePicker()
    }

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {

        val temp = args.transaction?.date?.time

        transactionDate?.hours = hourOfDay
        transactionDate?.minutes = minute
        transactionDate?.seconds = second

        requiredDate.text = transactionDate?.let { Utils.formatDate(it) }

        if(temp != null)
            args.transaction!!.date.time = temp

        isDateChanged = true

    }


    private fun showDatePicker() {

        val now = Calendar.getInstance()
        val dpd = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR), // Initial year selection
                now.get(Calendar.MONTH), // Initial month selection
                now.get(Calendar.DAY_OF_MONTH) // Inital day selection

        )
        dpd.maxDate = Calendar.getInstance()

        dpd.show(fragmentManager, "Datepickerdialog")
    }

    private fun showTimePicker() {
        val now = Calendar.getInstance()
        val dpd = TimePickerDialog.newInstance(
                this,
                now.get(Calendar.HOUR_OF_DAY), // Initial hour selection
                now.get(Calendar.MINUTE), // Initial minute selection
                false
        )
        dpd.show(fragmentManager, "Timepickerdialog")
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

    override fun setPresenter(presenter: CryptoTransactionContract.Presenter) {
        this.presenter = checkNotNull(presenter)
    }

    companion object {
        val REQUEST_CODE_PAIR = 1
        val REQUEST_CODE_EXCHANGE = 2

        val EXCHANGE_KEY = "key_exchange"
        val PAIR_KEY = "key_pair"
        val CRYPTO_KEY = "key_crypto"

        val TAG = "CryptoTransActivity"
    }

}
