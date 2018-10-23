package com.jonnycaley.cryptomanager.utils

import android.app.Application
import com.pacoworks.rxpaper2.RxPaperBook
import io.paperdb.Paper

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        RxPaperBook.init(this)
    }
}