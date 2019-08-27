
package com.jonnycaley.cryptomanager.data.model.Predictor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BetaInitializer {

    @SerializedName("class_name")
    @Expose
    private String className;
    @SerializedName("config")
    @Expose
    private Config_____ config;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Config_____ getConfig() {
        return config;
    }

    public void setConfig(Config_____ config) {
        this.config = config;
    }

}
