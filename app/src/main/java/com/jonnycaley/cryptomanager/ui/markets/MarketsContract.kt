package com.jonnycaley.cryptomanager.ui.markets

import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Currencies
import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView

class MarketsContract {
    interface View : BaseView<Presenter> {
        fun showTop100Changes(currencies: Currencies)
    }

    interface Presenter : BasePresenter
}