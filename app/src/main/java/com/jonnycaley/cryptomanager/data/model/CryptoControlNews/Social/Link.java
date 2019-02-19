
package com.jonnycaley.cryptomanager.data.model.CryptoControlNews.Social;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Link implements Serializable
{

    @SerializedName("_id")
    @Expose
    public String id;
    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("link")
    @Expose
    public String link;
    private final static long serialVersionUID = -8701650003897368103L;

}
