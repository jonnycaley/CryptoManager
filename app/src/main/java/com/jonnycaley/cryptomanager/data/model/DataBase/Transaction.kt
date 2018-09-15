package com.jonnycaley.cryptomanager.data.model.DataBase

import java.io.Serializable
import java.util.*

data class Transaction(val type : String, val exchange : String, val currency : String, val quantity : Long, val price : Long, val date : Date, val notes : String?) : Serializable