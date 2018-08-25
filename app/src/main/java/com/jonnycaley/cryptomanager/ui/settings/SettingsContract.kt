package com.jonnycaley.cryptomanager.ui.settings

import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView

class SettingsContract {
    interface View : BaseView<Presenter>
    interface Presenter : BasePresenter
}