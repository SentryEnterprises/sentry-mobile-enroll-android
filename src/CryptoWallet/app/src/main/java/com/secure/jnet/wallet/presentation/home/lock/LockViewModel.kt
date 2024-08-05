package com.secure.jnet.wallet.presentation.home.lock

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.secure.jnet.wallet.data.nfc.NfcActionResult
import com.secure.jnet.wallet.util.SingleLiveEvent

class LockViewModel() : ViewModel() {

    private val _showNfcError = SingleLiveEvent<String>()
    val showNfcError: LiveData<String> = _showNfcError

    private val _isVerified = SingleLiveEvent<Boolean>()
    val isVerified: LiveData<Boolean> = _isVerified

    fun processNfcActionResult(nfcActionResult: NfcActionResult) {
        when (nfcActionResult) {
            is NfcActionResult.ErrorResult -> {
                _showNfcError.value = nfcActionResult.error
            }

//            is NfcActionResult.VerifyPinResult -> {
//                if (nfcActionResult.isPinCorrect) {
//                    _navigateBack.value = true
//                }
//            }

            is NfcActionResult.VerifyBiometricResult -> {
                if (nfcActionResult.isBiometricCorrect) {
                    _isVerified.value = true
                }
            }

            else -> {
                throw IllegalStateException("$nfcActionResult nfc action result is not handled")
            }
        }
    }
}