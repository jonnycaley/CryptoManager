package com.jonnycaley.cryptomanager.data.model.CryptoControlNews

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class News : Serializable{

    @SerializedName("_id")
    @Expose
    var id: String? = null
    @SerializedName("hotness")
    @Expose
    var hotness: Float? = null
    @SerializedName("activityHotness")
    @Expose
    var activityHotness: Float? = null
    @SerializedName("primaryCategory")
    @Expose
    var primaryCategory: String? = null
    @SerializedName("words")
    @Expose
    var words: Int? = null
    @SerializedName("similarArticles")
    @Expose
    var similarArticles: List<SimilarArticle>? = null
    @SerializedName("coins")
    @Expose
    var coins: List<Coin>? = null
    @SerializedName("description")
    @Expose
    var description: String? = null
    @SerializedName("publishedAt")
    @Expose
    var publishedAt: String? = null
    @SerializedName("title")
    @Expose
    var title: String? = null
    @SerializedName("url")
    @Expose
    var url: String? = null
    @SerializedName("source")
    @Expose
    var source: Source? = null
    @SerializedName("thumbnail")
    @Expose
    var thumbnail: String? = null
    @SerializedName("sourceDomain")
    @Expose
    var sourceDomain: String? = null
    @SerializedName("originalImageUrl")
    @Expose
    var originalImageUrl: String? = null

}
