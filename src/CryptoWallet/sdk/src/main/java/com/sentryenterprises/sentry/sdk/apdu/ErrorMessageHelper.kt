package com.sentryenterprises.sentry.sdk.apdu

import com.sentryenterprises.sentry.sdk.apdu.APDUResponseCode
import com.sentryenterprises.sentry.sdk.presentation.SentrySDKError
import com.sentryenterprises.sentry.sdk.presentation.SentrySDKError.ApduCommandError
import com.sentryenterprises.sentry.sdk.presentation.SentrySDKError.BioVerifyAppletWrongVersion
import com.sentryenterprises.sentry.sdk.presentation.SentrySDKError.BioverifyAppletNotInstalled
import com.sentryenterprises.sentry.sdk.presentation.SentrySDKError.CardOSVersionError
import com.sentryenterprises.sentry.sdk.presentation.SentrySDKError.ConnectedWithoutTag
import com.sentryenterprises.sentry.sdk.presentation.SentrySDKError.CvmAppletBlocked
import com.sentryenterprises.sentry.sdk.presentation.SentrySDKError.CvmAppletError
import com.sentryenterprises.sentry.sdk.presentation.SentrySDKError.CvmAppletNotAvailable
import com.sentryenterprises.sentry.sdk.presentation.SentrySDKError.DataSizeNotSupported
import com.sentryenterprises.sentry.sdk.presentation.SentrySDKError.EnrollCodeDigitOutOfBounds
import com.sentryenterprises.sentry.sdk.presentation.SentrySDKError.EnrollCodeLengthOutOfBounds
import com.sentryenterprises.sentry.sdk.presentation.SentrySDKError.EnrollModeNotAvailable
import com.sentryenterprises.sentry.sdk.presentation.SentrySDKError.EnrollVerificationError
import com.sentryenterprises.sentry.sdk.presentation.SentrySDKError.EnrollmentStatusBufferTooSmall
import com.sentryenterprises.sentry.sdk.presentation.SentrySDKError.IncorrectTagFormat
import com.sentryenterprises.sentry.sdk.presentation.SentrySDKError.InvalidAPDUCommand
import com.sentryenterprises.sentry.sdk.presentation.SentrySDKError.KeyGenerationError
import com.sentryenterprises.sentry.sdk.presentation.SentrySDKError.SecureChannelInitializationError
import com.sentryenterprises.sentry.sdk.presentation.SentrySDKError.SecureCommunicationNotSupported
import com.sentryenterprises.sentry.sdk.presentation.SentrySDKError.SharedSecretExtractionError

fun Throwable?.getDecodedMessage() = when (this) {
    is SentrySDKError -> this.localizedErrorMessage()
    else -> this?.localizedMessage ?: "Unknown error $this"
}

fun SentrySDKError.localizedErrorMessage() = when (this) {
    is EnrollCodeDigitOutOfBounds -> "Individual enroll code digits must be in the range 0 - 9."
    is EnrollCodeLengthOutOfBounds -> "The enroll code must be between 4 - 6 characters in length."
    is IncorrectTagFormat -> "The card was scanned correctly, but it does not appear to be the correct format."
    is ApduCommandError -> {
        when (code) {
            APDUResponseCode.NO_MATCH_FOUND.value ->
                "(6300) No match found."

            APDUResponseCode.ENROLL_CODE_INCORRECT_THREE_TRIES_REMAIN.value ->
                "The enroll code on the scanned card does not match the enroll code set in the application. Open the phone Settings app, navigate to Sentry Enroll, and set the enroll code to match the enroll code on the card." +
                        "\n\n(0x63C3) Enroll code incorrect, three tries remaining."

            APDUResponseCode.ENROLL_CODE_INCORRECT_TWO_TRIES_REMAIN.value ->
                "The enroll code on the scanned card does not match the enroll code set in the application. Open the phone Settings app, navigate to Sentry Enroll, and set the enroll code to match the enroll code on the card." +
                        "\n\n(0x63C2) Enroll code incorrect, two tries remaining."

            APDUResponseCode.ENROLL_CODE_INCORRECT_ONE_TRIES_REMAIN.value ->
                "The enroll code on the scanned card does not match the enroll code set in the application. Open the phone Settings app, navigate to Sentry Enroll, and set the enroll code to match the enroll code on the card." +
                        "\n\n(0x63C1) Enroll code incorrect, one try remaining."

            APDUResponseCode.ENROLL_CODE_INCORRECT_ZERO_TRIES_REMAIN.value ->
                "The enroll code on the scanned card does not match the enroll code set in the application. Open the phone settings app, navigate to Sentry Enroll, and set the enroll code to match the enroll code on the card. " +
                        "Afterward, use the appropriate script file to reset your card.\n\n(0x63C0) Enroll code incorrect, zero tries remaining."

            APDUResponseCode.WRONG_LENGTH.value ->
                "(0x6700) Length parameter incorrect."

            APDUResponseCode.FORMAT_NOT_COMPLIANT.value ->
                "(0x6701) Command APDU format not compliant with this standard."

            APDUResponseCode.LENGTH_VALUE_NOT_THE_ONE_EXPECTED.value ->
                "(0x6702) The length parameter value is not the one expected."

            APDUResponseCode.COMMUNICATION_FAILURE.value ->
                "There was an error communicating with the card. Move the card away from the phone and try again.\n\n(6741) Non-specific communication failure."

            APDUResponseCode.FINGER_REMOVED.value ->
                "The finger was removed from the sensor before the scan completed. Please try again.\n\n(6745) Finger removed before scan completed."

            APDUResponseCode.POOR_IMAGE_QUALITY.value ->
                "The image scanned by the sensor was poor quality, please try again.\n\n(6747) Poor image quality."

            APDUResponseCode.USER_TIMEOUT_EXPIRED.value ->
                "No finger was detected on the sensor. Please try again.\n\n(6748) User timeout expired."

            APDUResponseCode.HOST_INTERFACE_TIMEOUT_EXPIRED.value ->
                "The card did not respond in the expected amount of time. Please try again.\n\n(6749) Host interface timeout expired."

            APDUResponseCode.CONDITION_OF_USE_NOT_SATISFIED.value ->
                "(6985) Conditions of use not satisfied."

            APDUResponseCode.NOT_ENOUGH_MEMORY.value ->
                "(6A84) Not enough memory space in the file."

            APDUResponseCode.WRONG_PARAMETERS.value ->
                "(0x6B00) Parameter bytes are invalid."

            APDUResponseCode.INSTRUCTION_BYTE_NOT_SUPPORTED.value ->
                "(0x6D00) Instruction byte not supported or invalid."

            APDUResponseCode.CLASS_BYTE_NOT_SUPPORTED.value ->
                "(0x6E00) Class byte not supported or invalid."

            APDUResponseCode.COMMAND_ABORTED.value ->
                "(6F00) Command aborted – more exact diagnosis not possible (e.g. operating system error)."

            APDUResponseCode.NO_PRECISE_DIAGNOSIS.value ->
                "An error occurred while communicating with the card. Move the card away from the phone and try again.\n\n(0x6F87) No precise diagnosis."

            APDUResponseCode.CARD_DEAD.value ->
                "(6FFF) Card dead (overuse)."

            APDUResponseCode.CALIBRATION_ERROR.value -> "(6744) The fingerprint sensor is returning a calibration error."

            else -> "Unknown Error Code: $code"
        }

    }

    is SecureCommunicationNotSupported -> "Applets on the scanned card do not support encryption. Please open Settings and turn the Secure Communication option off, then try again."
    is DataSizeNotSupported -> "Unable to store data to SentryCard: maximum size supported is 2048 bytes."
    is CvmAppletNotAvailable -> "Unable to initialize the CVM applet on the SentryCard."
    is CvmAppletBlocked -> "The CVM applet on the SentryCard is blocked."
    is CvmAppletError -> "The biometric verification attempt failed to respond properly. Please try again.\n\nError Code: $code"
    is BioverifyAppletNotInstalled -> "The SentryCard does not contain the BioVerify applet. This applet is required. Please run the applet install script to install the required applets."
    is EnrollModeNotAvailable -> "The SentryCard is already enrolled. To re-enroll, go into Options and reset biometric enrollment data."
    is EnrollVerificationError -> "The system was unable to verify that the enrolled fingerprints match the finger on the sensor. Please restart enrollment and try again.\\n\\n(6300) No match found."
    is BioVerifyAppletWrongVersion -> "This SentryCard has an unsupported version of the BioVerify applet installed."
    is EnrollmentStatusBufferTooSmall -> "The buffer returned from querying the card for its biometric enrollment status was unexpectedly too small."
    is InvalidAPDUCommand -> "The buffer used was not a valid `APDU` command."
    is ConnectedWithoutTag -> "NFC connection to card exists, but no tag."
    is SecureChannelInitializationError -> "Unable to initialize secure communication channel."
    is CardOSVersionError -> "Unexpected return value from querying card for OS version."
    is KeyGenerationError -> "Key generation error."
    is SharedSecretExtractionError -> "Shared secret extract error."
    SentrySDKError.InvalidFingerIndex -> "Unexpected finger index"
    is SentrySDKError.UnsupportedEnrollAppletVersion -> {
        "Unsupported enrollment version $version"
    }
}