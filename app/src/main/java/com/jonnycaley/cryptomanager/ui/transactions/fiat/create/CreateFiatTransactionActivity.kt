package com.jonnycaley.cryptomanager.ui.transactions.fiat.create

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import com.jonnycaley.cryptomanager.R
import android.content.Intent
import android.support.v7.app.AppCompatDelegate
import com.jonnycaley.cryptomanager.ui.pickers.currency.PickerCurrencyActivity
import com.jonnycaley.cryptomanager.ui.pickers.exchange.PickerExchangeActivity
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import java.util.*
import android.view.animation.AnimationUtils
import android.widget.*
import com.jonnycaley.cryptomanager.ui.base.BaseActivity
import com.jonnycaley.cryptomanager.ui.base.BaseArgs
import com.jonnycaley.cryptomanager.utils.Utils
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog

class CreateFiatTransactionActivity : AppCompatActivity(), CreateFiatTransactionContract.View, View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private lateinit var presenter: CreateFiatTransactionContract.Presenter

    val args by lazy { CreateFiatTransactionArgs.deserializeFrom(intent) }

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

    val layoutDate by lazy { findViewById<RelativeLayout>(R.id.layout_date) }

    var chosenDate = Calendar.getInstance().time

    override fun onCreate(savedInstanceState: Bundle?) {

        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.darktheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_fiat_transaction)

        setupToolbar()
        setupFields()

        presenter = CreateFiatTransactionPresenter(CreateFiatTransactionDataManager.getInstance(this), this)
        presenter.attachView()
    }

    override fun showProgressBar() {

    }

    override fun onTransactionComplete() {
        if(args.backpressToPortfolio) {
            loadBaseActivityWithoutRestart()
        } else {
            super.onBackPressed()
        }

//            backToHomeActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
//
//        BaseArgs(2).launch(this)
//        else
//            super.onBackPressed()
    }

    private fun loadBaseActivityWithoutRestart() {

        val bundle = Bundle()
        bundle.putInt("fragment_key", 2)
        val intent = Intent(this, BaseActivity::class.java)
        intent.putExtras(bundle)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)

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
                    val correctQuantity: Float

                    if (radioButtonWithdrawl.isChecked)
                        correctQuantity = (requiredQuantity.text.toString().toFloat() * (-1).toFloat())
                    else
                        correctQuantity = requiredQuantity.text.toString().toFloat()

                    presenter.saveFiatTransaction(requiredExchange.text.trim().toString(), requiredCurrency.text.trim().toString(), correctQuantity, chosenDate, notes.text.trim().toString())
                }
            }
        }
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

    private fun setupFields() {

        requiredCurrency.text = args.currency
        requiredDate.text = Utils.formatDate(chosenDate)

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

    override fun setPresenter(presenter: CreateFiatTransactionContract.Presenter) {
        this.presenter = checkNotNull(presenter)
    }

    companion object {
        val REQUEST_CODE_EXCHANGE = 1
        val REQUEST_CODE_CURRENCY = 2
    }
}