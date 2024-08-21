package com.secure.jnet.wallet.presentation.cardState

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.secure.jnet.wallet.util.SingleLiveEvent
import com.sentryenterprises.sentry.sdk.models.BiometricMode
import com.sentryenterprises.sentry.sdk.models.NfcActionResult

class GetCardStateViewModel : ViewModel() {

    private val _showNfcError = SingleLiveEvent<String>()
    val showNfcError: LiveData<String> = _showNfcError

    private val _showEnrollmentStatus = SingleLiveEvent<Boolean>()
    val showEnrollmentStatus: LiveData<Boolean> = _showEnrollmentStatus

    fun processNfcActionResult(nfcActionResult: NfcActionResult) {
        when (nfcActionResult) {
            is NfcActionResult.ErrorResult -> {
                _showNfcError.value = nfcActionResult.error
            }

            is NfcActionResult.EnrollmentStatusResult -> {
                _showEnrollmentStatus.value = nfcActionResult.biometricMode == BiometricMode.Verification
            }

            else -> {
                throw IllegalStateException("$nfcActionResult nfc action result is not handled")
            }
        }
    }
}