package com.jonnycaley.cryptomanager.data.model.CryptoCompare.GeneralInfo

import java.io.Serializable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ConversionInfo : Serializable {

    @SerializedName("Conversion")
    @Expose
    var conversion: String? = null
    @SerializedName("ConversionSymbol")
    @Expose
    var conversionSymbol: String? = null
    @SerializedName("CurrencyFrom")
    @Expose
    var currencyFrom: String? = null
    @SerializedName("CurrencyTo")
    @Expose
    var currencyTo: String? = null
    @SerializedName("Market")
    @Expose
    var market: String? = null
    @SerializedName("Supply")
    @Expose
    var supply: Long? = null
    @SerializedName("TotalVolume24H")
    @Expose
    var totalVolume24H: Double? = null
    @SerializedName("SubBase")
    @Expose
    var subBase: String? = null
    @SerializedName("SubsNeeded")
    @Expose
    var subsNeeded: List<String>? = null
    @SerializedName("RAW")
    @Expose
    var rAW: List<String>? = null

    companion object {
        private const val serialVersionUID = 1121812058879666583L
    }

}
