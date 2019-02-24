package com.jonnycaley.cryptomanager.data.model.DataBase

import java.io.Serializable
import java.math.BigDecimal
import java.util.*

data class Transaction(var exchange : String, var symbol : String, var name : String, var pairSymbol : String?, var quantity : BigDecimal, var price : BigDecimal, var priceUSD : BigDecimal, var date : Date, var notes : String?, var isDeducted : Boolean, var isDeductedPriceUsd : BigDecimal, var baseImageUrl : String?, var pairImageUrl : String?, var btcPrice: BigDecimal, var ethPrice : BigDecimal) : Serializable

//pairSymbol is null for fiat deposits/withdrawls (>0/<0)
//pairSymbol is not null for crypto holdings e.g. BTC/ETH symbol would be BTC pairSymbol would be ETH ------ buy/sell -> >0/<0
//isDeductedPriceUsd null -> is not deducted; else -> is deducted and pair/USD price is value
//priceUSD is the usd price of the symbol at the time
//price is the price against the pair (e.g. ETH/BTC could be 0.03 BTC)

//for fiat...
//priceUSD is the (exchange rate price on the transaction day * quantity)
//price is the exchange rate price on the transaction day
