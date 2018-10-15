package com.jonnycaley.cryptomanager.data.model.CryptoCompare.CurrentPrice

import java.io.Serializable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Price : Serializable {

    @SerializedName("USD")
    @Expose
    var uSD: Double? = null

    companion object {
        private const val serialVersionUID = -1471727736538909802L
    }

}
