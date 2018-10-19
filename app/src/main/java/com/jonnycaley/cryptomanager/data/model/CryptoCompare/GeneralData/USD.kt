package com.jonnycaley.cryptomanager.data.model.CryptoCompare.GeneralData

import java.io.Serializable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class USD : Serializable {

    @SerializedName("FROMSYMBOL")
    @Expose
    var fROMSYMBOL: String? = null
    @SerializedName("TOSYMBOL")
    @Expose
    var tOSYMBOL: String? = null
    @SerializedName("MARKET")
    @Expose
    var mARKET: String? = null
    @SerializedName("PRICE")
    @Expose
    var pRICE: String? = null
    @SerializedName("LASTUPDATE")
    @Expose
    var lASTUPDATE: String? = null
    @SerializedName("LASTVOLUME")
    @Expose
    var lASTVOLUME: String? = null
    @SerializedName("LASTVOLUMETO")
    @Expose
    var lASTVOLUMETO: String? = null
    @SerializedName("LASTTRADEID")
    @Expose
    var lASTTRADEID: String? = null
    @SerializedName("VOLUMEDAY")
    @Expose
    var vOLUMEDAY: String? = null
    @SerializedName("VOLUMEDAYTO")
    @Expose
    var vOLUMEDAYTO: String? = null
    @SerializedName("VOLUME24HOUR")
    @Expose
    var vOLUME24HOUR: String? = null
    @SerializedName("VOLUME24HOURTO")
    @Expose
    var vOLUME24HOURTO: String? = null
    @SerializedName("OPENDAY")
    @Expose
    var oPENDAY: String? = null
    @SerializedName("HIGHDAY")
    @Expose
    var hIGHDAY: String? = null
    @SerializedName("LOWDAY")
    @Expose
    var lOWDAY: String? = null
    @SerializedName("OPEN24HOUR")
    @Expose
    var oPEN24HOUR: String? = null
    @SerializedName("HIGH24HOUR")
    @Expose
    var hIGH24HOUR: String? = null
    @SerializedName("LOW24HOUR")
    @Expose
    var lOW24HOUR: String? = null
    @SerializedName("LASTMARKET")
    @Expose
    var lASTMARKET: String? = null
    @SerializedName("CHANGE24HOUR")
    @Expose
    var cHANGE24HOUR: String? = null
    @SerializedName("CHANGEPCT24HOUR")
    @Expose
    var cHANGEPCT24HOUR: String? = null
    @SerializedName("CHANGEDAY")
    @Expose
    var cHANGEDAY: String? = null
    @SerializedName("CHANGEPCTDAY")
    @Expose
    var cHANGEPCTDAY: String? = null
    @SerializedName("SUPPLY")
    @Expose
    var sUPPLY: String? = null
    @SerializedName("MKTCAP")
    @Expose
    var mKTCAP: String? = null
    @SerializedName("TOTALVOLUME24H")
    @Expose
    var tOTALVOLUME24H: String? = null
    @SerializedName("TOTALVOLUME24HTO")
    @Expose
    var tOTALVOLUME24HTO: String? = null

    companion object {
        private const val serialVersionUID = -1484067923379564287L
    }

}
