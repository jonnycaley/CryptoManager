package com.jonnycaley.cryptomanager

import com.jonnycaley.cryptomanager.utils.Utils
import com.jonnycaley.cryptomanager.utils.Utils.getPriceTextAbs
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
        assertEquals("0.000001", getPriceTextAbs((0.00000001).toDouble()))
        assertEquals("0.000001", getPriceTextAbs((0.00000010).toDouble()))
        assertEquals("0.000002", getPriceTextAbs((0.00000150).toDouble()))
        assertEquals("0.000010", getPriceTextAbs((0.00001000).toDouble()))
        assertEquals("0.000100", getPriceTextAbs((0.00010000).toDouble()))
        assertEquals("0.001000", getPriceTextAbs((0.00100000).toDouble()))
        assertEquals("0.010000", getPriceTextAbs((0.01000000).toDouble()))
        assertEquals("0.100000", getPriceTextAbs((0.10000000).toDouble()))

        assertEquals("1.00", getPriceTextAbs((1.00000000).toDouble()))
        assertEquals("1.01", getPriceTextAbs((1.00500000).toDouble()))
        assertEquals("1.10", getPriceTextAbs((1.10000000).toDouble()))
        assertEquals("1.11", getPriceTextAbs((1.10500000).toDouble()))

        assertEquals("11.00", getPriceTextAbs((11.00000000).toDouble()))
        assertEquals("11.10", getPriceTextAbs((11.10000000).toDouble()))
        assertEquals("11.11", getPriceTextAbs((11.11000000).toDouble()))
        assertEquals("11.12", getPriceTextAbs((11.11500000).toDouble()))

        assertEquals("111.11", getPriceTextAbs((111.11000000).toDouble()))
        assertEquals("111.12", getPriceTextAbs((111.11500000).toDouble()))
        assertEquals("1111.11", getPriceTextAbs((1111.1100000).toDouble()))
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






    @Test
    fun formatPrice(){
        assertEquals("$0", formatPrice(0.toBigDecimal(), "$"))
        assertEquals("$0.000001", formatPrice(0.00000001.toBigDecimal(), "$"))
        assertEquals("$0.000001", formatPrice(0.0000001.toBigDecimal(), "$"))
        assertEquals("$0.000001", formatPrice(0.000001.toBigDecimal(), "$"))
        assertEquals("$0.000010", formatPrice(0.000010.toBigDecimal(), "$"))
    }



    fun formatPrice(priceAsDouble: BigDecimal, symbol : String): String {

        println(priceAsDouble.toPlainString())

        if(priceAsDouble == 0.toBigDecimal() ){
            return "${symbol}0"
        }

        var absPrice = priceAsDouble

        var priceSubtractor = false

        if(priceAsDouble < 0.toBigDecimal() ) {
            priceSubtractor = true
            absPrice = priceAsDouble * (-1).toBigDecimal()
        }

        val price = Utils.toDecimals(absPrice, 8).toDouble()

        var priceText: String

        priceText = if(price > 1)
            Utils.toDecimals(absPrice, 2)
        else
            "0${Utils.toDecimals(absPrice, 6)}"

        if(priceText.indexOf(".") != -1 && (priceText.indexOf(".") + 1 == priceText.length -1))
            priceText += "0"

        priceText = symbol + priceText

        if(priceSubtractor)
            priceText = "-$priceText"

        return priceText
    }


}
