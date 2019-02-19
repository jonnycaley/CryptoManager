package com.jonnycaley.cryptomanager.data.model.CryptoCompare.GeneralInfo

import java.io.Serializable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Datum : Serializable {

    @SerializedName("CoinInfo")
    @Expose
    var coinInfo: CoinInfo? = null
//    @SerializedName("ConversionInfo")
//    @Expose
//    var conversionInfo: ConversionInfo? = null

    companion object {
        private const val serialVersionUID = -5576088300422251583L
    }

}
