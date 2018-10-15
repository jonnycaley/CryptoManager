package com.jonnycaley.cryptomanager.data.model.CryptoCompare.HistoricalData

import java.io.Serializable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ConversionType : Serializable {

    @SerializedName("type")
    @Expose
    var type: String? = null
    @SerializedName("conversionSymbol")
    @Expose
    var conversionSymbol: String? = null

    companion object {
        private const val serialVersionUID = -1637736086648811880L
    }

}
