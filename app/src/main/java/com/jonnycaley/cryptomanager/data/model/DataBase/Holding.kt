package com.jonnycaley.cryptomanager.data.model.DataBase

import java.io.Serializable

data class Holding(val symbol : String, val quantity : Float, val cost : Double,  val type : String) : Serializable