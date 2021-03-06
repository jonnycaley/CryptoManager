package com.jonnycaley.cryptomanager.ui.splash

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.ui.base.BaseArgs
import com.jonnycaley.cryptomanager.utils.Utils

class SplashActivity : AppCompatActivity(), SplashContract.View {

    private lateinit var presenter : SplashContract.Presenter

    val coordinatorLayout by lazy { findViewById<RelativeLayout>(R.id.coordinator) }
    val progressBar by lazy { findViewById<ProgressBar>(R.id.progress_bar) }

    override fun onCreate(savedInstanceState: Bundle?) {

        if(Utils.isDarkTheme())

        if(Utils.isDarkTheme()) { //if dark theme
            setTheme(R.style.darktheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        presenter = SplashPresenter(SplashDataManager.getInstance(this), this)
        presenter.attachView()
    }

    /*
    Function shows internet required snackbar
    */
    override fun showInternetRequired() {
        showSnackBar(resources.getString(R.string.internet_required))
    }

    /*
    Function error snackbar
    */
    override fun showError() {
        showSnackBar(resources.getString(R.string.error_occurred))
    }

    /*
    Function shows snackbar
    */
    fun showSnackBar(message: String) {
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry) { presenter.getCurrencies() }
                .show()
    }

    /*
    Function sets dark theme
    */
    override fun setDarkTheme() {
        Utils.setDarkMode()
        setTheme(R.style.darktheme)
        coordinatorLayout.setBackgroundColor(resources.getColor(R.color.black))
    }

    /*
    Function hides progress bar
    */
    override fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }

    override fun showUsingStorage() {
//        Toast.makeText(this, R.string.using_offline_data, Toast.LENGTH_SHORT).show()
    }

    override fun toBaseActivity() {
        BaseArgs(0).launch(this)
    }

    override fun setPresenter(presenter: SplashContract.Presenter) {
        this.presenter = checkNotNull(presenter)
    }
}
