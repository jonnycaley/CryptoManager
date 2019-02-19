package com.jonnycaley.cryptomanager.ui.markets

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MarketsPresenterTest {

    @Test
    fun getOfflineData() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("com.jonnycaley.cryptomanager", appContext.packageName)
    }

}