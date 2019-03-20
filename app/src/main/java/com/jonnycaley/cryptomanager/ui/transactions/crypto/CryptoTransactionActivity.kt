package com.jonnycaley.cryptomanager.ui.transactions.crypto

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
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
import com.rengwuxian.materialedittext.MaterialEditText
import com.squareup.picasso.Picasso
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.activity_update_crypto_transaction.*
import java.math.BigDecimal
import java.util.*

class CryptoTransactionActivity : AppCompatActivity(), CryptoTransactionContract.View, View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private lateinit var presenter: CryptoTransactionContract.Presenter

    val args by lazy { CryptoTransactionArgs.deserializeFrom(intent) }

    val layout by lazy { findViewById<RelativeLayout>(R.id.layout) }

    val buttonDelete by lazy { findViewById<ImageView>(R.id.button_delete) }

    val headerImage by lazy { findViewById<ImageView>(R.id.header_image) }
    val headerName by lazy { findViewById<TextView>(R.id.header_text_name) }
    val headerSymbol by lazy { findViewById<TextView>(R.id.header_text_symbol) }

    val layoutDate by lazy { findViewById<RelativeLayout>(R.id.layout_date) }
    val requiredDate by lazy { findViewById<TextView>(R.id.date) }

    val layoutEmptyPair by lazy { findViewById<RelativeLayout>(R.id.layout_empty_trading_pair) }
    val layoutFilledPair by lazy { findViewById<RelativeLayout>(R.id.layout_filled_trading_pair) }
    val requiredPair by lazy { findViewById<TextView>(R.id.trading_pair) }

    val layoutQuantity by lazy { findViewById<RelativeLayout>(R.id.layout_quantity) }
    val layoutPrice by lazy { findViewById<RelativeLayout>(R.id.layout_price) }

    val layoutEmptyExchange by lazy { findViewById<RelativeLayout>(R.id.layout_empty_exchange) }
    val layoutFilledExchange by lazy { findViewById<RelativeLayout>(R.id.layout_filled_exchange) }
    val requiredExchange by lazy { findViewById<TextView>(R.id.exchange) }

    val textPrice by lazy { findViewById<TextView>(R.id.text_price) }
    val requiredPrice by lazy { findViewById<com.rengwuxian.materialedittext.MaterialEditText>(R.id.edit_text_price) }
    val requiredQuantity by lazy { findViewById<com.rengwuxian.materialedittext.MaterialEditText>(R.id.edit_text_quantity) }

    val textDeduction by lazy { findViewById<TextView>(R.id.text_deducation) }

    val textSellAll by lazy { findViewById<TextView>(R.id.text_sell_all) }

    val radioButtonWithdrawl by lazy { findViewById<RadioButton>(R.id.radio_button_withdrawl) }
    val radioButtonDeposit by lazy { findViewById<RadioButton>(R.id.radio_button_deposit) }

    val switchDeduct by lazy { findViewById<Switch>(R.id.switch_deduct) }

    val edittextNotes by lazy { findViewById<MaterialEditText>(R.id.edit_text_notes) }

    val textSubmit by lazy { findViewById<TextView>(R.id.text_view_update) }
    val submitProgressBar by lazy { findViewById<ProgressBar>(R.id.progress_bar_submit) }

    var transactionDate = Calendar.getInstance().time

    var isUpdateTransaction = false
    var isDateChanged = false
    var backPressToPortfolio = false

    override fun onCreate(savedInstanceState: Bundle?) {

        if(Utils.isDarkTheme()) {
            setTheme(R.style.darktheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_crypto_transaction)

        if(!Utils.isDarkTheme()) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
            buttonDelete.setImageResource(R.drawable.baseline_delete_black_24)
        }

        args.transaction?.let { transaction ->

            isUpdateTransaction = true
            setupUpdate(transaction)
            setupToolbarUpdate(transaction)
            setupHeaderUpdate(transaction)
            requiredDate.text = transaction.date.let { Utils.formatDate(it) }
        }

        Utils.hideKeyboardFromActivity(this)

        args.notTransactions?.let { transaction ->
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

    override fun showProgressBar() {
        submitProgressBar.visibility = View.VISIBLE
    }

    override fun disableTouchEvents() {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    override fun hideProgressBar() {
        submitProgressBar.visibility = View.GONE
    }

    override fun enableTouchEvents() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    override fun showError() {
        Toast.makeText(this, resources.getString(R.string.error_occurred), Toast.LENGTH_SHORT).show()
    }

    override fun showNoInternet() {
        Toast.makeText(this, resources.getString(R.string.internet_required), Toast.LENGTH_SHORT).show()
    }

    override fun getName(): String {
        args.transaction?.let { return it.name }
        args.notTransactions?.let { return it.currency.coinName ?: it.currency.fullName ?: "" }
        return ""
    }

    private fun setupHeaderUpdate(transaction: Transaction) {

        Picasso.with(this)
                .load(transaction.baseImageUrl)
                .fit()
                .centerCrop()
                .transform(CircleTransform())
                .into(headerImage)
        headerName.text = transaction.symbol

    }

    private fun setupHeaderCreate(transaction: NotTransaction) {

        Picasso.with(this)
                .load(transaction.baseUrl + transaction.currency.imageUrl)
                .fit()
                .centerCrop()
                .transform(CircleTransform())
                .into(headerImage)
        headerName.text = transaction.currency.symbol

    }

    private fun setupCreate(transaction: NotTransaction) {
        buttonDelete.visibility = View.GONE
        textSubmit.text = "Create"
    }


    private fun setupBody() {

        layoutPrice.setOnClickListener(this)
        layoutQuantity.setOnClickListener(this)
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
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar_title.text = "Edit ${transaction.name} Transaction"
    }

    private fun setupToolbarCreate(transaction: NotTransaction) {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_title.text = "${transaction.currency.coinName} - ${transaction.currency.symbol}"
    }

    private fun setupUpdate(transaction: Transaction) {
        requiredPair.text = transaction.symbol + "/" + transaction.pairSymbol
        textPrice.text = "Price in ${transaction.pairSymbol}"
        if (transaction.quantity > 0.toBigDecimal()) {
            textDeduction.text = "Deduct from ${getPairText()}holdings"
            radioButtonDeposit.isChecked = true
        }
        else {
            textDeduction.text = "Add to ${getPairText()}holdings"
            radioButtonWithdrawl.isChecked = true
        }

        requiredExchange.text = transaction.exchange
        requiredPrice.setText(transaction.price.toString())
        if (transaction.quantity < 0.toBigDecimal())
            requiredQuantity.setText((transaction.quantity.times((-1).toBigDecimal())).toString())
        else
            requiredQuantity.setText(transaction.quantity.toString())
        if (transaction.isDeducted)
            switchDeduct.isChecked = true

        edittextNotes.setText(transaction.notes)

        layoutEmptyExchange.visibility = View.GONE
        layoutFilledExchange.visibility = View.VISIBLE
        layoutEmptyPair.visibility = View.GONE
        layoutFilledPair.visibility = View.VISIBLE
    }

    var isLoading = false


    override fun onClick(view: View?) {
        lateinit var i: Intent

            when (view?.id) {
                buttonDelete.id -> {
                    val diaBox = AskOption()
                    diaBox.show()
                }
                layoutDate.id -> {
                    showDatePicker()
                }
                layoutPrice.id -> {
                    //TODO: RE ADD FOCUS TO EDITTEXT WITH NUMERICAL KEYBOARD?
                }
                layoutQuantity.id -> {
                    //TODO: RE ADD FOCUS TO EDITTEXT WITH NUMERICAL KEYBOARD?
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
                        if (transactionDate > Calendar.getInstance().time) {
                            transactionDate = Calendar.getInstance().time
                        }

                        args.transaction?.let { transaction ->
                            println("update")
                            val tempDate: Date
                            if (isDateChanged)
                                tempDate = transactionDate
                            else {
                                tempDate = transaction.date
                            }
                        presenter.updateCryptoTransaction(transaction, radioButtonDeposit.isChecked, requiredExchange.text.trim().toString(), requiredPair.text.trim().substring(requiredPair.text.indexOf("/")+1), java.lang.Float.parseFloat(requiredPrice.text?.trim().toString()), java.lang.Float.parseFloat(requiredQuantity.text?.trim().toString()), tempDate, switchDeduct.isChecked, edittextNotes.text?.trim().toString())
                        }
                        if (!isUpdateTransaction) {
                            presenter.createCryptoTransaction(radioButtonDeposit.isChecked, requiredExchange.text.trim().toString(), requiredPair.text.trim().substring(requiredPair.text.indexOf("/") + 1), java.lang.Float.parseFloat(requiredPrice.text?.trim().toString()), java.lang.Float.parseFloat(requiredQuantity.text?.trim().toString()), transactionDate, switchDeduct.isChecked, edittextNotes.text?.trim().toString())
                        }
                    }
                }
            }
    }


    private fun AskOption(): AlertDialog {
        return AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Delete Transaction")
                .setMessage("Are you sure you want to delete this transaction?")
                .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, whichButton ->
                    args.transaction?.let { presenter.deleteTransaction(it) }
                    dialog.dismiss()
                })
                .setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                })
                .create()
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
                textPrice.text = "Price"
            }
        }
        if (requestCode == REQUEST_CODE_PAIR) {
            if (resultCode == Activity.RESULT_OK) {
                val pair = data?.getStringExtra("data")
                showPair(pair)
                showPairChanged(pair)
                pair?.let { pairSymbol -> presenter.getCurrentPrice(getSymbol(), pairSymbol) }
                textPrice.text = "Price in $pair"
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
        requiredPair.text = args.transaction?.symbol + "/" + pair
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
        if (requiredPair.text.isBlank())
            return ""
        return "${requiredPair.text.substring(requiredPair.text.indexOf("/") + 1)} "
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
        println(canSubmit)
        return canSubmit
    }


    private fun preventChanges() {
        layout.isClickable = false
    }

    override fun getSymbol(): String {
        return args.transaction?.symbol ?: args.notTransactions?.currency?.symbol.toString()
    }

    override fun onTransactionComplete() {
        if (isUpdateTransaction) {
            super.onBackPressed()
        } else {
            if (args.backpressToPortfolio)
                loadBaseActivityWithoutRestart()
            else
                super.onBackPressed()
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

        if (temp != null)
            args.transaction?.date?.time = temp

        showTimePicker()
    }

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {

        val temp = args.transaction?.date?.time

        transactionDate?.hours = hourOfDay
        transactionDate?.minutes = minute
        transactionDate?.seconds = second

        requiredDate.text = transactionDate?.let { Utils.formatDate(it) }

        if (temp != null)
            args.transaction?.date?.time = temp

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
