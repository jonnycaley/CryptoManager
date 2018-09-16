package com.jonnycaley.cryptomanager.data.model.DataBase

import java.io.Serializable

data class Holding(val currency : String, val quantity : Float, val type : String) : Serializable