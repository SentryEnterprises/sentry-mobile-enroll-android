package com.secure.jnet.wallet.di

import com.secure.jnet.jcwkit.JCWKitUtils
//import com.secure.jnet.wallet.data.remote.ApiFactory
import com.secure.jnet.wallet.data.remote.CryptoApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class CryptoModule {

//    @Singleton
//    @Provides
//    fun provideCryptoApi(): CryptoApi {
//        return ApiFactory.createApiService()
//    }

    @Provides
    fun provideJCWKitUtils(): JCWKitUtils {
        return JCWKitUtils()
    }
}