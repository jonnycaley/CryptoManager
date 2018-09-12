package com.jonnycaley.cryptomanager.data

import com.jonnycaley.cryptomanager.data.model.CryptoCompare.Exchanges.Exchanges
import io.reactivex.Single
import retrofit2.http.GET

interface ExchangeRatesService {

    @GET("latest?base=USD")
    fun getExchangeRates(): Single<String>

}
