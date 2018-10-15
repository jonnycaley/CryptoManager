package com.jonnycaley.cryptomanager.data.model.ExchangeRates

import java.io.Serializable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ExchangeRates : Serializable {

    @SerializedName("rates")
    @Expose
    var rates: List<Rate>? = null

    companion object {
        private const val serialVersionUID = -6604973715161531602L
    }

}
