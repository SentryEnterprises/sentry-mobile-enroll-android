package com.secure.jnet.wallet.domain

import com.secure.jnet.wallet.data.nfc.NfcActionResult

interface CardWallet {

    fun verifyPin(pinCode: String): NfcActionResult.VerifyPinResult

    fun changePin(oldPinCode: String, newPinCode: String): NfcActionResult.ChangePinResult

    fun enrollFinger(
        onBiometricProgressChanged: (Int) -> Unit
    ): NfcActionResult.BiometricEnrollmentResult

    fun verifyBiometric(): NfcActionResult.VerifyBiometricResult

    fun getEnrollmentStatus(pinCode: String): NfcActionResult.EnrollmentStatusResult
}