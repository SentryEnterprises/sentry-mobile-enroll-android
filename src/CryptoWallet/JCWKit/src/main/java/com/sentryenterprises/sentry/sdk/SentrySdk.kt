package com.sentryenterprises.sentry.sdk

import com.secure.jnet.wallet.presentation.SentrySDKError
import com.sentryenterprises.sentry.sdk.apdu.APDUResponseCode
import com.sentryenterprises.sentry.sdk.biometrics.BiometricsApi
import com.sentryenterprises.sentry.sdk.models.BiometricEnrollmentStatus
import com.sentryenterprises.sentry.sdk.models.BiometricMode
import com.sentryenterprises.sentry.sdk.models.BiometricProgress
import com.sentryenterprises.sentry.sdk.models.NfcIso7816Tag

/**
Entry point for the `SentrySDK` functionality. Provides methods exposing all available functionality.

This class controls and manages an `NFCReaderSession` to communicate with an `NFCISO7816Tag` via `APDU` commands.

The bioverify.cap, com.idex.enroll.cap, and com.jnet.CDCVM.cap applets must be installed on the SentryCard for full access to all functionality of this SDK.
 */
class SentrySdk(
    private val enrollCode: ByteArray,
    private val verboseDebugOutput: Boolean = true,
    private val useSecureCommunication: Boolean = true,

    ) {
    private val biometricsAPI: BiometricsApi =
        BiometricsApi(verboseDebugOutput, useSecureChannel = useSecureCommunication)

//    private var session: NFCReaderSession?
//    private var connectedTag: NFCISO7816Tag?
//    private var callback: ((Result<NFCISO7816Tag, Error>) -> Void)?


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
    fun getEnrollmentStatus(iso7816Tag: NfcIso7816Tag): BiometricEnrollmentStatus {
        var errorDuringSession = false
//        defer {
//            // closes the NFC reader session
//            if errorDuringSession {
//                session?.invalidate(errorMessage: cardCommunicationErrorText)
//            } else {
//                session?.invalidate()
//            }
//        }

        try {
            // establish a connection
//            let isoTag = try await establishConnection()

            // initialize the Enroll applet
            biometricsAPI.initializeEnroll(tag = iso7816Tag, enrollCode = enrollCode)

            // get and return the enrollment status
            val enrollStatus = biometricsAPI.getEnrollmentStatus(tag = iso7816Tag)
            return enrollStatus
        } catch (e: Exception) {
            errorDuringSession = true
            throw e
        }
    }


    fun enrollFinger(
        iso7816Tag: NfcIso7816Tag,
        resetOnFirstCall: Boolean = false,
        onBiometricProgressChanged: (BiometricProgress) -> Unit
    ): BiometricEnrollmentStatus {

        biometricsAPI.initializeEnroll(tag = iso7816Tag, enrollCode = enrollCode)

        val enrollStatus = biometricsAPI.getEnrollmentStatus(tag = iso7816Tag)
        if (enrollStatus == BiometricMode.Verification) {
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
                // scan the finger currently on the sensor
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


//            TODO()
//            resetOnFirstCall = false

            // inform the caller of the step that just finished
//                if (session) {
//                    stepFinished(session, maximumSteps - enrollmentsLeft, maximumSteps)
//                }
        }

        // after all fingerprints are enrolled, perform a verify
//        do {
        try {
            biometricsAPI.verifyEnrolledFingerprint(tag = iso7816Tag)
        } catch (error: SentrySDKError.ApduCommandError) {

            if (error.code == (APDUResponseCode.NO_MATCH_FOUND.value)) {
                // expose a custom error if the verify enrolled fingerprint command didn't find a match
                throw SentrySDKError.EnrollVerificationError
            } else {
                throw SentrySDKError.ApduCommandError(error.code)
            }
        }

        // enrollment is fully completed
//            if (session) {
//                enrollmentComplete(session)
//            }
//        } catch (let error) {
//        errorDuringSession = true
//        if let session = session {
//            connected(session, false)
//        }
//        throw error
//    }

        return BiometricEnrollmentStatus(0, 0, 0, BiometricMode.Enrollment)
    }


}