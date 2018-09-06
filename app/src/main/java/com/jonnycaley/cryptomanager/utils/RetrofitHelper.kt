package com.jonnycaley.cryptomanager.utils

import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class RetrofitHelper {

    fun createRetrofit(url : String, paramName : String?, paramKey : String?): Retrofit {
        return Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(createOkHttpClient(paramName, paramKey))
                .build()
    }

    fun createRetrofitWithScalars(url : String, paramName : String?, paramKey : String?): Retrofit {
        return Retrofit.Builder()
                .baseUrl(url)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(createOkHttpClient(paramName, paramKey))
                .build()
    }

    fun createOkHttpClient(paramName : String?, paramKey : String?): OkHttpClient {
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor { chain ->
            val original = chain.request()
            val originalHttpUrl = original.url()

            lateinit var url : HttpUrl

            if(paramName != null && paramKey != null){
                url = originalHttpUrl.newBuilder()
                        .addQueryParameter(paramName, paramKey)
                        .build()
            } else {
                url = originalHttpUrl.newBuilder()
                        .build()
            }

            println("Trying host: $url")

            // Request customization: add request headers
            val requestBuilder = original.newBuilder()
                    .url(url)

            val request = requestBuilder.build()
            chain.proceed(request)
        }

        return httpClient.build()
    }
}