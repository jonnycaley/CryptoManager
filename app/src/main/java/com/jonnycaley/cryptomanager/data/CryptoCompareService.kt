package com.jonnycaley.cryptomanager.data

import com.jonnycaley.cryptomanager.data.model.CryptoCompare.HistoricalData.Data
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.News.News
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CryptoCompareService {
    @GET("histo{time}")
    fun getCurrencyGraph(@Path("time") time: String, @Query("fsym") symbol: String, @Query("tsym") conversion: String, @Query("limit") limit: String, @Query("aggregate") aggregate: String): Single<Data>

    @GET("v2/news/?lang=EN&excludeCategories=Sponsored")
    fun getCurrencyNews(@Query("categories") symbol: String): Single<News>

    @GET("all/coinlist")
    fun getAllCurrencies(): Single<String>


    @GET("all/exchanges")
    fun getAllExchanges(): Single<String>

}