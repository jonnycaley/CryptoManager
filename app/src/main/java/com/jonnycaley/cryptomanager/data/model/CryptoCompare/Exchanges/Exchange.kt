package com.jonnycaley.cryptomanager.data.model.CryptoCompare.Exchanges

import java.io.Serializable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Exchange : Serializable {

    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("symbols")
    @Expose
    var symbols: List<Symbol>? = null

    companion object {
        private const val serialVersionUID = 2052850769949506670L
    }

}
