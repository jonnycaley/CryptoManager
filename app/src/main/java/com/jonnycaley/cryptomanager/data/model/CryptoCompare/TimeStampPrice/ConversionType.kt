package com.jonnycaley.cryptomanager.data.model.CryptoCompare.TimeStampPrice

import java.io.Serializable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ConversionType : Serializable {

    @SerializedName("type")
    @Expose
    var type: String? = null
    @SerializedName("conversionSymbol")
    @Expose
    var conversionSymbol: String? = null

    companion object {
        private const val serialVersionUID = 8345604875015279988L
    }

}
