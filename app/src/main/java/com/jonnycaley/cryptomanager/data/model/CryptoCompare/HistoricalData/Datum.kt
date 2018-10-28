package com.jonnycaley.cryptomanager.data.model.CryptoCompare.HistoricalData

import java.io.Serializable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

class Datum : Serializable {

    @SerializedName("time")
    @Expose
    var time: Long? = null
    @SerializedName("close")
    @Expose
    var close: BigDecimal? = null
    @SerializedName("high")
    @Expose
    var high: BigDecimal? = null
    @SerializedName("low")
    @Expose
    var low: BigDecimal? = null
    @SerializedName("open")
    @Expose
    var open: BigDecimal? = null
    @SerializedName("volumefrom")
    @Expose
    var volumefrom: Double? = null
    @SerializedName("volumeto")
    @Expose
    var volumeto: Double? = null

    companion object {
        private const val serialVersionUID = 3224776324444230935L
    }

}
