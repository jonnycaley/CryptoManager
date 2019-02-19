package com.jonnycaley.cryptomanager.data.model.CryptoCompare.PriceAtTimestampForReal

import java.io.Serializable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

class Price : Serializable {

    @SerializedName("Symbol")
    @Expose
    var symbol: String? = null

    @SerializedName("USD")
    @Expose
    var uSD: BigDecimal? = null


    @SerializedName("ETH")
    @Expose
    var eTh: BigDecimal? = null


    @SerializedName("BTC")
    @Expose
    var bTC: BigDecimal? = null

    companion object {
        private const val serialVersionUID = 8309645155194775027L
    }

}
