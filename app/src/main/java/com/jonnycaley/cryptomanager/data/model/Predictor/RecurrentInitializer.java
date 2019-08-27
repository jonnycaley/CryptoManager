
package com.jonnycaley.cryptomanager.data.model.Predictor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RecurrentInitializer {

    @SerializedName("class_name")
    @Expose
    private String className;
    @SerializedName("config")
    @Expose
    private Config___ config;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Config___ getConfig() {
        return config;
    }

    public void setConfig(Config___ config) {
        this.config = config;
    }

}
