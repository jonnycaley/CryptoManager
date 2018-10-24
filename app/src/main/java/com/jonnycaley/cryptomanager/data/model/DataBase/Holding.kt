package com.jonnycaley.cryptomanager.data.model.DataBase

import java.io.Serializable

data class Holding(var symbol : String, var quantity : Float, var costUsd : Double, var costBtc : Double, var costEth : Double,  var type : String, var imageUrl : String?) : Serializable