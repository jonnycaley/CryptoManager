package com.jonnycaley.cryptomanager.data

import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.Article
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CryptoControlService {

    @GET("news")
    fun getTopNews(@Query("limit") limit: String): Single<Array<Article>>


    @GET("news?latest=true")
    fun getLatestNews(@Query("limit") limit: String): Single<Array<Article>>


    @GET("news/coin/{symbol}")
    fun getCurrencyNews(@Path("symbol") currency: String): Single<Array<Article>>

}