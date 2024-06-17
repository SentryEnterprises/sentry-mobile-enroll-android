package com.secure.jnet.wallet.domain.interactor

//import com.secure.jnet.wallet.domain.models.enums.PrimaryCurrency
import com.secure.jnet.wallet.domain.preferences.AppPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesInteractor @Inject constructor(
    private val appPreferences: AppPreferences,
) {

//    var onboardingPassed: Boolean
//        get() = appPreferences.onboardingPassed
//        set(value) { appPreferences.onboardingPassed = value }
//
//    var autoLockTime: Long
//        get() = appPreferences.autoLockTime
//        set(value) { appPreferences.autoLockTime = value }
//
//    var primaryCurrency: PrimaryCurrency
//        get() = appPreferences.primaryCurrency
//        set(value) { appPreferences.primaryCurrency = value }

    var darkModeEnabled: Boolean
        get() = appPreferences.darkModeEnabled
        set(value) { appPreferences.darkModeEnabled = value }
}