package com.jonnycaley.cryptomanager.data.model.CryptoCompare.AllCurrencies

import java.io.Serializable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Currencies : Serializable {

    @SerializedName("Response")
    @Expose
    var response: String? = null
    @SerializedName("Message")
    @Expose
    var message: String? = null
    @SerializedName("Data")
    @Expose
    var data: List<Datum>? = null
    @SerializedName("BaseImageUrl")
    @Expose
    var baseImageUrl: String? = null
    @SerializedName("BaseLinkUrl")
    @Expose
    var baseLinkUrl: String? = null

    companion object {
        private const val serialVersionUID = -939304900234904014L
    }

}
