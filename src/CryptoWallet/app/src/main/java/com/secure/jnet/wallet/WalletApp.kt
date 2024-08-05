package com.secure.jnet.wallet

import android.app.Application
import timber.log.Timber

open class WalletApp : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}