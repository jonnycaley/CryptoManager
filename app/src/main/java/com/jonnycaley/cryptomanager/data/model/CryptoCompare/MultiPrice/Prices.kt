package com.jonnycaley.cryptomanager.data.model.CryptoCompare.MultiPrice

import java.io.Serializable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

class Prices : Serializable {

    @SerializedName("USD")
    @Expose
    var uSD: BigDecimal? = null
//    @SerializedName("BTC")
//    @Expose
//    var bTC: Double? = null

//  TODO: possibly add back in for future

    companion object {
        private const val serialVersionUID = 7789536942545729862L
    }

}
