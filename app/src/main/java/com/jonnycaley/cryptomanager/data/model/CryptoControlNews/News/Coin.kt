package com.jonnycaley.cryptomanager.data.model.CryptoControlNews.News

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class Coin : Serializable{

    @SerializedName("_id")
    @Expose
    var id: String? = null
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("slug")
    @Expose
    var slug: String? = null
    @SerializedName("tradingSymbol")
    @Expose
    var tradingSymbol: String? = null

}
