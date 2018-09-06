package com.jonnycaley.cryptomanager.ui.splash

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.ui.base.BaseActivity

class SplashActivity : AppCompatActivity(), SplashContract.View{

    private lateinit var presenter : SplashContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        presenter = SplashPresenter(SplashDataManager.getInstance(this), this)
        presenter.attachView()
    }

    override fun toBaseActivity() {
        startActivity(Intent(this, BaseActivity::class.java))
    }

    override fun setPresenter(presenter: SplashContract.Presenter) {
        this.presenter = checkNotNull(presenter)
    }
}
