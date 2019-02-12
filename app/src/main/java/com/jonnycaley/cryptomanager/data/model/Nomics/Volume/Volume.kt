package com.jonnycaley.cryptomanager.data.model.Nomics.Volume

import java.io.Serializable

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Volume : Serializable {

    @SerializedName("timestamp")
    @Expose
    var timestamp: String? = null
    @SerializedName("volume")
    @Expose
    var marketCap: String? = null

    companion object {
        private const val serialVersionUID = -4385852969341257227L
    }

}
