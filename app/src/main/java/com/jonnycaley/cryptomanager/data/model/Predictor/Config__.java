
package com.jonnycaley.cryptomanager.data.model.Predictor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Config__ {

    @SerializedName("seed")
    @Expose
    private Object seed;
    @SerializedName("dtype")
    @Expose
    private String dtype;

    public Object getSeed() {
        return seed;
    }

    public void setSeed(Object seed) {
        this.seed = seed;
    }

    public String getDtype() {
        return dtype;
    }

    public void setDtype(String dtype) {
        this.dtype = dtype;
    }

}
