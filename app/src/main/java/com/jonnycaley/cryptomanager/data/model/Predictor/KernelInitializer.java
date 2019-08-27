
package com.jonnycaley.cryptomanager.data.model.Predictor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class KernelInitializer {

    @SerializedName("class_name")
    @Expose
    private String className;
    @SerializedName("config")
    @Expose
    private Config__ config;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Config__ getConfig() {
        return config;
    }

    public void setConfig(Config__ config) {
        this.config = config;
    }

}
