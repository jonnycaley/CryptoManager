package com.jonnycaley.cryptomanager.ui.settings

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.Toast
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.Rate
import com.jonnycaley.cryptomanager.ui.base.BaseActivity
import com.jonnycaley.cryptomanager.ui.base.BaseArgs
import com.jonnycaley.cryptomanager.utils.interfaces.TabInterface
import java.util.*
import kotlin.collections.ArrayList

class SettingsFragment : Fragment(), SettingsContract.View, TabInterface {

    lateinit var mView : View

    private lateinit var presenter : SettingsContract.Presenter

    lateinit var settingsAdapter : SettingsAdapter

    val TAG = this.javaClass.simpleName

    val recyclerView by lazy { mView.findViewById<RecyclerView>(R.id.recycler_view) }
    val switchTheme by lazy { mView.findViewById<Switch>(R.id.switch_theme) }

    override fun setPresenter(presenter: SettingsContract.Presenter) {
        this.presenter = checkNotNull(presenter)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
            context?.setTheme(R.style.darktheme)
        else
            context?.setTheme(R.style.AppTheme)

        mView  = inflater.inflate(R.layout.fragment_settings, container, false)
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //view setup should occur here

        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
            switchTheme.isChecked = true

        switchTheme.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            BaseArgs(3).launch(context!! )
        }

        presenter = SettingsPresenter(SettingsDataManager.getInstance(context!!), this)
        presenter.attachView()
    }

    override fun onTabClicked() {
        Log.i(TAG, "onTabClicked()")
    }

    override fun onResume() {
        super.onResume()
        presenter.loadSettings()
    }

    override fun loadSettings(baseFiat: Rate) {

        val settingsList: ArrayList<String> = ArrayList(Arrays.asList("Saved Articles", "Delete All Articles", "Delete Portfolio", "Select Base Currency (${baseFiat.fiat} ${baseFiat.rate})"))

        //settings are loaded here and not in the onViewCreated as the presenter needs to be initialised first
        val mLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = mLayoutManager
        settingsAdapter = SettingsAdapter(settingsList, presenter, context)
        recyclerView.adapter = settingsAdapter

    }

    override fun showPortfolioDeleted() {
        Toast.makeText(context, "Portfolio Deleted", Toast.LENGTH_LONG).show()
    }

    override fun showPortfolioDeletedError() {
        Toast.makeText(context, "An error occurred", Toast.LENGTH_LONG).show()
    }

    override fun showSavedArticlesDeleted() {
        Toast.makeText(context, "Articles Deleted", Toast.LENGTH_LONG).show()
    }

    override fun showSavedArticlesDeletedError() {
        Toast.makeText(context, "An error occurred", Toast.LENGTH_LONG).show()
    }

    companion object {
        val TAG = "SettingsFragment"
    }
}