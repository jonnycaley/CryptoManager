package com.jonnycaley.cryptomanager.ui.markets

import android.widget.SearchView
import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Currency
import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.News
import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView

interface MarketsContract {
    interface View : BaseView<Presenter> {
        fun showTop100Changes(currencies: List<Currency>?)
        fun showLatestArticles(it: Array<News>)
        fun getCurrencySearchView(): SearchView
    }

    interface Presenter : BasePresenter
}