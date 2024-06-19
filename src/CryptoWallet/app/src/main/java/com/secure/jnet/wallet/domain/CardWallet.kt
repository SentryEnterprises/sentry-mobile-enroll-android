package com.secure.jnet.wallet.domain

import com.secure.jnet.wallet.data.nfc.NfcActionResult

interface CardWallet {

    fun enrollFinger(
        onBiometricProgressChanged: (Int) -> Unit
    ): NfcActionResult.BiometricEnrollmentResult

    fun verifyBiometric(): NfcActionResult.VerifyBiometricResult

    fun getEnrollmentStatus(pinCode: String): NfcActionResult.EnrollmentStatusResult
}