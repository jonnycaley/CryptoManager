
package com.jonnycaley.cryptomanager.data.model.Predictor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Layer {

    @SerializedName("class_name")
    @Expose
    private String className;
    @SerializedName("config")
    @Expose
    private Config_ config;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Config_ getConfig() {
        return config;
    }

    public void setConfig(Config_ config) {
        this.config = config;
    }

}
