package com.jonnycaley.cryptomanager.data.model.CryptoCompare.News

import java.io.Serializable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SourceInfo_ : Serializable {

    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("lang")
    @Expose
    var lang: String? = null
    @SerializedName("img")
    @Expose
    var img: String? = null

    companion object {
        private const val serialVersionUID = -7986612858113499787L
    }

}
