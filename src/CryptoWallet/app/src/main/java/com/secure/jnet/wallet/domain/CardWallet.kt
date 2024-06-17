package com.secure.jnet.wallet.domain

import com.secure.jnet.wallet.data.nfc.NfcActionResult

interface CardWallet {

  //  fun resetWallet(): NfcActionResult.ResetWalletResult

 //   fun getWalletStatus(pinCode: String): NfcActionResult.GetCardStatusResult

 //   fun getWalletStatusPin(pinCode: String): NfcActionResult.GetCardStatusResult

//    fun createWallet(
//        pinCode: String,
//        wordsCount: Int,
//        progressListener: ProgressListener? = null,
//    ): NfcActionResult.CreateWalletResult
//
//    fun restoreWallet(
//        pinCode: String,
//        mnemonic: String,
//        progressListener: ProgressListener? = null,
//    ): NfcActionResult.RestoreWalletResult

    fun verifyPin(pinCode: String): NfcActionResult.VerifyPinResult

    fun changePin(oldPinCode: String, newPinCode: String): NfcActionResult.ChangePinResult

    fun enrollFinger(
        onBiometricProgressChanged: (Int) -> Unit
    ): NfcActionResult.BiometricEnrollmentResult

    fun verifyBiometric(): NfcActionResult.VerifyBiometricResult

//    fun createBitcoinTransaction(
//        pinCode: String,
//        inputs: String,
//        inputsCount: Int,
//        outputs: String,
//        outputsCount: Int,
//    ): String
//
//    fun createEthereumTransaction(
//        pinCode: String,
//        chainId: ByteArray,
//        nonce: ByteArray,
//        maxPriorityFeePerGas: ByteArray,
//        maxFeePerGas: ByteArray,
//        gasLimit: ByteArray,
//        amount: ByteArray,
//        addressTo: ByteArray
//    ): String

    fun getEnrollmentStatus(pinCode: String): NfcActionResult.EnrollmentStatusResult
}

fun interface ProgressListener {
    fun onProgressUpdated(progress: Int)
}