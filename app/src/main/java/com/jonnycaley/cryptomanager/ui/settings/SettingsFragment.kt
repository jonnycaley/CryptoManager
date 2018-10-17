package com.jonnycaley.cryptomanager.ui.settings

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.utils.interfaces.TabInterface
import java.util.*
import kotlin.collections.ArrayList

class SettingsFragment : Fragment(), SettingsContract.View, TabInterface {

    lateinit var mView : View

    private lateinit var presenter : SettingsContract.Presenter

    lateinit var settingsAdapter : SettingsAdapter

    val TAG = this.javaClass.simpleName

    var settingsList: ArrayList<String> = ArrayList(Arrays.asList("Saved Articles", "Delete All Articles", "Delete Portfolio"))

    val recyclerView by lazy { mView.findViewById<RecyclerView>(R.id.recycler_view) }

    override fun setPresenter(presenter: SettingsContract.Presenter) {
        this.presenter = checkNotNull(presenter)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView  = inflater.inflate(R.layout.fragment_settings, container, false)
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //view setup should occur here

        presenter = SettingsPresenter(SettingsDataManager.getInstance(context!!), this)
        presenter.attachView()
    }

    override fun onTabClicked() {
        Log.i(TAG, "onTabClicked()")
    }

    override fun loadSettings() {
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