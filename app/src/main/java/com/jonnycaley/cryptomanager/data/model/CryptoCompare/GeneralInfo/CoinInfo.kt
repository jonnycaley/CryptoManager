package com.jonnycaley.cryptomanager.data.model.CryptoCompare.GeneralInfo

import java.io.Serializable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CoinInfo : Serializable {

    @SerializedName("Id")
    @Expose
    var id: String? = null
    @SerializedName("Name")
    @Expose
    var name: String? = null
    @SerializedName("FullName")
    @Expose
    var fullName: String? = null
    @SerializedName("Internal")
    @Expose
    var internal: String? = null
    @SerializedName("ImageUrl")
    @Expose
    var imageUrl: String? = null
    @SerializedName("Url")
    @Expose
    var url: String? = null
    @SerializedName("Algorithm")
    @Expose
    var algorithm: String? = null
    @SerializedName("ProofType")
    @Expose
    var proofType: String? = null
    @SerializedName("NetHashesPerSecond")
    @Expose
    var netHashesPerSecond: Double? = null
    @SerializedName("BlockNumber")
    @Expose
    var blockNumber: Long? = null
    @SerializedName("BlockTime")
    @Expose
    var blockTime: Long? = null
    @SerializedName("BlockReward")
    @Expose
    var blockReward: Double? = null
    @SerializedName("Type")
    @Expose
    var type: Long? = null
    @SerializedName("DocumentType")
    @Expose
    var documentType: String? = null

    companion object {
        private const val serialVersionUID = 7642698740874538313L
    }

}
