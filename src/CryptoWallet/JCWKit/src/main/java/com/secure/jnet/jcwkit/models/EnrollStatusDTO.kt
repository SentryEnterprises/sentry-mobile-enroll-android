package com.secure.jnet.jcwkit.models

data class EnrollStatusDTO(
    val maxFingerNumber: Int,
    val enrolledTouches: Int,
    val remainingTouches: Int,
    val biometricMode: BiometricMode,
)

enum class BiometricMode {
    ENROLL_MODE,
    VERIFY_MODE,
    UNKNOWN_MODE,
}

fun Int.mapToBiometricMode(): BiometricMode {
    return when(this) {
        0 -> BiometricMode.ENROLL_MODE
        1 -> BiometricMode.VERIFY_MODE
        2 -> BiometricMode.VERIFY_MODE
        else ->  BiometricMode.UNKNOWN_MODE
    }
}