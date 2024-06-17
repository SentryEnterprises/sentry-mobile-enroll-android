package com.secure.jnet.jcwkit.models

data class WalletStatusDTO(
    val gwlcs: GWLCS,
    val wpsm: WPSM,
    val wssm: WSSM,
)

// Global wallet lifecycle state
data class GWLCS(
    val persoStart: Boolean,
    val persoReady: Boolean,
    val persoAcc: Boolean,
    val persoDone: Boolean,
    val selectable: Boolean,
    val walletRecovery: Boolean,
    val walletCreation: Boolean,
)

// Wallet personalization state machine
data class WPSM(
    val setupPhase: Boolean,
    val pinSetupSuccessful: Boolean,
    val securityViolation: Boolean,
)

// Wallet security state machine
data class WSSM(
    val pinAuthFailure: Boolean,
    val pinAuthSuccessful: Boolean,
    val pinAuthRequested: Boolean,
)