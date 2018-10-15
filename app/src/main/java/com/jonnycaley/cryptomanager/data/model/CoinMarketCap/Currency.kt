package com.jonnycaley.cryptomanager.data.model.CoinMarketCap

import java.io.Serializable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Currency : Serializable {

    @SerializedName("id")
    @Expose
    var id: Long? = null
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("symbol")
    @Expose
    var symbol: String? = null
    @SerializedName("slug")
    @Expose
    var slug: String? = null
    @SerializedName("circulating_supply")
    @Expose
    var circulatingSupply: Float? = null
    @SerializedName("total_supply")
    @Expose
    var totalSupply: Float? = null
    @SerializedName("max_supply")
    @Expose
    var maxSupply: Float? = null
    @SerializedName("date_added")
    @Expose
    var dateAdded: String? = null
    @SerializedName("num_market_pairs")
    @Expose
    var numMarketPairs: Long? = null
    @SerializedName("cmc_rank")
    @Expose
    var cmcRank: Long? = null
    @SerializedName("last_updated")
    @Expose
    var lastUpdated: String? = null
    @SerializedName("quote")
    @Expose
    var quote: Quote? = null

    companion object {
        private const val serialVersionUID = 414026005826130565L
    }

}
