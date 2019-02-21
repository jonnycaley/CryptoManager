package com.jonnycaley.cryptomanager.data.model.Utils

import java.io.Serializable

data class Chart(var limit : Int, var aggregate : Int, var measure : String) : Serializable