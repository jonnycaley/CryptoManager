package com.jonnycaley.cryptomanager.data.model.DataBase

import java.io.Serializable
import java.math.BigDecimal

data class Holding(var symbol : String, var quantity : BigDecimal, var costUsd : Double, var costBtc : Double, var costEth : Double,  var type : String, var imageUrl : String?) : Serializable