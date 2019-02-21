package com.jonnycaley.cryptomanager

import com.jonnycaley.cryptomanager.ui.markets.CurrenciesAdapter
import com.jonnycaley.cryptomanager.utils.Utils
import com.jonnycaley.cryptomanager.utils.Utils.getPriceText
import org.junit.Test

import org.junit.Assert.*
import java.lang.Math.round
import java.math.BigDecimal
import java.text.DecimalFormat

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun percentRound_isCorrect() {
        assertEquals("0.000001", getPriceText((0.00000001).toDouble()))
        assertEquals("0.000001", getPriceText((0.00000010).toDouble()))
        assertEquals("0.000002", getPriceText((0.00000150).toDouble()))
        assertEquals("0.000010", getPriceText((0.00001000).toDouble()))
        assertEquals("0.000100", getPriceText((0.00010000).toDouble()))
        assertEquals("0.001000", getPriceText((0.00100000).toDouble()))
        assertEquals("0.010000", getPriceText((0.01000000).toDouble()))
        assertEquals("0.100000", getPriceText((0.10000000).toDouble()))

        assertEquals("1.00", getPriceText((1.00000000).toDouble()))
        assertEquals("1.01", getPriceText((1.00500000).toDouble()))
        assertEquals("1.10", getPriceText((1.10000000).toDouble()))
        assertEquals("1.11", getPriceText((1.10500000).toDouble()))

        assertEquals("11.00", getPriceText((11.00000000).toDouble()))
        assertEquals("11.10", getPriceText((11.10000000).toDouble()))
        assertEquals("11.11", getPriceText((11.11000000).toDouble()))
        assertEquals("11.12", getPriceText((11.11500000).toDouble()))

        assertEquals("111.11", getPriceText((111.11000000).toDouble()))
        assertEquals("111.12", getPriceText((111.11500000).toDouble()))
        assertEquals("1111.11", getPriceText((1111.1100000).toDouble()))
    }



    @Test
    fun roundToLowest10(){
        assertEquals(3980F, roundToLowest10(3980F))
    }

    private fun roundToLowest10(lowest: Float): Float {
        var lowest10 = ((lowest.toInt()+5)/10)*10; // if num is int
        if(lowest10 > lowest.toInt())
            lowest10 -= 10
        return lowest10.toFloat()
    }

    @Test
    fun roundToHighest(){
        assertEquals(3935F, roundToHighest(3930.86F, 5))
    }

    private fun roundToHighest(highest: Float, multiplesOf: Int): Float {

        var highestInt = round(highest)

        var highest10 = ((highestInt+(multiplesOf/2))/multiplesOf)*multiplesOf


        if(highest10 < highestInt)
            highest10 += multiplesOf

        return highest10.toFloat()
    }


}
