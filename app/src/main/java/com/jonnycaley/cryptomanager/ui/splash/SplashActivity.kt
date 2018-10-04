package com.jonnycaley.cryptomanager.ui.splash

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.ui.base.BaseArgs


class SplashActivity : AppCompatActivity(), SplashContract.View {

    private lateinit var presenter : SplashContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        presenter = SplashPresenter(SplashDataManager.getInstance(this), this)
        presenter.attachView()
    }

    override fun showInternetRequired() {
        Snackbar.make(findViewById(R.id.coordinator), R.string.splash_internet_required, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry) { presenter.getCurrencies() }
                .show()
    }

    override fun toBaseActivity() {
        BaseArgs(0).launch(this)
    }

    override fun setPresenter(presenter: SplashContract.Presenter) {
        this.presenter = checkNotNull(presenter)
    }
}
