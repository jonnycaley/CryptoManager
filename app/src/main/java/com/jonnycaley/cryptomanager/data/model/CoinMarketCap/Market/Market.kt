package com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Market

import java.io.Serializable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Market.Data

class Market : Serializable {

    @SerializedName("data")
    @Expose
    var data: Data? = null
    @SerializedName("status")
    @Expose
    var status: Status? = null

    companion object {
        private const val serialVersionUID = 5238751506793636051L
    }

}
