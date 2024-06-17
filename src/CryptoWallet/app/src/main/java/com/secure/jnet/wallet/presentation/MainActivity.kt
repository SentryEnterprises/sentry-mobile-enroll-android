package com.secure.jnet.wallet.presentation

import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import com.secure.jnet.wallet.R
//import com.secure.jnet.wallet.data.JCWCardWalletManager
import com.secure.jnet.wallet.domain.interactor.PreferencesInteractor
import com.secure.jnet.wallet.presentation.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity(), NfcAdapter.ReaderCallback {

    @Inject
    lateinit var preferencesInteractor: PreferencesInteractor

//    @Inject
//    lateinit var jcwCardWalletManager: JCWCardWalletManager

    private val nfcViewModel: NfcViewModel by viewModels()

    private var nfcAdapter: NfcAdapter? = null

    private var timestamp: Long = 0L

    private lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (preferencesInteractor.darkModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }

        setContentView(R.layout.activity_main)

        handler = Handler(Looper.getMainLooper())
    }

//    override fun onUserInteraction() {
//        super.onUserInteraction()
//
//        startAutoLockTimer()
//    }

    override fun onResume() {
        super.onResume()
        Timber.d("Getting NFC Adapter")
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        Timber.d("NFC Adapter: $nfcAdapter")

        enableReaderMode()

//        if (timestamp != 0L && System.currentTimeMillis() - timestamp > BACKGROUND_LOCK_TIMEOUT) {
//            navigateToLockScreen()
//        }

 //       startAutoLockTimer()
    }

    override fun onPause() {
        disableReaderMode()

        timestamp = System.currentTimeMillis()

 //       stopAutoLockTimer()
        super.onPause()
    }

    private fun enableReaderMode() {
        if (nfcAdapter != null) {
            val options = Bundle().apply {
                // Work around for some broken Nfc firmware implementations that poll the card too fast
                putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 250) // default is 125
            }

            // Enable ReaderMode for all types of card and disable platform sounds
            nfcAdapter!!.enableReaderMode(
                this,
                this,
                NfcAdapter.FLAG_READER_NFC_A or
                        NfcAdapter.FLAG_READER_NFC_B or
                        NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK or
                        NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS,
                options
            )
        }
    }

    private fun disableReaderMode() {
        nfcAdapter?.disableReaderMode(this)
        nfcAdapter = null
    }

    override fun onTagDiscovered(tag: Tag?) {
        nfcViewModel.onTagDiscovered(tag)

//        jcwCardWalletManager.onTagDiscovered(tag)
    }
//
//    private fun startAutoLockTimer() {
//        handler.removeCallbacksAndMessages(null)
//
//        handler.postDelayed({
//            navigateToLockScreen()
//
//            startAutoLockTimer()
//        }, INACTIVITY_LOCK_TIMEOUT)
//    }
//
//    private fun stopAutoLockTimer() {
//        handler.removeCallbacksAndMessages(null)
//    }
//
//    private fun isScreenNeedLock(): Boolean {
//        when (findNavController(R.id.mainContainer).currentDestination?.id) {
//            R.id.splashFragment,
//            R.id.attachCardFragment,
//            R.id.protectWalletFragment,
//            R.id.restoreWalletBeginFragment,
//            R.id.createPinFragment,
//            R.id.biometricBeginFragment,
//            R.id.biometricDescriptionFragment,
//            R.id.biometricTutorialFragment,
//            R.id.biometricFingerEnrollFragment,
//            R.id.biometricDoneFragment,
//            R.id.recoverySeedFragment,
//            R.id.restoreWalletFragment,
//            R.id.restoreWalletSuccessFragment,
//            R.id.creatingWalletFragment,
//            R.id.seedTutorialFragment,
//            R.id.seedAgreementFragment,
//            R.id.seedShowPhraseFragment,
//            R.id.seedCheckFragment,
//            R.id.seedDoneFragment,
//            R.id.onboardingFragment,
//            -> return false
//        }
//
//        return true
//    }

//    private fun navigateToLockScreen() {
//        if (findNavController(R.id.mainContainer).currentDestination?.id == R.id.lockFragment
//            || !isScreenNeedLock()
//        ) {
//            return
//        }
//
//        findNavController(R.id.mainContainer).navigate(R.id.action_global_lockFragment)
//    }
//
//    private companion object {
//        private const val BACKGROUND_LOCK_TIMEOUT: Long = 5 * 60 * 1000 // 5 minutes in milliseconds
//        private const val INACTIVITY_LOCK_TIMEOUT: Long = 1 * 60 * 1000 // 1 minute in milliseconds
//    }
}