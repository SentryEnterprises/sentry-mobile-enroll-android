package com.sentryenterprises.sentry.sdk

import com.secure.jnet.wallet.presentation.SentrySDKError
import com.sentryenterprises.sentry.sdk.apdu.APDUResponseCode
import com.sentryenterprises.sentry.sdk.biometrics.BiometricsApi
import com.sentryenterprises.sentry.sdk.models.BiometricMode.Enrollment
import com.sentryenterprises.sentry.sdk.models.BiometricMode.Verification
import com.sentryenterprises.sentry.sdk.models.BiometricProgress
import com.sentryenterprises.sentry.sdk.models.NfcActionResult
import com.sentryenterprises.sentry.sdk.models.NfcActionResult.BiometricEnrollment
import com.sentryenterprises.sentry.sdk.models.NfcIso7816Tag

/**
Entry point for the `SentrySDK` functionality. Provides methods exposing all available functionality.

This class controls and manages an `NFCReaderSession` to communicate with an `NFCISO7816Tag` via `APDU` commands.

The bioverify.cap, com.idex.enroll.cap, and com.jnet.CDCVM.cap applets must be installed on the SentryCard for full access to all functionality of this SDK.
 */
class SentrySdk(
    private val enrollCode: ByteArray,
    verboseDebugOutput: Boolean = true,
    useSecureCommunication: Boolean = true,
) {
    private val biometricsAPI: BiometricsApi = BiometricsApi(
        isDebugOutputVerbose = verboseDebugOutput,
        useSecureChannel = useSecureCommunication
    )


    /**
    Retrieves the biometric fingerprint enrollment status.

    Opens an `NFCReaderSession`, connects to an `NFCISO7816Tag` through this session, and sends `APDU` commands to a java applet running on the connected SentryCard.

    - Returns: A `BiometricEnrollmentStatus` structure containing information on the fingerprint enrollment status.

    This method can throw the following exceptions:
     * `SentrySDKError.enrollCodeLengthOutOfbounds` if `enrollCode` is less than four (4) characters or more than six (6) characters in length.
     * `SentrySDKError.apduCommandError` that contains the status word returned by the last failed `APDU` command.
     * `SentrySDKError.enrollCodeDigitOutOfBounds` if an enroll code digit is not in the range 0-9.
     * `SentrySDKError.incorrectTagFormat` if an NFC session scanned a tag, but it is not an ISO7816 tag.
     * `NFCReaderError` if an error occurred during the NFC session (includes user cancellation of the NFC session).

     */
    fun getEnrollmentStatus(iso7816Tag: NfcIso7816Tag): BiometricEnrollment {
        var errorDuringSession = false

        try {
            biometricsAPI.initializeEnroll(tag = iso7816Tag, enrollCode = enrollCode)

            val enrollStatus = biometricsAPI.getEnrollmentStatus(tag = iso7816Tag)
            return BiometricEnrollment(enrollStatus.mode == Enrollment)
        } catch (e: Exception) {
            errorDuringSession = true
            throw e
        }
    }


    fun enrollFinger(
        iso7816Tag: NfcIso7816Tag,
        resetOnFirstCall: Boolean = false,
        onBiometricProgressChanged: (BiometricProgress) -> Unit
    ): NfcActionResult.EnrollFingerprint {

        biometricsAPI.initializeEnroll(tag = iso7816Tag, enrollCode = enrollCode)

        val enrollStatus = biometricsAPI.getEnrollmentStatus(tag = iso7816Tag)
        if (enrollStatus == Verification) {
            throw SentrySDKError.EnrollModeNotAvailable
        }

        val maximumSteps =
            enrollStatus.enrolledTouches + enrollStatus.remainingTouches

        // if we're resetting, assume we have not yet enrolled anything
        var enrollmentsLeft = if (resetOnFirstCall) {
            maximumSteps
        } else {
            enrollStatus.remainingTouches
        }

        while (enrollmentsLeft > 0) {
            try {
                enrollmentsLeft = if (resetOnFirstCall) {
                    biometricsAPI.resetEnrollAndScanFingerprint(tag = iso7816Tag)
                } else {
                    biometricsAPI.enrollScanFingerprint(tag = iso7816Tag)
                }
                onBiometricProgressChanged(BiometricProgress.Progressing(enrollmentsLeft))

            } catch (e: SentrySDKError.ApduCommandError) {
                if (e.code == APDUResponseCode.POOR_IMAGE_QUALITY.value) {
                    onBiometricProgressChanged(BiometricProgress.Feedback("Poor image quality"))
                } else if (e.code == APDUResponseCode.HOST_INTERFACE_TIMEOUT_EXPIRED.value) {
                    onBiometricProgressChanged(BiometricProgress.Feedback("Timeout limit exceeded"))
                } else {
                    throw e
                }
            }
        }

        try {
            biometricsAPI.verifyEnrolledFingerprint(tag = iso7816Tag)
        } catch (error: SentrySDKError.ApduCommandError) {
            if (error.code == (APDUResponseCode.NO_MATCH_FOUND.value)) {
                throw SentrySDKError.EnrollVerificationError
            } else {
                throw SentrySDKError.ApduCommandError(error.code)
            }
        }

        return NfcActionResult.EnrollFingerprint.Complete
    }

    /**
     * Resets the biometric data recorded on the card. This effectively erases all fingerprint enrollment and puts the card into an unenrolled state.
     *
     * Opens an `NFCReaderSession`, connects to an Nfc tag through this session, and sends `APDU` commands to a java applet running on the connected SentryCard.
     *
     * - Warning: This is for development purposes only! This command only works on development cards, and fails when used on production cards.
     */
    fun resetCard(tag: NfcIso7816Tag): NfcActionResult.ResetBiometrics {
        // reset the biometric data, setting the card into an unenrolled state
        return biometricsAPI.resetBiometricData(tag = tag)
    }


    /**
    Validates that the finger on the fingerprint sensor matches (or does not match) a fingerprint recorded during enrollment.

    Opens an `NFCReaderSession`, connects to an `NFCISO7816Tag` through this session, and sends `APDU` commands to a java applet running on the connected SentryCard.

    This process waits up to five (5) seconds for a finger to be pressed against the sensor. This timeout is (currently) not configurable. If a finger is not detected on the sensor within the
    timeout period, a `SentrySDKError.apduCommandError` is thrown, indicating either a user timeout expiration (0x6748) or a host interface timeout expiration (0x6749).

    - Returns: `True` if the scanned fingerprint matches the one recorded during enrollment, otherwise `false`.

    This method can throw the following exceptions:
     * `SentrySDKError.enrollCodeLengthOutOfbounds` if `enrollCode` is less than four (4) characters or more than six (6) characters in length.
     * `SentrySDKError.apduCommandError` that contains the status word returned by the last failed `APDU` command.
     * `SentrySDKError.enrollCodeDigitOutOfBounds` if an enroll code digit is not in the range 0-9.
     * `SentrySDKError.incorrectTagFormat` if an NFC session scanned a tag, but it is not an ISO7816 tag.
     * `SentrySDKError.cvmAppletNotAvailable` if the CVM applet on the SentryCard could not be initialized.
     * `SentrySDKError.cvmAppletBlocked` if the CVM applet on the SentryCard is blocked (likely requiring a full card reset).
     * `SentrySDKError.cvmAppletError` if the CVM applet returned an unexpected error code.
     * `NFCReaderError` if an error occurred during the NFC session (includes user cancellation of the NFC session).

     */
    fun validateFingerprint(tag: NfcIso7816Tag): NfcActionResult.VerifyBiometric {
        biometricsAPI.initializeVerify(tag = tag)
        return biometricsAPI.getFingerprintVerification(tag = tag)
    }


    fun getCardSoftwareVersions(tag: NfcIso7816Tag): NfcActionResult.VersionInformation {
        val osVersion = biometricsAPI.getCardOSVersion(tag = tag)
        print("OS= $osVersion")

        val verifyVersion = biometricsAPI.getVerifyAppletVersion(tag = tag)
        print("Verify= $verifyVersion")

        val enrollVersion = biometricsAPI.getEnrollmentAppletVersion(tag = tag)
        print("Enroll= $enrollVersion")

        val cvmVersion = biometricsAPI.getCVMAppletVersion(tag = tag)
        print("CVM= $cvmVersion")

        return NfcActionResult.VersionInformation(
            osVersion = osVersion,
            enrollAppletVersion = enrollVersion,
            cvmAppletVersion = cvmVersion,
            verifyAppletVersion = verifyVersion
        )
    }
}