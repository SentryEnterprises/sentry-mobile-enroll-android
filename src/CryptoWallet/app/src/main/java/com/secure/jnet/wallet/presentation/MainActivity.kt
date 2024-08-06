package com.secure.jnet.wallet.presentation

import android.nfc.NfcAdapter
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.secure.jnet.wallet.data.nfc.NfcAction
import timber.log.Timber
import com.secure.jnet.wallet.presentation.cardState.GetCardStateScreen
import com.secure.jnet.wallet.util.PIN_BIOMETRIC
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.secure.jnet.wallet.presentation.settings.SettingsScreen
import com.secure.jnet.wallet.presentation.settings.VersionInfoScreen

const val NAV_GET_CARD_STATE = "Main"
const val NAV_SETTINGS = "Settings"
const val NAV_VERSION_INFO = "VersionInfo"

class MainActivity : ComponentActivity() {

    private val nfcViewModel: NfcViewModel by viewModels()

    private var nfcAdapter: NfcAdapter? = null

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.plant(Timber.DebugTree())

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        setContent {
            val view = LocalView.current
            SideEffect {
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            }

            MaterialTheme(darkColorScheme()) {

                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = NAV_GET_CARD_STATE,
                    enterTransition = { EnterTransition.None },
                    exitTransition = { ExitTransition.None },
                ) {
                    composable(NAV_GET_CARD_STATE) {
                        GetCardStateScreen(
                            nfcViewModel = nfcViewModel,
                            onNavigate = { navController.navigate(it) })
                    }
                    composable(NAV_SETTINGS) {
                        SettingsScreen(
                            nfcViewModel = nfcViewModel,
                            onNavigate = { navController.navigate(it) })
                    }
                    composable(NAV_VERSION_INFO) {
                        VersionInfoScreen(
                            nfcViewModel = nfcViewModel,
                            onNavigate = { navController.navigate(it) })
                    }
                }
            }

        }

        nfcViewModel.startNfcAction(NfcAction.GetEnrollmentStatus(PIN_BIOMETRIC))
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