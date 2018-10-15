package com.jonnycaley.cryptomanager.data.model.CoinMarketCap

import java.io.Serializable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class USD : Serializable {

    @SerializedName("price")
    @Expose
    var price: Float? = null
    @SerializedName("volume_24h")
    @Expose
    var volume24h: Float? = null
    @SerializedName("percent_change_1h")
    @Expose
    var percentChange1h: Float? = null
    @SerializedName("percent_change_24h")
    @Expose
    var percentChange24h: Float? = null
    @SerializedName("percent_change_7d")
    @Expose
    var percentChange7d: Float? = null
    @SerializedName("market_cap")
    @Expose
    var marketCap: Float? = null
    @SerializedName("last_updated")
    @Expose
    var lastUpdated: String? = null

    companion object {
        private const val serialVersionUID = -4950104786989413331L
    }

}
