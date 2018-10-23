package com.jonnycaley.cryptomanager.data

import com.jonnycaley.cryptomanager.data.model.CryptoCompare.CurrentPrice.Price
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.GeneralInfo.GeneralInfo
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.HistoricalData.HistoricalData
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.News.News
import com.jonnycaley.cryptomanager.data.model.CryptoCompare.TimeStampPrice.Response
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CryptoCompareService {
    @GET("histo{time}")
    fun getCurrencyGraph(@Path("time") time: String, @Query("fsym") symbol: String, @Query("tsym") conversion: String, @Query("limit") limit: String, @Query("aggregate") aggregate: String): Observable<HistoricalData>

    @GET("histo{time}")
    fun getCurrencyGraphScalars(@Path("time") time: String, @Query("fsym") symbol: String, @Query("tsym") conversion: String, @Query("limit") limit: String, @Query("aggregate") aggregate: String): Observable<String>

    @GET("v2/news/?lang=EN&excludeCategories=Sponsored")
    fun getCurrencyNews(@Query("categories") symbol: String): Observable<News>

    @GET("all/coinlist")
    fun getAllCrypto(): Observable<String?>

    @GET("all/exchanges")
    fun getAllExchanges(): Observable<String>

    @GET("histominute")
    fun getPriceAtMinute(@Query("fsym") symbol: String, @Query("tsym") conversion: String, @Query("limit") limit: String, @Query("aggregate") aggregate: String, @Query("toTs") timestamp: String): Observable<Response>

    @GET("histominute")
    fun getPriceAtMinuteString(@Query("fsym") symbol: String, @Query("tsym") conversion: String, @Query("limit") limit: String, @Query("aggregate") aggregate: String, @Query("toTs") timestamp: String): Observable<String>

    @GET("histo{time}")
    fun getPriceAt(@Path("time") time: String, @Query("fsym") symbol: String, @Query("tsym") conversion: String, @Query("limit") limit: String): Observable<HistoricalData>

    @GET("price")
    fun getCurrentPrice(@Query("fsym") symbol: String, @Query("tsyms") conversion: String): Observable<Price>

    @GET("pricemulti")
    fun getMultiPrice(@Query("fsyms") symbol: String, @Query("tsyms") conversion: String): Observable<String>

    @GET("pricemultifull")
    fun getGeneralData(@Query("fsyms") symbol: String, @Query("tsyms") conversion: String): Observable<String>

    @GET("coin/generalinfo")
    fun getGeneralInfo(@Query("fsyms") symbol: String, @Query("tsym") conversion: String): Observable<GeneralInfo>


}