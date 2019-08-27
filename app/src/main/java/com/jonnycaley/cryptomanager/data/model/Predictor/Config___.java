
package com.jonnycaley.cryptomanager.data.model.Predictor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Config___ {

    @SerializedName("gain")
    @Expose
    private Double gain;
    @SerializedName("seed")
    @Expose
    private Object seed;
    @SerializedName("dtype")
    @Expose
    private String dtype;

    public Double getGain() {
        return gain;
    }

    public void setGain(Double gain) {
        this.gain = gain;
    }

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
