package com.jonnycaley.cryptomanager.ui.transactions.fiat

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.jonnycaley.cryptomanager.R
import android.content.Intent
import android.widget.EditText
import com.jonnycaley.cryptomanager.ui.pickers.currency.PickerCurrencyActivity
import com.jonnycaley.cryptomanager.ui.pickers.exchange.PickerExchangeActivity
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import java.text.SimpleDateFormat
import java.util.*
import android.view.animation.AnimationUtils
import android.widget.ToggleButton
import com.jonnycaley.cryptomanager.ui.base.BaseArgs
import com.jonnycaley.cryptomanager.utils.Constants
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import java.text.ParseException

class FiatTransactionActivity : AppCompatActivity(), FiatTransactionContract.View, View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private lateinit var presenter: FiatTransactionContract.Presenter

    val args by lazy { FiatTransactionArgs.deserializeFrom(intent) }

    val toggleButtonType by lazy { findViewById<ToggleButton>(R.id.toggle_button_type) }

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

    val layoutDate by lazy { findViewById<RelativeLayout>(R.id.layout_date) }

    var yearSet : Int = 0
    var monthSet : Int = 0
    var daySet : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fiat_transaction)

        setupToolbar()
        setupFields()

        presenter = FiatTransactionPresenter(FiatTransactionDataManager.getInstance(this), this)
        presenter.attachView()
    }

    override fun showProgressBar() {

    }

    override fun onTransactionComplete() {
        BaseArgs(2).launch(this)
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        yearSet = year
        monthSet = monthOfYear
        daySet = dayOfMonth
        showTimePicker()
    }

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        requiredDate.text = formatDate(daySet, monthSet, yearSet, hourOfDay, minute)
    }

    private fun formatDate(dayOfMonth: Int, monthOfYear: Int, year: Int, hour : Int, minute : Int): CharSequence? {

        val correctMonth = monthOfYear + 1
        val strCurrentDate = "$dayOfMonth $correctMonth $year $hour $minute"
        var format = SimpleDateFormat("dd M yyyy HH mm")
        val newDate = format.parse(strCurrentDate)

        format = SimpleDateFormat(Constants.dateFormat)
        val date = format.format(newDate)

        return date
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
                    presenter.saveFiatTransaction(toggleButtonType.isChecked, requiredExchange.text.trim().toString(), requiredCurrency.text.trim().toString(), requiredQuantity.text.toString().toDouble(), stringToDate(requiredDate.text.trim()), notes.text.trim().toString())
                }
            }
        }
    }

    fun stringToDate(dateString : CharSequence): Date {

        val format = SimpleDateFormat(Constants.dateFormat)

        var date : Date
        try {
            date = format.parse(dateString.toString())
            return date
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return Date()
    }

    private fun preventFieldChanges() {

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
            println("No currency chosen!")
            canSubmit = false
        }
        if (requiredQuantity.text.toString().trim() == "") {
            requiredQuantity.startAnimation(shake)
            println("No quantity chosen!")
            canSubmit = false
        }

        return canSubmit
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
        dpd.show(fragmentManager, "Datepickerdialog")
    }


    fun getCurrentDate(): String {

        val c = Calendar.getInstance().time
        val df = SimpleDateFormat(Constants.dateFormat)

        return df.format(c)
    }

    private fun setupFields() {

        requiredCurrency.text = args.currency
        requiredDate.text = getCurrentDate()

        layoutExchangeFilled.setOnClickListener(this)
        layoutExchangeEmpty.setOnClickListener(this)
        layoutCurrencyFilled.setOnClickListener(this)
        layoutCurrencyEmpty.setOnClickListener(this)
        layoutDate.setOnClickListener(this)

        textViewSubmit.setOnClickListener(this)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = args.currency
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
    }
}