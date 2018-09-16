package com.jonnycaley.cryptomanager.data.model.DataBase

import java.io.Serializable
import java.util.*

data class Transaction(val exchange : String, val symbol : String, val pairSymbol : String?, val quantity : Float, val price : Float, val date : Date, val notes : String?, val isDeducted : Boolean?) : Serializable

//pairSymbol is null for fiat deposits/withdrawls (>0/<0)
//pairSymbol is not null for crypto transactions e.g. BTC/ETH symbol would be BTC pairSymbol would be ETH ------ buy/sell -> >0/<0
//isDeducted 0 -> no; 1 -> yes