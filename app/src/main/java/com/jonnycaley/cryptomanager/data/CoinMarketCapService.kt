package com.jonnycaley.cryptomanager.data

import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Currencies
import io.reactivex.Single
import retrofit2.http.GET

interface CoinMarketCapService {
    @GET("cryptocurrency/listings/latest?start=1&limit=100")
    fun getTop100(): Single<Currencies>

    @GET("cryptocurrency/listings/latest?start=1&limit=100&convert=USD")
    fun getTop100USD(): Single<Currencies>

    @GET("cryptocurrency/listings/latest?start=1&limit=100&convert=BTC")
    fun getTop100BTC(): Single<Currencies>
}