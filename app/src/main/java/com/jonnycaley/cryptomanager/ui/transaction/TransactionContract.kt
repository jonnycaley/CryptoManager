package com.jonnycaley.cryptomanager.ui.transaction

import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView

class TransactionContract {
    interface View : BaseView<Presenter>
    interface Presenter : BasePresenter
}