package com.jonnycaley.cryptomanager.ui.portfolio

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.ui.base.BaseActivity
import com.jonnycaley.cryptomanager.ui.search.SearchActivity

class PortfolioFragment : Fragment(), PortfolioContract.View, View.OnClickListener {

    lateinit var mView: View

    private lateinit var presenter: PortfolioContract.Presenter

    val buttonGetStarted by lazy { mView.findViewById<Button>(R.id.button_get_started) }

    override fun setPresenter(presenter: PortfolioContract.Presenter) {
        this.presenter = checkNotNull(presenter)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_portfolio, container, false)
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { //set all of the saved data from the onCreate attachview
        super.onViewCreated(view, savedInstanceState)
        //view setup should occur here

        buttonGetStarted.setOnClickListener(this)

        presenter = PortfolioPresenter(PortfolioDataManager.getInstance(context!!), this)
        presenter.attachView()
    }

    override fun onClick(v: View?) {
        when(v?.id){
            buttonGetStarted.id -> {
                startActivity(Intent(context, SearchActivity::class.java))
            }
        }
    }
}