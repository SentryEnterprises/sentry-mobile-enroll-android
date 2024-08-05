package com.secure.jnet.wallet.presentation

import android.nfc.NfcAdapter
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatActivity
import com.secure.jnet.wallet.R
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private val nfcViewModel: NfcViewModel by viewModels()

    private var nfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        setContentView(R.layout.activity_main)
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