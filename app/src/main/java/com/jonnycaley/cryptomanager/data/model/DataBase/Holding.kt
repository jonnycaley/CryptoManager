package com.jonnycaley.cryptomanager.data.model.DataBase

import java.io.Serializable

data class Holding(val currency : String, val quantity : Double, val cost : Long, val type : String) : Serializable