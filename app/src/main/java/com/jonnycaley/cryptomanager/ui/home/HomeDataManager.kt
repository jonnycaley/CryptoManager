package com.jonnycaley.cryptomanager.ui.home

import android.content.Context
import com.jonnycaley.cryptomanager.data.CoinMarketCapService
import com.jonnycaley.cryptomanager.data.CryptoControlService
import com.jonnycaley.cryptomanager.utils.Constants
import com.jonnycaley.cryptomanager.utils.Utils
import com.jonnycaley.cryptomanager.utils.prefs.UserPreferences
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class HomeDataManager private constructor(val UserPreferences: UserPreferences) {

    companion object {

        private var INSTANCE: HomeDataManager? = null

        private lateinit var context: Context

        private val TAG = "NewsData"

        @JvmStatic
        fun getInstance(context: Context): HomeDataManager {
            if (INSTANCE == null) {
                INSTANCE = HomeDataManager(UserPreferences.getInstance(context))
                this.context = context
            }
            return INSTANCE!!
        }
    }



    fun checkConnection(): Boolean {
        return Utils.isNetworkConnected(context)
    }

    fun createOkHttpClient(paramName : String, paramKey : String): OkHttpClient {
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor { chain ->
            val original = chain.request()
            val originalHttpUrl = original.url()

            val url = originalHttpUrl.newBuilder()
                    .addQueryParameter(paramName, paramKey)
                    .build()

            // Request customization: add request headers
            val requestBuilder = original.newBuilder()
                    .url(url)

            val request = requestBuilder.build()
            chain.proceed(request)
        }

        return httpClient.build()
    }

    fun getCryptoControlService(): CryptoControlService {
        val retrofit = createRetrofit("https://cryptocontrol.io/api/v1/public/", Constants.CRYPTOCONTROL_NAME, Constants.CRYPTOCONTROL_KEY)
        return retrofit.create(CryptoControlService::class.java)
    }

    private fun createRetrofit(url : String, paramName : String, paramKey : String): Retrofit {
        return Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(createOkHttpClient(paramName, paramKey))
                .build()
    }

    fun getCoinMarketCapService(): CoinMarketCapService {
        val retrofit = createRetrofit("https://pro-api.coinmarketcap.com/v1/", Constants.COINMARKETCAP_NAME, Constants.COINMARKETCAP_KEY)
        return retrofit.create(CoinMarketCapService::class.java)
    }

}