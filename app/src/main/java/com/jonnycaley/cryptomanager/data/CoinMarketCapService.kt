package com.jonnycaley.cryptomanager.data

import com.jonnycaley.cryptomanager.data.model.CoinMarketCap.Currencies
import com.jonnycaley.cryptomanager.data.model.CryptoControlNews.News
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface CoinMarketCapService {
    @GET("cryptocurrency/listings/latest?limit=100")
    fun getTop100(@Query("start") limit: String): Single<Currencies>
}