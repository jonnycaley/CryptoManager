package com.jonnycaley.cryptomanager.data.model.CryptoCompare.TimeStampPrice

import java.io.Serializable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Datum : Serializable {

    @SerializedName("time")
    @Expose
    var time: Long? = null
    @SerializedName("close")
    @Expose
    var close: Double? = null
    @SerializedName("high")
    @Expose
    var high: Double? = null
    @SerializedName("low")
    @Expose
    var low: Double? = null
    @SerializedName("open")
    @Expose
    var open: Double? = null
//    @SerializedName("volumefrom")
//    @Expose
//    var volumefrom: Long? = null
//    @SerializedName("volumeto")
//    @Expose
//    var volumeto: Long? = null

    companion object {
        private const val serialVersionUID = -303234590215995297L
    }

}
