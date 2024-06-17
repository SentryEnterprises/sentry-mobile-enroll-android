package com.secure.jnet.wallet.di

import com.secure.jnet.wallet.data.preferences.AppPreferencesImpl
import com.secure.jnet.wallet.domain.preferences.AppPreferences
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module(
    includes = [
        CryptoModule::class,
        NfcModule::class,
        RepositoryModule::class,
    ]
)
@InstallIn(SingletonComponent::class)
abstract class ApplicationModule {

    @Binds
    @Singleton
    abstract fun provideAppPreferences(
        appPreferencesImpl: AppPreferencesImpl
    ): AppPreferences

    companion object {
        @Provides
        fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO
    }
}