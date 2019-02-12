package com.jonnycaley.cryptomanager.data

import com.jonnycaley.cryptomanager.data.model.Nomics.MarketCap.MarketCap
import com.jonnycaley.cryptomanager.data.model.Nomics.Volume.Volume
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface NomicsService {

    @GET("market-cap/history")
    fun getMarketCap(@Query("start") start: String): Observable<MarketCap>


    @GET("market-cap/history")
    fun getVolume(@Query("start") start: String): Observable<Volume>
}