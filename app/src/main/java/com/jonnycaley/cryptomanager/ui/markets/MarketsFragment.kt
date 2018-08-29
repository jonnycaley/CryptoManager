package com.jonnycaley.cryptomanager.ui.portfolio

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Currencies
import com.jonnycaley.cryptomanager.ui.markets.MarketsContract
import com.jonnycaley.cryptomanager.ui.markets.MarketsDataManager
import com.jonnycaley.cryptomanager.ui.markets.MarketsPresenter
import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter

class MarketsFragment : Fragment(), MarketsContract.View{

    lateinit var mView : View

    lateinit var presenter : BasePresenter

    override fun setPresenter(presenter: MarketsContract.Presenter) {
        this.presenter = checkNotNull(presenter)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView  = inflater.inflate(R.layout.fragment_markets, container, false)
        println("onCreateView")
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { //set all of the saved data from the onCreate attachview
        super.onViewCreated(view, savedInstanceState)

        presenter = MarketsPresenter(MarketsDataManager.getInstance(context!!), this)
        presenter.attachView()
    }

    override fun showTop100Changes(currencies: Currencies) {

    }
}