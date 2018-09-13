package com.jonnycaley.cryptomanager.ui.splash

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.ui.base.BaseActivity
import com.jonnycaley.cryptomanager.ui.base.BaseArgs
import io.paperdb.Paper

class SplashActivity : AppCompatActivity(), SplashContract.View {

    private lateinit var presenter : SplashContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Paper.init(this)

        presenter = SplashPresenter(SplashDataManager.getInstance(this), this)
        presenter.attachView()
    }

    override fun toBaseActivity() {
        BaseArgs(0).launch(this)
    }

    override fun setPresenter(presenter: SplashContract.Presenter) {
        this.presenter = checkNotNull(presenter)
    }
}
