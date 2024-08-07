package com.secure.jnet.wallet.presentation

import android.nfc.TagLostException
import com.secure.jnet.jcwkit.JCWIOException
import com.sentryenterprises.sentry.sdk.apdu.APDUResponseCode

class ErrorMessageHelper(
    val exception: Exception?
) {
    fun getErrorMessage(): String {
        if (exception == null) {
            return "Unable to construct error message: No exception specified."
        }

        if (exception is TagLostException) {
            return "Communication with the card was interrupted or timed out. Move the card away from the phone and try again.\n\n${exception.message}"
        }

        if (exception is JCWIOException) {
            return when(exception.errorCode) {
                APDUResponseCode.NO_MATCH_FOUND.value -> "(6300) No match found."
                APDUResponseCode.PIN_INCORRECT_THREE_REMAIN.value -> "The enroll code on the scanned card does not match the enroll code set in the application. Open the iPhone Settings app, navigate to Sentry Enroll, and set the enroll code to match the enroll code on the card.\n\n(0x63C3) Enroll code incorrect, three tries remaining."
                APDUResponseCode.PIN_INCORRECT_TWO_REMAIN.value -> "The enroll code on the scanned card does not match the enroll code set in the application. Open the iPhone Settings app, navigate to Sentry Enroll, and set the enroll code to match the enroll code on the card.\n\n(0x63C2) Enroll code incorrect, two tries remaining."
                APDUResponseCode.PIN_INCORRECT_ONE_REMAIN.value -> "The enroll code on the scanned card does not match the enroll code set in the application. Open the iPhone Settings app, navigate to Sentry Enroll, and set the enroll code to match the enroll code on the card.\n\n(0x63C1) Enroll code incorrect, one try remaining."
                APDUResponseCode.PIN_INCORRECT_ZERO_REMAIN.value -> "The enroll code on the scanned card does not match the enroll code set in the application. Open the iPhone Settings app, navigate to Sentry Enroll, and set the enroll code to match the enroll code on the card. Afterward, use the appropriate script file to reset your card.\n\n(0x63C0) Enroll code incorrect, zero tries remaining."
                APDUResponseCode.WRONG_LENGTH.value -> "(0x6700) Length parameter incorrect."
                APDUResponseCode.FORMAT_NOT_COMPLIANT.value -> "(0x6701) Command APDU format not compliant with this standard."
                APDUResponseCode.LENGTH_VALUE_NOT_ONE_EXPECTED.value -> "(0x6702) The length parameter value is not the one expected."
                APDUResponseCode.COMMUNICATION_FAILURE.value -> "There was an error communicating with the card. Move the card away from the phone and try again.\n\n(6741) Non-specific communication failure."
                APDUResponseCode.FINGER_REMOVED.value -> "The finger was removed from the sensor before the scan completed. Please try again.\n\n(6745) Finger removed before scan completed."
                APDUResponseCode.POOR_IMAGE_QUALITY.value -> "The image scanned by the sensor was poor quality, please try again.\n\n(6747) Poor image quality."
                APDUResponseCode.USER_TIMEOUT_EXPIRED.value -> "No finger was detected on the sensor. Please try again.\n\n(6748) User timeout expired."
                APDUResponseCode.HOST_INTERFACE_TIMEOUT_EXPIRED.value -> "No finger was detected on the sensor. Please try again.\n\n(6749) Host interface timeout expired."
                APDUResponseCode.CONDITION_OF_USE_NOT_SATISFIED.value -> "(6985) Conditions of use not satisfied."
                APDUResponseCode.NOT_ENOUGH_MEMORY.value -> "(6A84) Not enough memory space in the file."
                APDUResponseCode.WRONG_PARAMETERS.value -> "(0x6B00) Parameter bytes are invalid."
                APDUResponseCode.INSTRUCTION_BYTE_NOT_SUPPORTED.value -> "(0x6D00) Instruction byte not supported or invalid."
                APDUResponseCode.CLASS_BYTE_NOT_SUPPORTED.value -> "(0x6E00) Class byte not supported or invalid."
                APDUResponseCode.COMMAND_ABORTED.value -> "(6F00) Command aborted – more exact diagnosis not possible (e.g. operating system error)."
                APDUResponseCode.NO_PRECISE_DIAGNOSIS.value -> "An error occurred while communicating with the card. Move the card away from the phone and try again.\n\n(0x6F87) No precise diagnosis."
                APDUResponseCode.CARD_DEAD.value -> "(6FFF) Card dead (overuse)."
                else -> "(${exception.errorCode}) Unknown Error Code"
            }
        }

        return "${exception.message}"
    }
}