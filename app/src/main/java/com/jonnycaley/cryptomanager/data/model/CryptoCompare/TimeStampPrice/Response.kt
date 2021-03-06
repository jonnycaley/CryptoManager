package com.jonnycaley.cryptomanager.data.model.CryptoCompare.TimeStampPrice

import java.io.Serializable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Response : Serializable {

    @SerializedName("Response")
    @Expose
    var response: String? = null
    @SerializedName("Type")
    @Expose
    var type: Long? = null
    @SerializedName("Aggregated")
    @Expose
    var aggregated: Boolean? = null
    @SerializedName("Data")
    @Expose
    var data: List<Datum>? = null
    @SerializedName("TimeTo")
    @Expose
    var timeTo: Long? = null
    @SerializedName("TimeFrom")
    @Expose
    var timeFrom: Long? = null
    @SerializedName("FirstValueInArray")
    @Expose
    var firstValueInArray: Boolean? = null
    @SerializedName("ConversionType")
    @Expose
    var conversionType: ConversionType? = null

    companion object {
        private const val serialVersionUID = 5606076251076325235L
    }

}
