package com.jonnycaley.cryptomanager.ui.pickers.pair

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.jonnycaley.cryptomanager.R

class PickerPairActivity : AppCompatActivity(), PickerPairContract.View {

    private lateinit var presenter: PickerPairContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picker_pair)

        setupToolbar()

        presenter = PickerPairPresenter(PickerPairDataManager.getInstance(this), this)
        presenter.attachView()
    }


    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "PickerPairActivity"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                super.onBackPressed()
                return false
            }
        }
        return false
    }

    override fun setPresenter(presenter: PickerPairContract.Presenter) {
        this.presenter = checkNotNull(presenter)
    }
}
