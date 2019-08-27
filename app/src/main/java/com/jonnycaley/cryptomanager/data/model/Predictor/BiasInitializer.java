
package com.jonnycaley.cryptomanager.data.model.Predictor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BiasInitializer {

    @SerializedName("class_name")
    @Expose
    private String className;
    @SerializedName("config")
    @Expose
    private Config____ config;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Config____ getConfig() {
        return config;
    }

    public void setConfig(Config____ config) {
        this.config = config;
    }

}
