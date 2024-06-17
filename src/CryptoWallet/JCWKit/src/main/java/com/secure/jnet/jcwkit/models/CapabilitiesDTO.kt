package com.secure.jnet.jcwkit.models

data class CapabilitiesDTO(
    val isBiometricEnabled: Boolean,
    val isSecureChannelEnabled: Boolean,
    val is1MillionIterationsEnabled: Boolean,
    val isStorePin: Boolean,
    val isPinDisabled: Boolean,
)