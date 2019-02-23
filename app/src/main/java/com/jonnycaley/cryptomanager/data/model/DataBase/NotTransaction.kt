package com.jonnycaley.cryptomanager.data.model.DataBase

import com.jonnycaley.cryptomanager.data.model.CryptoCompare.AllCurrencies.Datum
import java.io.Serializable

data class NotTransaction(val currency: Datum, val imageUrl: String?, val baseUrl: String?, val backpressToPortfolio : Boolean) : Serializable
