package com.jonnycaley.cryptomanager.data.model.CryptoControlNews.Social

import java.io.Serializable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Social : Serializable {

    @SerializedName("description")
    @Expose
    var description: String? = null
//    @SerializedName("gitrepos")
//    @Expose
//    var gitrepos: List<Any>? = null
    @SerializedName("links")
    @Expose
    var links: List<Link>? = null
    @SerializedName("subreddits")
    @Expose
    var subreddits: List<String>? = null
    @SerializedName("twitterUsernames")
    @Expose
    var twitterUsernames: List<String>? = null

    companion object {
        private const val serialVersionUID = 7414464028196804189L
    }

}
