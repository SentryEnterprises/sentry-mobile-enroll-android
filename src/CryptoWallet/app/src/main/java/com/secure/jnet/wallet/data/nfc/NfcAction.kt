package com.secure.jnet.wallet.data.nfc

import com.secure.jnet.jcwkit.models.BiometricMode
//import com.secure.jnet.wallet.data.crypto.models.RawTransactionDTO
//import com.secure.jnet.wallet.domain.models.AccountEntity
//import com.secure.jnet.wallet.domain.models.enums.WalletStatus

sealed class NfcAction {
//    data object ResetWallet : NfcAction()
//    data class GetCardStatus(val pinCode: String) : NfcAction()
//    data class CreateWallet(val pinCode: String, val wordsCount: Int) : NfcAction()
//    data class RestoreWallet(val pinCode: String, val mnemonic: String) : NfcAction()
    data class VerifyPin(val pinCode: String) : NfcAction()
    data class ChangePin(val currentPinCode: String, val newPinCode: String) : NfcAction()
//    data class SignTransaction(val transaction: RawTransactionDTO, val pinCode: String) : NfcAction()

    data object BiometricEnrollment : NfcAction()
    data object VerifyBiometric : NfcAction()

    data class GetEnrollmentStatus(val pinCode: String) : NfcAction()
}

sealed class NfcActionResult {

    data class ErrorResult(
        val error: String,
    ) : NfcActionResult()

//    data object ResetWalletResult : NfcActionResult()
//
//    data class GetCardStatusResult(
//        val walletStatus: WalletStatus,
//        val pinRequired: Boolean,
//        val accounts: List<AccountEntity>,
//    ) : NfcActionResult()
//
//    data class CreateWalletResult(
//        val seed: String,
//        val accounts: List<AccountEntity>,
//    ) : NfcActionResult()
//
//    data class RestoreWalletResult(
//        val accounts: List<AccountEntity>,
//    ) : NfcActionResult()

    data class VerifyPinResult(
        val isPinCorrect: Boolean,
    ) : NfcActionResult()

    data class ChangePinResult(
        val isPinCorrect: Boolean,
    ) : NfcActionResult()

//    data class SignTransactionResult(
//        val signedTx: String,
//    ) : NfcActionResult()

    data class BiometricEnrollmentResult(
        val isSuccess: Boolean,
    ) : NfcActionResult()

    data class VerifyBiometricResult(
        val isBiometricCorrect: Boolean,
    ) : NfcActionResult()

    data class EnrollmentStatusResult(
        val maxFingerNumber: Int,
        val enrolledTouches: Int,
        val remainingTouches: Int,
        val biometricMode: BiometricMode
    ) : NfcActionResult()
}