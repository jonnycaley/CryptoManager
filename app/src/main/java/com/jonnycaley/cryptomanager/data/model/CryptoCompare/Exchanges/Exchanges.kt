package com.jonnycaley.cryptomanager.data.model.CryptoCompare.Exchanges

import java.io.Serializable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Exchanges : Serializable {

    @SerializedName("exchanges")
    @Expose
    var exchanges: List<Exchange>? = null

    companion object {
        private const val serialVersionUID = -5179421123172065537L
    }

}
