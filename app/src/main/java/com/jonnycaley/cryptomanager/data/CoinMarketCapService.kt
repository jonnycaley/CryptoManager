package com.jonnycaley.cryptomanager.data

import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Currencies
import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Market.Market
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface CoinMarketCapService {
    @GET("cryptocurrency/listings/latest?start=1&limit=100")
    fun getTop100(): Observable<Currencies>

    @GET("cryptocurrency/listings/latest?start=1&limit=100&convert=USD")
    fun getTop100USD(): Observable<Currencies>

    @GET("cryptocurrency/listings/latest?start=1&convert=USD")
    fun getTopUSD(@Query("limit") symbol: String): Observable<Currencies>

    @GET("cryptocurrency/listings/latest?start=1&limit=100&convert=BTC")
    fun getTop100BTC(): Observable<Currencies>

    @GET("global-metrics/quotes/latest")
    fun getMarketData(): Observable<Market>
}