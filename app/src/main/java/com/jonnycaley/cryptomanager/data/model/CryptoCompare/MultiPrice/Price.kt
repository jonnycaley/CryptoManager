package com.jonnycaley.cryptomanager.data.model.CryptoCompare.MultiPrice

import java.io.Serializable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Price : Serializable {

    @SerializedName("symbol")
    @Expose
    var symbol: String? = null
    @SerializedName("prices")
    @Expose
    var prices: Prices? = null

    companion object {
        private const val serialVersionUID = 8427095744291832267L
    }

}
