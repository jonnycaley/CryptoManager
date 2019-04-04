package com.jonnycaley.cryptomanager.ui.transactions.fiat

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import com.jonnycaley.cryptomanager.ui.base.BaseActivity
import com.jonnycaley.cryptomanager.ui.pickers.currency.PickerCurrencyActivity
import com.jonnycaley.cryptomanager.ui.pickers.exchange.PickerExchangeActivity
import com.jonnycaley.cryptomanager.utils.Constants
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import java.text.SimpleDateFormat
import java.util.*
import android.content.DialogInterface
import android.util.Log
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import com.jonnycaley.cryptomanager.utils.Utils
import kotlinx.android.synthetic.main.activity_update_fiat_transaction.*

class FiatTransactionActivity : AppCompatActivity(), FiatTransactionContract.View, View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private lateinit var presenter: FiatTransactionContract.Presenter

    val args by lazy { FiatTransactionArgs.deserializeFrom(intent) }

    val textviewDeposit by lazy { findViewById<TextView>(R.id.text_view_deposit) }
    val textviewWithdrawl by lazy { findViewById<TextView>(R.id.text_view_withdrawl) }

    val requiredExchange by lazy { findViewById<TextView>(R.id.text_chosen_exchange) }
    val layoutExchangeFilled by lazy { findViewById<RelativeLayout>(R.id.layout_exchange_filled) }
    val layoutExchangeEmpty by lazy { findViewById<RelativeLayout>(R.id.layout_exchange_empty) }

    val requiredCurrency by lazy { findViewById<TextView>(R.id.currency) }
    val layoutCurrencyFilled by lazy { findViewById<RelativeLayout>(R.id.layout_currency_filled) }
    val layoutCurrencyEmpty by lazy { findViewById<RelativeLayout>(R.id.layout_currency_empty) }

    val layoutQuantity by lazy { findViewById<RelativeLayout>(R.id.layout_quantity) }

    val requiredQuantity by lazy { findViewById<EditText>(R.id.edit_text_quantity) }
    val notes by lazy { findViewById<EditText>(R.id.edit_text_notes) }


    val layoutDeposit by lazy { findViewById<LinearLayout>(R.id.layout_deposit_checked) }
    val layoutWithdrawl by lazy { findViewById<LinearLayout>(R.id.layout_withdrawl_checked) }

    val progressButtonCreateDeposit by lazy { findViewById<com.ekalips.fancybuttonproj.FancyButton>(R.id.progress_button_create_deposit) }
    val progressButtonCreateWithdrawl by lazy { findViewById<com.ekalips.fancybuttonproj.FancyButton>(R.id.progress_button_create_withdrawl) }
    val progressButtonUpdateDeposit by lazy { findViewById<com.ekalips.fancybuttonproj.FancyButton>(R.id.progress_button_update_deposit) }
    val progressButtonUpdateWithdrawl by lazy { findViewById<com.ekalips.fancybuttonproj.FancyButton>(R.id.progress_button_update_withdrawl) }

    val requiredDate by lazy { findViewById<TextView>(R.id.date) }

    val buttonDelete by lazy { findViewById<ImageView>(R.id.button_delete)}

    val layoutDate by lazy { findViewById<RelativeLayout>(R.id.layout_date) }

//    val chosenDate by lazy { args.transaction.date }

    var transactionDate = Calendar.getInstance().time

    var isDateChanged = false

    override fun onCreate(savedInstanceState: Bundle?) {

        if(Utils.isDarkTheme()) {
            setTheme(R.style.darktheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_fiat_transaction)

        if(!Utils.isDarkTheme()) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
            buttonDelete.setImageResource(R.drawable.baseline_delete_black_24)
        }

        progressButtonCreateDeposit.visibility = View.GONE
        progressButtonCreateWithdrawl.visibility = View.GONE
        progressButtonUpdateDeposit.visibility = View.GONE
        progressButtonUpdateWithdrawl.visibility = View.GONE

        fillFields()
        setupUpdate()
        setupCreate()

        presenter = FiatTransactionPresenter(FiatTransactionDataManager.getInstance(this), this)
        presenter.attachView()
    }

    private fun AskOption(): AlertDialog {
        return AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Delete Transaction")
                .setMessage("Are you sure you want to delete this transaction?")

                .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, whichButton ->
                    //your deleting code
                    args.transaction?.let { presenter.deleteTransaction(it) }
                    dialog.dismiss()
                })
                .setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss() })
                .create()

    }

    private fun setupCreate() {
        args.currency?.let { currency ->
            setupToolbarCreate(currency)
            requiredDate.text = Utils.formatDate(transactionDate)
            requiredCurrency.text = currency
            progressButtonCreateDeposit.visibility = View.VISIBLE
        }
    }

    override fun startUpdateDepositProgress() {
        progressButtonUpdateDeposit.collapse()
    }

    override fun startUpdateWithdrawlProgress() {
        progressButtonUpdateWithdrawl.collapse()
    }

    override fun stopUpdateDepositProgress() {
        progressButtonUpdateDeposit.expand()
    }

    override fun stopUpdateWithdrawlProgress() {
        progressButtonUpdateWithdrawl.expand()
    }

    override fun startCreateDepositProgress() {
        progressButtonCreateDeposit.collapse()
    }

    override fun startCreateWithdrawlProgress() {
        progressButtonCreateWithdrawl.collapse()
    }

    override fun stopCreateDepositProgress() {
        progressButtonCreateWithdrawl.expand()
    }

    override fun stopCreateWithdrawlProgress() {
        progressButtonCreateWithdrawl.expand()
    }

    override fun disableTouchEvents() {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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

    private fun setupUpdate() {

        args.transaction?.let { transaction ->
            if(transaction.quantity > 0.toBigDecimal() ){
                showDepositChecked()
                hideWithdrawlChecked()
                progressButtonUpdateDeposit.visibility = View.VISIBLE
            } else {
                showWithdrawlChecked()
                hideDepositChecked()
                progressButtonUpdateWithdrawl.visibility = View.VISIBLE
            }

            buttonDelete.visibility = View.VISIBLE

            layoutExchangeEmpty.visibility = View.GONE
            layoutExchangeFilled.visibility = View.VISIBLE

            layoutCurrencyEmpty.visibility = View.GONE
            layoutCurrencyFilled.visibility = View.VISIBLE

            requiredExchange.text = transaction.exchange
            requiredCurrency.text = transaction.symbol
            requiredQuantity.setText(String.format(transaction.quantity.abs().toString()))
            requiredDate.text = Utils.formatDate(transaction.date)
            buttonDelete.visibility = View.VISIBLE
            setupToolbarUpdate(transaction)
        }
    }

    private fun hideDepositChecked() {
        layoutDeposit.visibility = View.GONE
    }

    private fun hideWithdrawlChecked() {
        layoutWithdrawl.visibility = View.GONE
    }

    private fun showWithdrawlChecked() {
        layoutWithdrawl.visibility = View.VISIBLE
    }

    private fun showDepositChecked() {
        layoutDeposit.visibility = View.VISIBLE
    }

    private fun fillFields() {

        layoutExchangeFilled.setOnClickListener(this)
        layoutExchangeEmpty.setOnClickListener(this)
        layoutCurrencyFilled.setOnClickListener(this)
        layoutCurrencyEmpty.setOnClickListener(this)
        layoutDate.setOnClickListener(this)
        buttonDelete.setOnClickListener(this)
        layoutQuantity.setOnClickListener(this)
        textviewDeposit.setOnClickListener(this)
        textviewWithdrawl.setOnClickListener(this)

        progressButtonCreateDeposit.setOnClickListener(this)
        progressButtonCreateWithdrawl.setOnClickListener(this)
        progressButtonUpdateDeposit.setOnClickListener(this)
        progressButtonUpdateWithdrawl.setOnClickListener(this)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_EXCHANGE) {
            if (resultCode == Activity.RESULT_OK) {
                val exchange = data?.getStringExtra("data")
                layoutExchangeEmpty.visibility = View.GONE
                layoutExchangeFilled.visibility = View.VISIBLE
                requiredExchange.text = exchange
            }
        }
        if (requestCode == REQUEST_CODE_CURRENCY) {
            if (resultCode == Activity.RESULT_OK) {
                val currency = data?.getStringExtra("data")
                layoutCurrencyEmpty.visibility = View.GONE
                layoutCurrencyFilled.visibility = View.VISIBLE
                requiredCurrency.text = currency
                toolbar_title.text = currency
            }
        }
    }

    override fun onClick(v: View?) {
        lateinit var i: Intent

        when (v?.id) {
            buttonDelete.id -> {
                val diaBox = AskOption()
                diaBox.show()
//                args.transaction?.let { presenter.deleteTransaction(it) }
            }
            layoutQuantity.id -> {
                focusEdittext(requiredQuantity)
                //TODO: RE ADD FOCUS TO EDITTEXT WITH NUMERICAL KEYBOARD?
            }
            layoutExchangeFilled.id -> {
                i = Intent(this, PickerExchangeActivity::class.java)
                startActivityForResult(i, REQUEST_CODE_EXCHANGE)
            }
            layoutExchangeEmpty.id -> {
                i = Intent(this, PickerExchangeActivity::class.java)
                startActivityForResult(i, REQUEST_CODE_EXCHANGE)
            }
            layoutCurrencyFilled.id -> {
                i = Intent(this, PickerCurrencyActivity::class.java)
                startActivityForResult(i, REQUEST_CODE_CURRENCY)
            }
            layoutCurrencyEmpty.id -> {
                i = Intent(this, PickerCurrencyActivity::class.java)
                startActivityForResult(i, REQUEST_CODE_CURRENCY)
            }
            layoutDate.id -> {
                showDatePicker()
            }
            textviewDeposit.id -> {
                showDepositChecked()
                hideWithdrawlChecked()

                if(args.transaction != null) {
                    showUpdateDeposit()
                    hideUpdateWithdrawl()
                }
                else {
                    showCreateDeposit()
                    hideCreateWithdrawl()
                }
            }
            textviewWithdrawl.id -> {
                showWithdrawlChecked()
                hideDepositChecked()
                if(args.transaction != null) {
                    hideUpdateDeposit()
                    showUpdateWithdrawl()
                }
                else {
                    hideCreateDeposit()
                    showCreateWithdrawl()
                }
            }
            progressButtonCreateDeposit.id -> {
                startProcess()
            }
            progressButtonUpdateDeposit.id -> {
                startProcess()
            }
            progressButtonCreateWithdrawl.id -> {
                startProcess()
            }
            progressButtonUpdateWithdrawl.id -> {
                startProcess()
            }
        }
    }

    private fun startProcess() {
        if (checkFields()) {
            preventFieldChanges()
            val correctQuantity: Float

            if (layoutWithdrawl.visibility == View.VISIBLE)
                correctQuantity = (requiredQuantity.text.toString().toFloat() * (-1).toFloat())
            else
                correctQuantity = requiredQuantity.text.toString().toFloat()

            if(transactionDate > Calendar.getInstance().time)
                transactionDate = Calendar.getInstance().time

            val tempDate : Date

            if(isDateChanged)
                tempDate = transactionDate

            else
                tempDate = args.transaction?.date ?: transactionDate

            args.transaction?.let { transaction ->
                presenter.updateFiatTransaction(transaction, requiredExchange.text.trim().toString(), requiredCurrency.text.trim().toString(), correctQuantity, tempDate, notes.text.trim().toString())
            }

            if(args.transaction == null)
                presenter.createFiatTransaction(requiredExchange.text.trim().toString(), requiredCurrency.text.trim().toString(), correctQuantity, tempDate, notes.text.trim().toString())
        }
    }

    private fun showCreateWithdrawl() {
        progressButtonCreateWithdrawl.visibility = View.VISIBLE
    }

    private fun hideCreateDeposit() {
        progressButtonCreateDeposit.visibility = View.GONE
    }

    private fun showUpdateWithdrawl() {
        progressButtonUpdateWithdrawl.visibility = View.VISIBLE
    }

    private fun hideUpdateDeposit() {
        progressButtonUpdateDeposit.visibility = View.GONE
    }

    private fun hideCreateWithdrawl() {
        progressButtonCreateWithdrawl.visibility = View.GONE
    }

    private fun showCreateDeposit() {
        progressButtonCreateDeposit.visibility = View.VISIBLE
    }

    private fun hideUpdateWithdrawl() {
        progressButtonUpdateWithdrawl.visibility = View.GONE
    }

    private fun showUpdateDeposit() {
        progressButtonUpdateDeposit.visibility = View.VISIBLE
    }

    override fun onTransactionUpdated() {
        super.onBackPressed()
    }

    override fun onTransactionCreated() {
        if(args.backpressToPortfolio)
            loadBaseActivityWithoutRestart()
        else
            super.onBackPressed()
    }


    fun focusEdittext(editText: EditText){
        editText.requestFocus()
        val imm : InputMethodManager =  getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }


    private fun loadBaseActivityWithoutRestart() {

        val bundle = Bundle()
        bundle.putInt("fragment_key", 2)
        val intent = Intent(this, BaseActivity::class.java)
        intent.putExtras(bundle)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)

    }

    private fun checkFields(): Boolean {

        var canSubmit = true

        val shake = AnimationUtils.loadAnimation(this, R.anim.shake)

        if (requiredExchange.text.toString().trim() == "") {
            layoutExchangeEmpty.startAnimation(shake)
            println("No exchange chosen!")
            canSubmit = false
        }
        if (requiredCurrency.text.toString().trim() == "") {
            layoutCurrencyEmpty.startAnimation(shake)
            println("No symbol chosen!")
            canSubmit = false
        }
        if (requiredQuantity.text.toString().trim() == "") {
            requiredQuantity.startAnimation(shake)
            println("No quantity chosen!")
            canSubmit = false
        }
        return canSubmit
    }

    private fun preventFieldChanges() {

    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {

        transactionDate.year = year - 1900
        transactionDate.month = monthOfYear
        transactionDate.date = dayOfMonth

        showTimePicker()
    }

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {

        transactionDate.hours = hourOfDay
        transactionDate.minutes = minute
        transactionDate.seconds = second
        requiredDate.text = Utils.formatDate(transactionDate)

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
                now.get(Calendar.HOUR_OF_DAY), // Initial year selection
                now.get(Calendar.MINUTE), // Initial month selection
                false
        )
        dpd.show(fragmentManager, "Datepickerdialog")
    }

    private fun setupToolbarUpdate(transaction: Transaction) {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_title.text = transaction.symbol
    }


    private fun setupToolbarCreate(currency: String) {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_title.text = currency
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

    override fun setPresenter(presenter: FiatTransactionContract.Presenter) {
        this.presenter = checkNotNull(presenter)
    }

    companion object {
        val REQUEST_CODE_EXCHANGE = 1
        val REQUEST_CODE_CURRENCY = 2

        val TAG = "FiatTransActivity"
    }
}

