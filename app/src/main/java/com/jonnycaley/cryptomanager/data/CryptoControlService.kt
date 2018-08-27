package com.jonnycaley.cryptomanager.data

import com.jonnycaley.cryptomanager.data.model.news.News
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.GET

interface CryptoControlService {

    @GET("news")
    fun getTopNews(): Single<Array<News>>
}