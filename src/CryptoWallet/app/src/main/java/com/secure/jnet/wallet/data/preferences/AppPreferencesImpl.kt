package com.secure.jnet.wallet.data.preferences

import android.content.Context
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

    override var darkModeEnabled: Boolean
        // Don't forget to change splash screen background color when changing def value
        get() = preferences.getBoolean(DARK_MODE_ENABLED, true)
        set(value) = preferences.edit { it.putBoolean(DARK_MODE_ENABLED, value) }

    private companion object {
        private const val APP_PREFS = "appPrefs"
        private const val DARK_MODE_ENABLED = "darkModeEnabled"
    }
}