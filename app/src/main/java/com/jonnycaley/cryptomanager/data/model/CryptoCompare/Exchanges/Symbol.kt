package com.jonnycaley.cryptomanager.data.model.CryptoCompare.Exchanges

import java.io.Serializable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Symbol : Serializable {

    @SerializedName("symbol")
    @Expose
    var symbol: String? = null
    @SerializedName("converters")
    @Expose
    var converters: List<String>? = null

    companion object {
        private const val serialVersionUID = 8135977198303971047L
    }

}
