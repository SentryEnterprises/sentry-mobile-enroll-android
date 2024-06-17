package com.secure.jnet.wallet.data.preferences

import android.content.Context
//import com.secure.jnet.wallet.domain.models.enums.PrimaryCurrency
import com.secure.jnet.wallet.domain.preferences.AppPreferences
import com.secure.jnet.wallet.util.ext.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppPreferencesImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : AppPreferences {

    private val preferences by lazy {
        context.getSharedPreferences(
            APP_PREFS,
            Context.MODE_PRIVATE
        )
    }

//    override var onboardingPassed: Boolean
//        get() = preferences.getBoolean(ONBOARDING_PASSED, false)
//        set(value) = preferences.edit { it.putBoolean(ONBOARDING_PASSED, value) }

    override var darkModeEnabled: Boolean
        // Don't forget to change splash screen background color when changing def value
        get() = preferences.getBoolean(DARK_MODE_ENABLED, true)
        set(value) = preferences.edit { it.putBoolean(DARK_MODE_ENABLED, value) }

//    override var autoLockTime: Long
//        get() = preferences.getLong(AUTO_LOCK_TIME, 300L)
//        set(value) = preferences.edit { it.putLong(AUTO_LOCK_TIME, value) }

//    override var primaryCurrency: PrimaryCurrency
//        get() {
//            val value = preferences.getString(PRIMARY_CURRENCY, null)
//            return PrimaryCurrency.valueOf(value ?: PrimaryCurrency.FIAT.name)
//        }
//        set(value) = preferences.edit { it.putString(PRIMARY_CURRENCY, value.name) }

    private companion object {
        private const val APP_PREFS = "appPrefs"
 //       private const val ONBOARDING_PASSED = "onboardingPassed"
        private const val DARK_MODE_ENABLED = "darkModeEnabled"
 //       private const val PRIMARY_CURRENCY = "primaryCurrency"
 //       private const val AUTO_LOCK_TIME = "autoLockTime"
    }
}