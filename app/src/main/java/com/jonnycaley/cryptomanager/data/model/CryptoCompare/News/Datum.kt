package com.jonnycaley.cryptomanager.data.model.CryptoCompare.News

import java.io.Serializable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Datum : Serializable {

    @SerializedName("id")
    @Expose
    var id: String? = null
    @SerializedName("guid")
    @Expose
    var guid: String? = null
    @SerializedName("published_on")
    @Expose
    var publishedOn: Long? = null
    @SerializedName("imageurl")
    @Expose
    var imageurl: String? = null
    @SerializedName("title")
    @Expose
    var title: String? = null
    @SerializedName("url")
    @Expose
    var url: String? = null
    @SerializedName("source")
    @Expose
    var source: String? = null
    @SerializedName("body")
    @Expose
    var body: String? = null
    @SerializedName("tags")
    @Expose
    var tags: String? = null
    @SerializedName("categories")
    @Expose
    var categories: String? = null
    @SerializedName("upvotes")
    @Expose
    var upvotes: String? = null
    @SerializedName("downvotes")
    @Expose
    var downvotes: String? = null
    @SerializedName("lang")
    @Expose
    var lang: String? = null
    @SerializedName("source_info")
    @Expose
    var sourceInfo: SourceInfo_? = null

    companion object {
        private const val serialVersionUID = -4300866526123376585L
    }

}
