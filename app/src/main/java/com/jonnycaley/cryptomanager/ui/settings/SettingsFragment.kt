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
import java.util.*
import kotlin.collections.ArrayList

class SettingsFragment : Fragment(), SettingsContract.View{

    lateinit var mView : View

    private lateinit var presenter : SettingsContract.Presenter

    lateinit var settingsAdapter : SettingsAdapter

    val TAG = this.javaClass.simpleName

    var settingsList: ArrayList<String> = ArrayList(Arrays.asList("Saved Articles"))

    val recyclerView by lazy { mView!!.findViewById<RecyclerView>(R.id.recycler_view) }

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

        val mLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = mLayoutManager
        settingsAdapter = SettingsAdapter(settingsList, context)
        recyclerView.adapter = settingsAdapter

        presenter = SettingsPresenter(SettingsDataManager.getInstance(context!!), this)
        presenter.attachView()
    }
}