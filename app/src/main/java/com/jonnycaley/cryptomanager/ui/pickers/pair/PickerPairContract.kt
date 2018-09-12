package com.jonnycaley.cryptomanager.ui.pickers.pair

import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView

interface PickerPairContract {
    interface View : BaseView<Presenter>
    interface Presenter : BasePresenter
}