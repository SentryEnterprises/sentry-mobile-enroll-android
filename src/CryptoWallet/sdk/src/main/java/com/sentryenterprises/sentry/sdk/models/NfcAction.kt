package com.sentryenterprises.sentry.sdk.models

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

    data class BiometricEnrollment(
        val isStatusEnrollment: Boolean,
    ) : NfcActionResult()

    sealed class ResetBiometrics() : NfcActionResult() {
        data object Success : ResetBiometrics()
        data class Failed(val reason: String) : ResetBiometrics()
    }

    data class VerifyBiometric(
        val fingerprintValidation: FingerprintValidation,
    ) : NfcActionResult()

    data class VersionInformation(
        val osVersion: VersionInfo,
        val enrollAppletVersion: VersionInfo,
        val cvmAppletVersion: VersionInfo,
        val verifyAppletVersion : VersionInfo,
    ) : NfcActionResult()

    sealed class EnrollFingerprint(): NfcActionResult() {
        data object Complete : EnrollFingerprint()
        data object Failed : EnrollFingerprint()
    }

}

/**
 * Indicates the results of a fingerprint validation.
 */
enum class FingerprintValidation {
    // The finger on the sensor matches the fingerprints recorded during enrollment.
    MatchValid,

    // The finger on the sensor does not match the fingerprints recorded during enrollment.
    MatchFailed,

    // The card is not enrolled and fingerprint verification cannot be performed.
    NotEnrolled
}