package com.jonnycaley.cryptomanager.data.model.DataBase

import java.io.Serializable

data class FiatTransaction(val id : Int, val type : Variables.Transaction.Type, val exchange : String, val currency : String, val quantity : String, val date : String, val notes : String?) : Serializable