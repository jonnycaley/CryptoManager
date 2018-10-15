package com.jonnycaley.cryptomanager.ui.crypto

import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView

interface CryptoContract {
    interface View : BaseView<Presenter>
    interface Presenter : BasePresenter
}