package com.jonnycaley.cryptomanager.data.model.CoinMarketCap

import java.io.Serializable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Currencies : Serializable {

    @SerializedName("status")
    @Expose
    var status: Status? = null
    @SerializedName("data")
    @Expose
    var data: List<Currency>? = null

    companion object {
        private const val serialVersionUID = 8601485601519706969L
    }

}
