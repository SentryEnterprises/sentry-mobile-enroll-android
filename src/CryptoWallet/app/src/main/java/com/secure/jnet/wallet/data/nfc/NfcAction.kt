package com.secure.jnet.wallet.data.nfc

import com.secure.jnet.jcwkit.models.BiometricMode

sealed class NfcAction {
//    data class VerifyPin(val pinCode: String) : NfcAction()

    data object BiometricEnrollment : NfcAction()

    data object VerifyBiometric : NfcAction()

    data object GetVersionInformation : NfcAction()

    data object ResetBiometricData : NfcAction()

    data class GetEnrollmentStatus(val pinCode: String) : NfcAction()
}

sealed class NfcActionResult {

    data class ErrorResult(
        val error: String,
    ) : NfcActionResult()

//    data class VerifyPinResult(
//        val isPinCorrect: Boolean,
//    ) : NfcActionResult()

    data class BiometricEnrollmentResult(
        val isSuccess: Boolean,
    ) : NfcActionResult()

    data class ResetBiometricsResult(
        val isSuccess: Boolean,
    ) : NfcActionResult()

    data class VerifyBiometricResult(
        val isBiometricCorrect: Boolean,
    ) : NfcActionResult()

    data class VersionInformationResult(
        val version: String,
    ) : NfcActionResult()

    data class EnrollmentStatusResult(
        val maxFingerNumber: Int,
        val enrolledTouches: Int,
        val remainingTouches: Int,
        val biometricMode: BiometricMode
    ) : NfcActionResult()
}