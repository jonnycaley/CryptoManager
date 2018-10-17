package com.jonnycaley.cryptomanager.ui.settings

import com.jonnycaley.cryptomanager.utils.mvp.BasePresenter
import com.jonnycaley.cryptomanager.utils.mvp.BaseView

interface SettingsContract {
    interface View : BaseView<Presenter> {
        fun showPortfolioDeleted()
        fun showPortfolioDeletedError()
        fun showSavedArticlesDeleted()
        fun showSavedArticlesDeletedError()
        fun loadSettings()
    }

    interface Presenter : BasePresenter {
        fun deletePortfolio()
        fun deleteSavedArticles()
    }
}