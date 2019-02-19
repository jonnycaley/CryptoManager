package com.jonnycaley.cryptomanager.data

import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.News.Article
import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.Social.Social
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CryptoControlService {

    @GET("news")
    fun getTopNews(): Observable<Array<Article>>


    @GET("news?latest=true")
    fun getLatestNews(@Query("limit") limit: String): Observable<Array<Article>>


    @GET("news/coin/{symbol}")
    fun getCurrencyNews(@Path("symbol") currency: String): Observable<Array<Article>>


    @GET("details/coin/{symbol}")
    fun getCurrencySocial(@Path("symbol") currency: String): Observable<Social>

}