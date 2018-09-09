package com.jonnycaley.cryptomanager.ui.transactions.fiatTransaction

import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView

interface FiatTransactionContract {
    interface View : BaseView<Presenter>
    interface Presenter : BasePresenter
}