package com.jonnycaley.cryptomanager.data.model.Predictor

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Model {

    @SerializedName("class_name")
    @Expose
    var className: String? = null
    @SerializedName("config")
    @Expose
    var config: Config? = null
    @SerializedName("keras_version")
    @Expose
    var kerasVersion: String? = null
    @SerializedName("backend")
    @Expose
    var backend: String? = null

}
