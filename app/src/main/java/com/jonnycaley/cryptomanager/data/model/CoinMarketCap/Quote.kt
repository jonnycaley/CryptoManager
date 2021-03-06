package com.jonnycaley.cryptomanager.data.model.CoinMarketCap

import java.io.Serializable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Quote : Serializable {

    @SerializedName("USD")
    @Expose
    var uSD: USD? = null

    @SerializedName("BTC")
    @Expose
    var bTC: USD? = null

    companion object {
        private const val serialVersionUID = -7649085317542117573L
    }

}
