package com.jonnycaley.cryptomanager.ui.settings

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.Toast
import com.jonnycaley.cryptomanager.BuildConfig
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.Rate
import com.jonnycaley.cryptomanager.ui.base.BaseArgs
import com.jonnycaley.cryptomanager.utils.Utils
import com.jonnycaley.cryptomanager.utils.interfaces.TabInterface
import java.util.*
import kotlin.collections.ArrayList
import android.graphics.Typeface
import android.widget.RelativeLayout
import android.widget.TextView
import com.thefinestartist.Base.getAssets

class SettingsFragment : androidx.fragment.app.Fragment(), SettingsContract.View, TabInterface {

    lateinit var mView : View

    private lateinit var presenter : SettingsContract.Presenter

    lateinit var settingsAdapterGeneral : SettingsAdapter
    lateinit var settingsAdapterAbout : SettingsAdapter
    lateinit var settingsAdapterData : SettingsAdapter

    val TAG = this.javaClass.simpleName

    val recyclerViewGeneral by lazy { mView.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recycler_view_general) }
    val recyclerViewData by lazy { mView.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recycler_view_data) }
    val recyclerViewAbout by lazy { mView.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recycler_view_about) }

    val layoutProgressBar by lazy { mView.findViewById<RelativeLayout>(R.id.progress_bar_layout) }

    val textGeneral by lazy { mView.findViewById<TextView>(R.id.text_general) }
    val textData by lazy { mView.findViewById<TextView>(R.id.text_data) }
    val textAbout by lazy { mView.findViewById<TextView>(R.id.text_about) }

    val switchTheme by lazy { mView.findViewById<Switch>(R.id.switch_theme) }

    override fun setPresenter(presenter: SettingsContract.Presenter) {
        this.presenter = checkNotNull(presenter)
    }

    override fun hideProgressLayout() {
        layoutProgressBar.visibility = View.GONE
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if(Utils.isDarkTheme())
            context?.setTheme(R.style.darktheme)
        else
            context?.setTheme(R.style.AppTheme)

        mView  = inflater.inflate(R.layout.fragment_settings, container, false) //inflate layout
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //view setup should occur here

        if(Utils.isDarkTheme()) {
            switchTheme.isChecked = true //switch theme
        }

        val custom_font = Typeface.createFromAsset(context?.applicationContext?.assets, "fonts/Roboto-Bold.ttf") //init fonts

        textGeneral.typeface = custom_font //change font
        textData.typeface = custom_font //change font
        textAbout.typeface = custom_font //change font

        switchTheme.setOnCheckedChangeListener { buttonView, isChecked -> // switch theme
            if(isChecked){
                Utils.setDarkMode() //set theme
            } else {
                Utils.setLightMode() //set theme
            }
            presenter.saveThemePreference(isChecked)
        }

        presenter = SettingsPresenter(SettingsDataManager.getInstance(context!!), this) //attach presenter
        presenter.attachView()
    }

    /*
    Function updates the theme
    */
    override fun updateTheme() {
        context?.let { Utils.vibrate(it) }

        BaseArgs(3).launch(context!!)
    }

    /*
    Function vibrates the phone
    */
    fun vibratePhone() {
        val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(200)
        }
    }

    override fun onTabClicked(isTabAlreadyClicked: Boolean) {
        Log.i(TAG, "onTabClicked()")
    }

    override fun onResume() {
        super.onResume()
        presenter.loadSettings()
    }

    /*
    Function loads the settings
    */
    override fun loadSettings(baseFiat: Rate) {

        val versionName = BuildConfig.VERSION_NAME

        val settingsListGeneral: ArrayList<String> = ArrayList(Arrays.asList(resources.getString(R.string.settings_select_base_currency)))
        val settingsListData: ArrayList<String> = ArrayList(Arrays.asList(resources.getString(R.string.settings_saved_articles), resources.getString(R.string.settings_transaction_history), resources.getString(R.string.settings_delete_all_articles), resources.getString(R.string.settings_delete_portfolio)))
        val settingsListAbout: ArrayList<String> = ArrayList(Arrays.asList(resources.getString(R.string.settings_review_app), resources.getString(R.string.settings_share_app), resources.getString(R.string.settings_send_feedback) , resources.getString(R.string.settings_version)))

        //settings are loaded here and not in the onViewCreated as the presenter needs to be initialised first
        val mLayoutManagerAbout = androidx.recyclerview.widget.LinearLayoutManager(context)
        val mLayoutManagerData = androidx.recyclerview.widget.LinearLayoutManager(context)
        val mLayoutManagerGeneral = androidx.recyclerview.widget.LinearLayoutManager(context)

        recyclerViewAbout.layoutManager = mLayoutManagerAbout //init recyclerview
        recyclerViewData.layoutManager = mLayoutManagerData //init recyclerview
        recyclerViewGeneral.layoutManager = mLayoutManagerGeneral //init recyclerview

        settingsAdapterGeneral = SettingsAdapter(settingsListGeneral, baseFiat.fiat, baseFiat.rate, versionName, presenter, context) //attach recyclerview
        recyclerViewGeneral.adapter = settingsAdapterGeneral

        settingsAdapterData = SettingsAdapter(settingsListData, baseFiat.fiat, baseFiat.rate, versionName, presenter, context) //attach recyclerview
        recyclerViewData.adapter = settingsAdapterData

        settingsAdapterAbout = SettingsAdapter(settingsListAbout, baseFiat.fiat, baseFiat.rate, versionName, presenter, context) //attach recyclerview
        recyclerViewAbout.adapter = settingsAdapterAbout

    }

    override fun showPortfolioDeleted() {
        Toast.makeText(context, "Portfolio Deleted", Toast.LENGTH_SHORT).show()
    }

    override fun showPortfolioDeletedError() {
        Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show()
    }

    override fun showSavedArticlesDeleted() {
        Toast.makeText(context, "Articles Deleted", Toast.LENGTH_SHORT).show()
    }

    override fun showSavedArticlesDeletedError() {
        Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show()
    }

    companion object {
        val TAG = "SettingsFragment"
    }
}