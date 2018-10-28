package com.jonnycaley.cryptomanager.data.model.DataBase

import java.io.Serializable
import java.math.BigDecimal

data class Holding(var symbol : String, var quantity : BigDecimal, var costUsd : BigDecimal, var costBtc : BigDecimal, var costEth : BigDecimal,  var type : String, var imageUrl : String?) : Serializable