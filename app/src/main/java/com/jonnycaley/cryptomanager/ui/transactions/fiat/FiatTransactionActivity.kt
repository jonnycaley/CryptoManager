package com.jonnycaley.cryptomanager.ui.transactions.fiat

import android.app.Activity
import android.app.AlertDialog
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
import com.jonnycaley.cryptomanager.utils.Utils
import kotlinx.android.synthetic.main.activity_update_fiat_transaction.*

class FiatTransactionActivity : AppCompatActivity(), FiatTransactionContract.View, View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private lateinit var presenter: FiatTransactionContract.Presenter

    val args by lazy { FiatTransactionArgs.deserializeFrom(intent) }

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

        fillFields()
        setupUpdate()
        setupCreate()

        Log.i(TAG + "Seeee", args.backpressToPortfolio.toString())

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
            requiredDate.text = formatDate(transactionDate)
            textViewSubmit.text = "Create"
            requiredCurrency.text = currency
        }
    }

    private fun setupUpdate() {

        args.transaction?.let { transaction ->
            if(transaction.quantity > 0.toBigDecimal() ){
                radioButtonDeposit.isChecked = true
            } else {
                radioButtonWithdrawl.isChecked = true
            }
            buttonDelete.visibility = View.VISIBLE

            layoutExchangeEmpty.visibility = View.GONE
            layoutExchangeFilled.visibility = View.VISIBLE

            layoutCurrencyEmpty.visibility = View.GONE
            layoutCurrencyFilled.visibility = View.VISIBLE

            requiredExchange.text = transaction.exchange
            requiredCurrency.text = transaction.symbol
            requiredQuantity.setText(String.format(transaction.quantity.abs().toString()))
            requiredDate.text = formatDate(transaction.date)
            buttonDelete.visibility = View.VISIBLE
            setupToolbarUpdate(transaction)
        }
    }

    private fun fillFields() {

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
                val diaBox = AskOption()
                diaBox.show()
//                args.transaction?.let { presenter.deleteTransaction(it) }
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
                    val correctQuantity: Float

                    if (radioButtonWithdrawl.isChecked)
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
        }
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

