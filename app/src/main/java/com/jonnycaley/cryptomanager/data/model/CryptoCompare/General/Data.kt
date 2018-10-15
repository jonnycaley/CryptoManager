package com.jonnycaley.cryptomanager.data.model.CryptoCompare.General

import java.io.Serializable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Data : Serializable {

    @SerializedName("USD")
    @Expose
    var uSD: USD? = null

    companion object {
        private const val serialVersionUID = 3410518599933113648L
    }

}
