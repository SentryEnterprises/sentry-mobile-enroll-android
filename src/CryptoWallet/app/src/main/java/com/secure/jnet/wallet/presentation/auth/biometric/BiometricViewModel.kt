package com.secure.jnet.wallet.presentation.auth.biometric

import androidx.lifecycle.LiveData
import com.secure.jnet.wallet.data.nfc.NfcActionResult
import com.secure.jnet.wallet.presentation.base.BaseViewModel
import com.secure.jnet.wallet.util.SingleLiveEvent


class BiometricViewModel : BaseViewModel() {
    private val _showNfcError = SingleLiveEvent<String>()
    val showNfcError: LiveData<String> = _showNfcError

    private val _showBiometricEnrollError = SingleLiveEvent<Boolean>()
    val showBiometricEnrollError: LiveData<Boolean> = _showBiometricEnrollError

    private val _showButtonContainer = SingleLiveEvent<Boolean>()
    val showButtonContainer: LiveData<Boolean> = _showButtonContainer

    fun processNfcActionResult(nfcActionResult: NfcActionResult) {
        when (nfcActionResult) {
            is NfcActionResult.ErrorResult -> {
                _showNfcError.value = nfcActionResult.error
            }

            is NfcActionResult.BiometricEnrollmentResult -> {
                if (nfcActionResult.isSuccess) {
                    _showButtonContainer.value = true
                } else {
                    _showBiometricEnrollError.value = true
                }
            }

            else -> {
                throw IllegalStateException("$nfcActionResult nfc action result is not handled")
            }
        }
    }
}