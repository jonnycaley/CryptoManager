package com.jonnycaley.cryptomanager.data.model.CryptoCompare.MultiPrice

import java.io.Serializable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MultiPrices : Serializable {

    @SerializedName("prices")
    @Expose
    var prices: List<Price>? = null

    companion object {
        private const val serialVersionUID = -2832808215286642993L
    }

}
