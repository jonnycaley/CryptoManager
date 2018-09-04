package com.jonnycaley.cryptomanager.data

import com.jonnycaley.cryptomanager.data.model.CryptoCompare.HistoricalData.Data
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CryptoCompareService {
    @GET("histo{time}")
    fun getCurrencyData(@Path("time") time: String, @Query("fsym") symbol: String, @Query("tsym") conversion: String, @Query("limit") limit: String, @Query("aggregate") aggregate: String): Single<Data>

}