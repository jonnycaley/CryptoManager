package com.jonnycaley.cryptomanager.ui.settings

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter

class SettingsFragment : Fragment(), SettingsContract.View{

    lateinit var mView : View

    var listener: mListener? = null

    lateinit var presenter : BasePresenter

    override fun setPresenter(presenter: SettingsContract.Presenter) {
        this.presenter = checkNotNull(presenter)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is mListener) {
            this.listener = context
        } else {
            println("Must implement listener")
        }
        println("onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //data initialization that doesnt require activity
//        this.headerStr = arguments?.getString("headerStr").toString()
        println("onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView  = inflater.inflate(R.layout.fragment_settings, container, false)
        println("onCreateView")
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { //set all of the saved data from the onCreate attachview
        super.onViewCreated(view, savedInstanceState)
        //view setup should occur here

        presenter = SettingsPresenter(SettingsDataManager.getInstance(context!!), this)
        presenter.attachView()
        //get all the info for when the onViewCreated runs...
        println("onViewCreated")
    }

    override fun onDetach() {
        super.onDetach()
        this.listener = null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //once things in activity have been created
        //accessing the views of the activity done here
        //safe to search for activity objects
        println("onActivityCreated")
    }



    fun whenStuffHappensUpdateActivity(){
        listener?.onUserNotDefaulted()
    }


    //SETUP

    fun newInstance(headerStr: String): SettingsFragment {
        val fragmentDemo = SettingsFragment()
        val args = Bundle()
        args.putString("headerStr", headerStr)
        fragmentDemo.arguments = args
        return fragmentDemo
    }

    interface mListener{
        fun onUserDefaulted()
        fun onUserNotDefaulted()
    } //this interface is for when the activity needs to react to a fragment change
}