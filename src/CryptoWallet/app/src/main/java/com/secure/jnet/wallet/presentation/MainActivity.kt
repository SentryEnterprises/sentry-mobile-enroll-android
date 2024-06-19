package com.secure.jnet.wallet.presentation

import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatActivity
import com.secure.jnet.wallet.R
import com.secure.jnet.wallet.domain.interactor.PreferencesInteractor
//import com.secure.jnet.wallet.presentation.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NfcAdapter.ReaderCallback {

    @Inject
    lateinit var preferencesInteractor: PreferencesInteractor

    private val nfcViewModel: NfcViewModel by viewModels()

    private var nfcAdapter: NfcAdapter? = null

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

    override fun onResume() {
        super.onResume()
        Timber.d("Getting NFC Adapter")
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        Timber.d("NFC Adapter: $nfcAdapter")

        enableReaderMode()
    }

    override fun onPause() {
        disableReaderMode()
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
    }
}