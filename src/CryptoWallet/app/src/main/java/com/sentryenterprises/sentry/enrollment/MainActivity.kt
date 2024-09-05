package com.sentryenterprises.sentry.enrollment

import android.app.Activity
import android.nfc.NfcAdapter
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.activity.compose.setContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import timber.log.Timber
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sentryenterprises.sentry.enrollment.Screen.Enroll
import com.sentryenterprises.sentry.enrollment.Screen.GetCardState
import com.sentryenterprises.sentry.enrollment.Screen.Reset
import com.sentryenterprises.sentry.enrollment.Screen.Settings
import com.sentryenterprises.sentry.enrollment.Screen.Verify
import com.sentryenterprises.sentry.enrollment.Screen.VersionInfo
import com.sentryenterprises.sentry.enrollment.home.lock.VerifyScreen
import com.sentryenterprises.sentry.enrollment.cardState.GetCardStateScreen
import com.sentryenterprises.sentry.enrollment.reset.ResetScreen
import com.sentryenterprises.sentry.enrollment.settings.SettingsScreen
import com.sentryenterprises.sentry.enrollment.versioninfo.VersionInfoScreen
import kotlinx.serialization.Serializable

sealed class Screen {

    @Serializable
    data object GetCardState : Screen()

    @Serializable
    data object Settings : Screen()

    @Serializable
    data object VersionInfo : Screen()

    @Serializable
    data object Reset : Screen()

    @Serializable
    data object Enroll : Screen()

    @Serializable
    data object Verify : Screen()
}


class MainActivity : ComponentActivity() {

    private val nfcViewModel: NfcViewModel by viewModels()

    private var nfcAdapter: NfcAdapter? = null

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.plant(Timber.DebugTree())

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        setContent {
            SentryTheme {

                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = GetCardState,
                    enterTransition = { EnterTransition.None },
                    exitTransition = { ExitTransition.None },
                ) {
                    composable<GetCardState> {
                        GetCardStateScreen(
                            nfcViewModel = nfcViewModel,
                            onNavigate = { navController.navigate(it) }
                        )
                    }
                    composable<Settings> {
                        SettingsScreen(
                            nfcViewModel = nfcViewModel,
                            onNavigate = { navController.navigate(it) }
                        )
                    }
                    composable<VersionInfo> {
                        VersionInfoScreen(
                            nfcViewModel = nfcViewModel,
                            onNavigate = { navController.navigate(it) }
                        )
                    }
                    composable<Verify> {
                        VerifyScreen(
                            nfcViewModel = nfcViewModel,
                            onNavigate = { navController.navigate(it) }
                        )
                    }
                    composable<Enroll> {
                        EnrollScreen(
                            nfcViewModel = nfcViewModel,
                            onNavigate = { navController.navigate(it) }
                        )
                    }
                    composable<Reset> {
                        ResetScreen(
                            nfcViewModel = nfcViewModel,
                            onNavigate = { navController.navigate(it) }
                        )
                    }
                }
            }

        }

    }

    override fun onResume() {
        super.onResume()
        Timber.d("Getting NFC Adapter")
        nfcAdapter = NfcAdapter.getDefaultAdapter(this).also {
            enableReaderMode(it)
        }
        Timber.d("NFC Adapter: $nfcAdapter")

    }

    override fun onPause() {
        disableReaderMode()
        super.onPause()
    }

    private fun enableReaderMode(nfcAdapter: NfcAdapter) {
        val options = Bundle().apply {
            // Work around for some broken Nfc firmware implementations that poll the card too fast
            putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 250) // default is 125
        }

        // Enable ReaderMode for all types of card and disable platform sounds
        nfcAdapter.enableReaderMode(
            this,
            { tag -> nfcViewModel.onTagDiscovered(tag) },
            NfcAdapter.FLAG_READER_NFC_A or
                    NfcAdapter.FLAG_READER_NFC_B or
                    NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK or
                    NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS,
            options
        )
    }

    private fun disableReaderMode() {
        nfcAdapter?.disableReaderMode(this)
        nfcAdapter = null
    }

}

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF008EEC),
    background = Color(0xFF000000),
    onPrimary = Color(0xFFFFFFFF),
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF004ED5),
    background = Color(0xFFFFFBFE),
    onPrimary = Color(0xFF000000),
)

@Composable
fun SentryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }


    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}