package com.jonnycaley.cryptomanager.data.model.ExchangeRates

import java.io.Serializable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

class Rate : Serializable {

    @SerializedName("fiat")
    @Expose
    var fiat: String? = null
    @SerializedName("rate")
    @Expose
    var rate: BigDecimal? = null

    companion object {
        private const val serialVersionUID = 1944469845247308751L
    }

}
