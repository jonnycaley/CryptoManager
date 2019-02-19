package com.jonnycaley.cryptomanager.data.model.Nomics.MarketCap

import java.io.Serializable

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MarketCap : Serializable {

    @SerializedName("timestamp")
    @Expose
    var timestamp: String? = null
    @SerializedName("market_cap")
    @Expose
    var marketCap: String? = null

    companion object {
        private const val serialVersionUID = -4385852969341257227L
    }

}
