//package com.secure.jnet.wallet.data.remote
//
//import com.secure.jnet.wallet.BuildConfig
//import okhttp3.OkHttpClient
//import okhttp3.logging.HttpLoggingInterceptor
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import java.util.concurrent.TimeUnit
//
//object ApiFactory {
//
//    private const val BASE_URL = "https://services.jnet-secure.com/integrations/gateway-svc/v1/"
////    private const val BASE_URL = "https://api.stage.sentry.tokend.io/integrations/gateway-svc/v1/"
//
//    fun createApiService(): CryptoApi {
//
//        val client = OkHttpClient.Builder().apply {
//            if (BuildConfig.DEBUG) {
//                addNetworkInterceptor(
//                    HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
//                )
//               // addNetworkInterceptor(CurlLoggingInterceptor())
//            }
//
//            connectTimeout(60, TimeUnit.SECONDS)
//            readTimeout(30, TimeUnit.SECONDS)
//            writeTimeout(30, TimeUnit.SECONDS)
//        }.build()
//
//        val retrofit = Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .client(client)
//            .build()
//
//        return retrofit.create(CryptoApi::class.java)
//    }
//}