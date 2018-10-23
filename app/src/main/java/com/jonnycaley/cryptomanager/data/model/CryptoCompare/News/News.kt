package com.jonnycaley.cryptomanager.data.model.CryptoCompare.News

import java.io.Serializable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class News : Serializable {

    @SerializedName("Type")
    @Expose
    var type: Long? = null
    @SerializedName("Message")
    @Expose
    var message: String? = null
    @SerializedName("Promoted")
    @Expose
    var promoted: List<Promoted>? = null
    @SerializedName("Data")
    @Expose
    var data: List<Datum>? = null

    companion object {
        private const val serialVersionUID = -8629232095946710110L
    }

}
