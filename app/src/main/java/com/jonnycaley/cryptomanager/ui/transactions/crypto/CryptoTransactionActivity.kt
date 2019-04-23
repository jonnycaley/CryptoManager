package com.jonnycaley.cryptomanager.ui.transactions.crypto

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.DataBase.NotTransaction
import com.jonnycaley.cryptomanager.data.model.DataBase.Transaction
import com.jonnycaley.cryptomanager.ui.base.BaseActivity
import com.jonnycaley.cryptomanager.ui.pickers.exchange.PickerExchangeActivity
import com.jonnycaley.cryptomanager.ui.pickers.pair.PickerPairActivity
import com.jonnycaley.cryptomanager.utils.CircleTransform
import com.jonnycaley.cryptomanager.utils.Utils
import com.r0adkll.slidr.Slidr
import com.rengwuxian.materialedittext.MaterialEditText
import com.squareup.picasso.Picasso
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.activity_crypto_transaction.*
import java.math.BigDecimal
import java.util.*

class CryptoTransactionActivity : AppCompatActivity(), CryptoTransactionContract.View, View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private lateinit var presenter: CryptoTransactionContract.Presenter

    val args by lazy { CryptoTransactionArgs.deserializeFrom(intent) }

    val layout by lazy { findViewById<RelativeLayout>(R.id.layout) }

    val buttonDelete by lazy { findViewById<ImageView>(R.id.button_delete) }

    val arrowExchange by lazy { findViewById<ImageView>(R.id.arrow_exchange) }
    val arrowTradingPair by lazy { findViewById<ImageView>(R.id.arrow_trading_pair) }

    val layoutSell by lazy { findViewById<LinearLayout>(R.id.layout_sell_checked) }
    val layoutBuy by lazy { findViewById<LinearLayout>(R.id.layout_buy_checked) }

    val buttonCreateBuy by lazy { findViewById<TextView>(R.id.button_create_buy) }
    val buttonCreateSell by lazy { findViewById<TextView>(R.id.button_create_sell) }
    val buttonUpdateBuy by lazy { findViewById<TextView>(R.id.button_update_buy) }
    val buttonUpdateSell by lazy { findViewById<TextView>(R.id.button_update_sell) }

    val textviewBuy by lazy { findViewById<TextView>(R.id.text_view_buy) }
    val textviewSell by lazy { findViewById<TextView>(R.id.text_view_sell) }

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

    val switchDeduct by lazy { findViewById<Switch>(R.id.switch_deduct) }

    val edittextNotes by lazy { findViewById<MaterialEditText>(R.id.edit_text_notes) }

    val toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
    val title by lazy { findViewById<TextView>(R.id.title) }

    var transactionDate = Calendar.getInstance().time

    var isUpdateTransaction = false
    var isDateChanged = false
    var backPressToPortfolio = false

    override fun onCreate(savedInstanceState: Bundle?) {

        if(Utils.isDarkTheme()) {
            setTheme(R.style.darktheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crypto_transaction)

        Slidr.attach(this)

        if(!Utils.isDarkTheme()) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)  //set icon black
            buttonDelete.setImageResource(R.drawable.baseline_delete_black_24)  //set icon black
            arrowExchange.setImageResource(R.drawable.next_white)  //set icon white
            arrowTradingPair.setImageResource(R.drawable.next_white)  //set icon white
        }

        buttonCreateBuy.visibility = View.GONE //hide view
        buttonCreateSell.visibility = View.GONE //hide view
        buttonUpdateBuy.visibility = View.GONE //hide view
        buttonUpdateSell.visibility = View.GONE //hide view

        args.transaction?.let { transaction ->

            isUpdateTransaction = true
            setupUpdate(transaction)
            setupToolbarUpdate(transaction) //set up toolbar update
            setupHeaderUpdate(transaction) //set up header update
            requiredDate.text = transaction.date.let { Utils.formatDate(it) }
        }

        Utils.hideKeyboardFromActivity(this)

        args.notTransactions?.let { transaction ->
            backPressToPortfolio = transaction.backpressToPortfolio

            setupCreate(transaction)
            setupToolbarCreate(transaction) //set up toolbar create
            setupHeaderCreate(transaction) //set up header create
            requiredDate.text = Utils.formatDate(transactionDate)
        }

        setupBody()

        presenter = CryptoTransactionPresenter(CryptoTransactionDataManager.getInstance(this), this)
        presenter.attachView()
    }


    /*
    Function sets up headers update
    */
    private fun setupHeaderUpdate(transaction: Transaction) {

        Picasso.with(this)
                .load(transaction.baseImageUrl) //load image url
                .fit()
                .centerCrop()
                .transform(CircleTransform())
                .into(headerImage)
        headerName.text = transaction.symbol

    }

    /*
    Function sets up headers create
    */
    private fun setupHeaderCreate(transaction: NotTransaction) {

        Picasso.with(this)
                .load(transaction.baseUrl + transaction.currency.imageUrl)
                .fit()
                .centerCrop()
                .transform(CircleTransform())
                .into(headerImage)
        headerName.text = transaction.currency.symbol

    }

    /*
    Function sets up create
    */
    private fun setupCreate(transaction: NotTransaction) {
        buttonDelete.visibility = View.GONE //change view visibility
        showCreateBuy()
    }


    private fun hideSellLayout() {
        layoutSell.visibility = View.GONE //change view visibility
    }

    private fun showBuyLayout() {
        layoutBuy.visibility = View.VISIBLE //change view visibility
    }

    private fun hideBuyLayout() {
        layoutBuy.visibility = View.GONE //change view visibility
    }

    private fun showSellLayout() {
        layoutSell.visibility = View.VISIBLE //change view visibility
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

    override fun getName(): String {
        args.transaction?.let { return it.name }
        args.notTransactions?.let { return it.currency.coinName ?: it.currency.fullName ?: "" }
        return ""
    }

    private fun setupBody() {

        requiredPrice.setOnFocusChangeListener { v, hasFocus ->
            println(hasFocus)
            if(!hasFocus) {
                val view = this.currentFocus
                view?.let { v ->
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.let { it.hideSoftInputFromWindow(v.windowToken, 0) }
                }
            }
        }

        layoutPrice.setOnClickListener(this)
        layoutQuantity.setOnClickListener(this)
        layoutDate.setOnClickListener(this)
        layoutEmptyPair.setOnClickListener(this)
        layoutFilledPair.setOnClickListener(this)
        layoutEmptyExchange.setOnClickListener(this)
        layoutFilledExchange.setOnClickListener(this)
        textviewBuy.setOnClickListener(this)
        textviewSell.setOnClickListener(this)
        textviewSell.setOnClickListener(this)
        textSellAll.setOnClickListener(this)
        buttonDelete.setOnClickListener(this)

        buttonCreateBuy.setOnClickListener(this)
        buttonCreateSell.setOnClickListener(this)
        buttonUpdateBuy.setOnClickListener(this)
        buttonUpdateSell.setOnClickListener(this)
    }

    private fun setupToolbarUpdate(transaction: Transaction) {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        title.text = "Edit ${transaction.name} Transaction"
    }

    private fun setupToolbarCreate(transaction: NotTransaction) {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title.text = "${transaction.currency.coinName} - ${transaction.currency.symbol}"
    }

    private fun setupUpdate(transaction: Transaction) {
        requiredPair.text = transaction.symbol + "/" + transaction.pairSymbol
        textPrice.text = "Price in ${transaction.pairSymbol}"
        if (transaction.quantity > 0.toBigDecimal()) {
            textDeduction.text = "Deduct from ${getPairText()}holdings"
            showUpdateBuy()
            hideUpdateSell()
            showBuyLayout()
            hideSellLayout()
        }
        else {
            textDeduction.text = "Add to ${getPairText()}holdings"
            showUpdateSell()
            hideUpdateBuy()
            showSellLayout()
            hideBuyLayout()
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
                    focusEdittext(requiredPrice)
                    //TODO: RE ADD FOCUS TO EDITTEXT WITH NUMERICAL KEYBOARD?
                }
                layoutQuantity.id -> {
                    focusEdittext(requiredQuantity)
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
                textviewBuy.id -> {
                    textDeduction.text = "Deduct from ${getPairText()}holdings"
                    textSellAll.visibility = View.GONE

                    if(isUpdateTransaction) {
                        showUpdateBuy()
                        hideUpdateSell()
                    }
                    else {
                        showCreateBuy()
                        hideCreateSell()
                    }
                    showBuyLayout()
                    hideSellLayout()
                }
                textviewSell.id -> {
                    textDeduction.text = "Add to ${getPairText()}holdings"
                    textSellAll.visibility = View.VISIBLE
                    if(isUpdateTransaction) {
                        showUpdateSell()
                        hideUpdateBuy()
                    }
                    else {
                        showCreateSell()
                        hideCreateBuy()
                    }
                    showSellLayout()
                    hideBuyLayout()
                }
                textSellAll.id -> {
                    presenter.getAllHoldings(getSymbol())
                }
                buttonCreateBuy.id -> {
                    startProcess(true)
                }
                buttonCreateSell.id -> {
                    startProcess(false)
                }
                buttonUpdateBuy.id -> {
                    startProcess(true)
                }
                buttonUpdateSell.id -> {
                    startProcess(false)
                }
            }
    }

    private fun hideCreateBuy() {
        buttonCreateBuy.visibility = View.GONE
    }

    private fun showCreateSell() {
        buttonCreateSell.visibility = View.VISIBLE
    }

    private fun hideUpdateBuy() {
        buttonUpdateBuy.visibility = View.GONE
    }

    private fun showUpdateSell() {
        buttonUpdateSell.visibility = View.VISIBLE
    }

    private fun hideCreateSell() {
        buttonCreateSell.visibility = View.GONE
    }

    private fun showCreateBuy() {
        buttonCreateBuy.visibility = View.VISIBLE
    }

    private fun hideUpdateSell() {
        buttonUpdateSell.visibility = View.GONE
    }

    private fun showUpdateBuy() {
        buttonUpdateBuy.visibility = View.VISIBLE
    }

    override fun startUpdateBuyProgress() {
        buttonUpdateBuy.visibility = View.GONE
    }

    override fun startUpdateSellProgress() {
        buttonUpdateSell.visibility = View.GONE
    }

    override fun stopUpdateBuyProgress() {
        buttonCreateBuy.visibility = View.VISIBLE
    }

    override fun stopUpdateSellProgress() {
        buttonCreateBuy.visibility = View.VISIBLE
    }

    override fun startCreateBuyProgress() {
        buttonCreateBuy.visibility = View.GONE
    }

    override fun startCreateSellProgress() {
        buttonCreateSell.visibility = View.GONE
    }

    override fun stopCreateBuyProgress() {
        buttonCreateBuy.visibility = View.VISIBLE
    }

    override fun stopCreateSellProgress() {
        buttonCreateBuy.visibility = View.VISIBLE
    }

    private fun startProcess(bool : Boolean) {
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
                presenter.updateCryptoTransaction(transaction, bool, requiredExchange.text.trim().toString(), requiredPair.text.trim().substring(requiredPair.text.indexOf("/")+1), java.lang.Float.parseFloat(requiredPrice.text?.trim().toString()), java.lang.Float.parseFloat(requiredQuantity.text?.trim().toString()), tempDate, switchDeduct.isChecked, edittextNotes.text?.trim().toString())
            }
            if (!isUpdateTransaction) {
                presenter.createCryptoTransaction(bool, requiredExchange.text.trim().toString(), requiredPair.text.trim().substring(requiredPair.text.indexOf("/") + 1), java.lang.Float.parseFloat(requiredPrice.text?.trim().toString()), java.lang.Float.parseFloat(requiredQuantity.text?.trim().toString()), transactionDate, switchDeduct.isChecked, edittextNotes.text?.trim().toString())
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
        if (layoutBuy.visibility == View.VISIBLE)
            textDeduction.text = "Deduct from $pair holdings"
        else
            textDeduction.text = "Add to $pair holdings"
    }

    private fun showPair(pair: String?) {
        layoutEmptyPair.visibility = View.GONE
        layoutFilledPair.visibility = View.VISIBLE
        if(args.transaction?.symbol != null)
            requiredPair.text = args.transaction?.symbol + "/" + pair
        else
            requiredPair.text = args.notTransactions?.currency?.symbol + "/" + pair
    }

    fun focusEdittext(editText: EditText){
        editText.requestFocus()
        val imm : InputMethodManager =  getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
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
