package com.jonnycaley.cryptomanager.data.model.CryptoControlNews.News

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class SimilarArticle : Serializable {

    @SerializedName("_id")
    @Expose
    var id: String? = null
    @SerializedName("title")
    @Expose
    var title: String? = null
    @SerializedName("publishedAt")
    @Expose
    var publishedAt: String? = null
    @SerializedName("sourceDomain")
    @Expose
    var sourceDomain: String? = null
    @SerializedName("url")
    @Expose
    var url: String? = null
    @SerializedName("thumbnail")
    @Expose
    var thumbnail: String? = null

}
