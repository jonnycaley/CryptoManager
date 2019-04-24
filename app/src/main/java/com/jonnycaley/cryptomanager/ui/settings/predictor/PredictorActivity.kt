package com.jonnycaley.cryptomanager.ui.settings.predictor

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.gson.Gson
import com.jonnycaley.cryptomanager.R
import com.jonnycaley.cryptomanager.data.model.Predictor.Model
import com.jonnycaley.cryptomanager.utils.Utils
import com.r0adkll.slidr.Slidr
import kotlinx.android.synthetic.main.activity_predictor.*

class PredictorActivity : AppCompatActivity() {


    lateinit var predictorAdapter : PredictorAdapter

    val recyclerView by lazy { findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recycler_view) }

    override fun onCreate(savedInstanceState: Bundle?) {

        if(Utils.isDarkTheme()) {
            setTheme(R.style.darktheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_predictor)

        if(!Utils.isDarkTheme()) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
        }

        Slidr.attach(this)

        setupToolbar() //set up toolbar

        loadJsonModel()
    }

    private fun loadJsonModel() {
        val json = resources.getString(R.string.model_json)

        val gson = Gson().fromJson(json, Model::class.java)

        val mLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        recyclerView.layoutManager = mLayoutManager
        predictorAdapter = gson.config?.layers?.let { PredictorAdapter(it, this) }!!
        recyclerView.adapter = predictorAdapter
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.title = ""
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
}