package com.sentryenterprises.sentry.sdk.models

/**
Indicates the card's biometric mode.
 */
sealed class BiometricMode {
    // the card is in enrollment mode and will accept fingerprint enrollment commands
    data object Enrollment : BiometricMode()

    // the card is in verification mode
    data object Verification : BiometricMode()
}

/**
Encapsulates the information returned from querying the card for its enrollment status.
 */
data class BiometricEnrollmentStatus(
    /// Usually 1, due to only 1 finger can be saved on the card for now.
    val maximumFingers: Byte,

    /// Indicates the number of currently enrolled touches (in the range 0 - 6).
    val enrolledTouches: Byte,

    /// Indicates the number of touches remaining to be enrolled (in the range 0 - 6).
    val remainingTouches: Byte,

    /// Indicates the card's enrollment mode (either available for enrollment or ready to verify fingerprints).
    val mode: BiometricMode,
)