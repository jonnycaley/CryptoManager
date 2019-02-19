package com.jonnycaley.cryptomanager.data.model.CryptoCompare.GeneralInfo

import java.io.Serializable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GeneralInfo : Serializable {

    @SerializedName("Message")
    @Expose
    var message: String? = null
    @SerializedName("Type")
    @Expose
    var type: Long? = null
    @SerializedName("Data")
    @Expose
    var data: List<Datum>? = null

    companion object {
        private const val serialVersionUID = 9024820647869296734L
    }

}
