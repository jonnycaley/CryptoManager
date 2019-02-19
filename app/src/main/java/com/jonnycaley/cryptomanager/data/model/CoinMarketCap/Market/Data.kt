package com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Market

import java.io.Serializable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Data : Serializable {

    @SerializedName("btc_dominance")
    @Expose
    var btcDominance: Float? = null
    @SerializedName("eth_dominance")
    @Expose
    var ethDominance: Float? = null
    @SerializedName("active_cryptocurrencies")
    @Expose
    var activeCryptocurrencies: Int? = null
    @SerializedName("active_market_pairs")
    @Expose
    var activeMarketPairs: Int? = null
    @SerializedName("active_exchanges")
    @Expose
    var activeExchanges: Int? = null
    @SerializedName("last_updated")
    @Expose
    var lastUpdated: String? = null
    @SerializedName("quote")
    @Expose
    var quote: Quote? = null

    companion object {
        private const val serialVersionUID = -305119108882521200L
    }

}
