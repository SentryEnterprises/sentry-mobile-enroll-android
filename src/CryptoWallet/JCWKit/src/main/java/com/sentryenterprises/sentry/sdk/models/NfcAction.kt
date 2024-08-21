package com.sentryenterprises.sentry.sdk.models

import com.sentryenterprises.sentry.sdk.models.BiometricMode


sealed class NfcAction {
    data object VerifyBiometric : NfcAction()
    data object GetVersionInformation : NfcAction()
    data object ResetBiometricData : NfcAction()
    data object EnrollFingerprint : NfcAction()
    data class GetEnrollmentStatus(
        val pinCode: String
    ) : NfcAction()
}

sealed class NfcActionResult {

    data class ErrorResult(
        val error: String,
    ) : NfcActionResult()

//    data class VerifyPinResult(
//        val isPinCorrect: Boolean,
//    ) : NfcActionResult()

    data class BiometricEnrollmentResult(
        val isStatusEnrollment: Boolean,
    ) : NfcActionResult()

    sealed class ResetBiometricsResult(
    ) : NfcActionResult() {
        data object Success : ResetBiometricsResult()
        data class Failed(val reason: String) : ResetBiometricsResult()
    }

    data class VerifyBiometricResult(
        val isBiometricCorrect: Boolean,
    ) : NfcActionResult()

    data class VersionInformationResult(
        val osVersion: VersionInfo,
        val enrollAppletVersion: VersionInfo,
        val cvmAppletVersion: VersionInfo,
        val verifyAppletVersion : VersionInfo,
    ) : NfcActionResult()

    data class EnrollmentStatusResult(
        val maxFingerNumber: Int,
        val enrolledTouches: Int,
        val remainingTouches: Int,
        val biometricMode: BiometricMode
    ) : NfcActionResult()
}