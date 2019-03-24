package com.jonnycaley.cryptomanager.ui.search

import com.jonnycaley.cryptomanager.data.model.CryptoCompare.AllCurrencies.Datum
import com.jonnycaley.cryptomanager.data.model.ExchangeRates.Rate
import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView

interface SearchContract {
    interface View : BaseView<Presenter> {
        fun showCurrencies(currencies: List<Datum>?, baseImageUrl: String?, baseLinkUrl: String?)
        fun getSearchType() : String?
        fun showFiats(currencies: List<Rate>, withUsdTop: Boolean)
        fun showProgressLayout()
        fun hideProgressLayout()
        fun showError()
        fun showSearchBar()
    }

    interface Presenter : BasePresenter {
        fun showCurrencies(filter: String?)
        fun showFiats(filter: String?)
        fun getAllFiats()
        fun getAllCurrencies()
    }
}