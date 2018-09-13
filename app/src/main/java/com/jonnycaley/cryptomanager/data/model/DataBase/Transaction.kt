package com.jonnycaley.cryptomanager.data.model.DataBase

import java.io.Serializable
import java.util.*

data class Transaction(val id : Int, val type : String, val exchange : String, val currency : String, val quantity : Double, val date : Date, val notes : String?) : Serializable