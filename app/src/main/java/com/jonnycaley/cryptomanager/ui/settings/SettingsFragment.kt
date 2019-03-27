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

class SettingsFragment : androidx.fragment.app.Fragment(), SettingsContract.View, TabInterface {

    lateinit var mView : View

    private lateinit var presenter : SettingsContract.Presenter

    lateinit var settingsAdapter : SettingsAdapter

    val TAG = this.javaClass.simpleName

    val recyclerView by lazy { mView.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recycler_view) }
    val switchTheme by lazy { mView.findViewById<Switch>(R.id.switch_theme) }

    override fun setPresenter(presenter: SettingsContract.Presenter) {
        this.presenter = checkNotNull(presenter)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if(Utils.isDarkTheme())
            context?.setTheme(R.style.darktheme)
        else
            context?.setTheme(R.style.AppTheme)

        mView  = inflater.inflate(R.layout.fragment_settings, container, false)
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //view setup should occur here

        if(Utils.isDarkTheme()) {
            switchTheme.isChecked = true
        }

        switchTheme.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                Utils.setDarkMode()
            } else {
                Utils.setLightMode()
            }
            presenter.saveThemePreference(isChecked)
        }

        presenter = SettingsPresenter(SettingsDataManager.getInstance(context!!), this)
        presenter.attachView()
    }

    override fun updateTheme() {
        context?.let { Utils.vibrate(it) }

        BaseArgs(3).launch(context!!)

        //TODO: RESTART ACTIVITY OF RE-LOAD ALL VIEWS IN FRAGMENT? :S
//        (activity as BaseActivity).updateThemeChanged()
//        presenter.loadSettings()
    }

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

    override fun loadSettings(baseFiat: Rate) {

        val versionName = BuildConfig.VERSION_NAME

        val settingsList: ArrayList<String> = ArrayList(Arrays.asList("Saved Articles", "Delete All Articles", "Delete Portfolio", "Select Base Currency (${baseFiat.fiat} ${baseFiat.rate})", "Send Feedback", "Full transaction history", "Share this app", "Review this app", "Version $versionName"))

        //settings are loaded here and not in the onViewCreated as the presenter needs to be initialised first
        val mLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        recyclerView.layoutManager = mLayoutManager
        settingsAdapter = SettingsAdapter(settingsList, presenter, context)
        recyclerView.adapter = settingsAdapter

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