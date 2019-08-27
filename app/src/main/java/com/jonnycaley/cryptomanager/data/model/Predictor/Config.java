
package com.jonnycaley.cryptomanager.data.model.Predictor;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Config {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("layers")
    @Expose
    private List<Layer> layers = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Layer> getLayers() {
        return layers;
    }

    public void setLayers(List<Layer> layers) {
        this.layers = layers;
    }

}
