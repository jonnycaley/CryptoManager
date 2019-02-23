package com.jonnycaley.cryptomanager.ui.transactions.fiat.update

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import com.jonnycaley.cryptomanager.ui.pickers.currency.PickerCurrencyActivity
import com.jonnycaley.cryptomanager.ui.pickers.exchange.PickerExchangeActivity
import com.jonnycaley.cryptomanager.utils.Constants
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import java.text.SimpleDateFormat
import java.util.*

class UpdateFiatTransactionActivity : AppCompatActivity(), UpdateFiatTransactionContract.View, View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private lateinit var presenter: UpdateFiatTransactionContract.Presenter

    val args by lazy { UpdateFiatTransactionArgs.deserializeFrom(intent) }

    val radioButtonDeposit by lazy { findViewById<RadioButton>(R.id.radio_button_deposit) }
    val radioButtonWithdrawl by lazy { findViewById<RadioButton>(R.id.radio_button_withdrawl) }

    val requiredExchange by lazy { findViewById<TextView>(R.id.text_chosen_exchange) }
    val layoutExchangeFilled by lazy { findViewById<RelativeLayout>(R.id.layout_exchange_filled) }
    val layoutExchangeEmpty by lazy { findViewById<RelativeLayout>(R.id.layout_exchange_empty) }

    val requiredCurrency by lazy { findViewById<TextView>(R.id.currency) }
    val layoutCurrencyFilled by lazy { findViewById<RelativeLayout>(R.id.layout_currency_filled) }
    val layoutCurrencyEmpty by lazy { findViewById<RelativeLayout>(R.id.layout_currency_empty) }

    val requiredQuantity by lazy { findViewById<EditText>(R.id.edit_text_quantity) }
    val notes by lazy { findViewById<EditText>(R.id.edit_text_notes) }

    val textViewSubmit by lazy { findViewById<TextView>(R.id.text_view_submit) }
    val requiredDate by lazy { findViewById<TextView>(R.id.date) }

    val buttonDelete by lazy { findViewById<Button>(R.id.button_delete)}

    val layoutDate by lazy { findViewById<RelativeLayout>(R.id.layout_date) }

//    val chosenDate by lazy { args.transaction.date }

    var transactionDate = Calendar.getInstance().time

    var isDateChanged = false


    override fun onCreate(savedInstanceState: Bundle?) {

        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.darktheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_fiat_transaction)

        fillFields()

        setupToolbar()

        presenter = UpdateFiatTransactionPresenter(UpdateFiatTransactionDataManager.getInstance(this), this)
        presenter.attachView()
    }

    private fun fillFields() {
        if(args.transaction.quantity > 0.toBigDecimal() ){
                radioButtonDeposit.isChecked = true
            } else {
                radioButtonWithdrawl.isChecked = true
            }

        layoutExchangeEmpty.visibility = View.GONE
        layoutExchangeFilled.visibility = View.VISIBLE
        requiredExchange.text = args.transaction.exchange

        layoutCurrencyEmpty.visibility = View.GONE
        layoutCurrencyFilled.visibility = View.VISIBLE
        requiredCurrency.text = args.transaction.symbol

        requiredQuantity.setText(args.transaction.quantity.toString())

        requiredDate.text = formatDate(args.transaction.date)

        layoutExchangeFilled.setOnClickListener(this)
        layoutExchangeEmpty.setOnClickListener(this)
        layoutCurrencyFilled.setOnClickListener(this)
        layoutCurrencyEmpty.setOnClickListener(this)
        layoutDate.setOnClickListener(this)
        buttonDelete.setOnClickListener(this)

        textViewSubmit.setOnClickListener(this)
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
                val exchange = data?.getStringExtra("data")
                layoutCurrencyEmpty.visibility = View.GONE
                layoutCurrencyFilled.visibility = View.VISIBLE
                requiredCurrency.text = exchange
            }
        }
    }

    override fun onClick(v: View?) {
        lateinit var i: Intent

        when (v?.id) {
            buttonDelete.id -> {
                presenter.deleteTransaction(args.transaction)
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
            textViewSubmit.id -> {
                if (checkFields()) {
                    preventFieldChanges()
                    var correctQuantity: Float

                    if (radioButtonWithdrawl.isChecked)
                        correctQuantity = (requiredQuantity.text.toString().toFloat() * (-1).toFloat())
                    else
                        correctQuantity = requiredQuantity.text.toString().toFloat()


                    val tempDate : Date
                    if(isDateChanged)
                        tempDate = transactionDate
                    else
                        tempDate = args.transaction?.date!!


                    presenter.updateFiatTransaction(getTransaction(), requiredExchange.text.trim().toString(), requiredCurrency.text.trim().toString(), correctQuantity, tempDate, notes.text.trim().toString())
                }
            }
        }
    }

    override fun onTransactionUpdated() {
        super.onBackPressed()
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
        requiredDate.text = formatDate(transactionDate)

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

    private fun formatDate(date: Date): CharSequence? {

        val format = SimpleDateFormat(Constants.dateFormat)
        return format.format(date)
    }

    override fun getTransaction(): Transaction {
        return args.transaction
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = args.transaction.symbol
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

    override fun setPresenter(presenter: UpdateFiatTransactionContract.Presenter) {
        this.presenter = checkNotNull(presenter)
    }

    companion object {
        val REQUEST_CODE_EXCHANGE = 1
        val REQUEST_CODE_CURRENCY = 2
    }
}

