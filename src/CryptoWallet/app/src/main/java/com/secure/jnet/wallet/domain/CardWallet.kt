package com.secure.jnet.wallet.domain

import com.secure.jnet.wallet.data.nfc.NfcActionResult

interface CardWallet {

    fun enrollFinger(
        onBiometricProgressChanged: (Int) -> Unit
    ): NfcActionResult.BiometricEnrollmentResult

    fun verifyBiometric(): NfcActionResult.VerifyBiometricResult

    fun versionInformation(): NfcActionResult.VersionInformationResult

    fun getEnrollmentStatus(pinCode: String): NfcActionResult.EnrollmentStatusResult
}