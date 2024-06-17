package com.secure.jnet.wallet.data

import com.secure.jnet.jcwkit.JCWKit
import com.secure.jnet.jcwkit.SmartCardApduCallback
//import com.secure.jnet.jcwkit.models.AccountDTO
//import com.secure.jnet.jcwkit.models.AccountStatus
import com.secure.jnet.jcwkit.models.BiometricMode
//import com.secure.jnet.jcwkit.models.Bip
//import com.secure.jnet.wallet.data.mappers.WalletStatusDataModelMapper
import com.secure.jnet.wallet.data.nfc.NfcActionResult
import com.secure.jnet.wallet.domain.CardWallet
//import com.secure.jnet.wallet.domain.ProgressListener
////import com.secure.jnet.wallet.domain.models.AccountEntity
//import com.secure.jnet.wallet.domain.models.enums.CryptoCurrency
//import com.secure.jnet.wallet.domain.models.enums.WalletStatus
import com.secure.jnet.wallet.util.PIN_BIOMETRIC
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JCWCardWallet @Inject constructor(
    private val callback: SmartCardApduCallback,
    private val testNet: Boolean,
    private val jcwKit: JCWKit,
) : CardWallet {

    override fun getEnrollmentStatus(pinCode: String): NfcActionResult.EnrollmentStatusResult {
        jcwKit.initEnrollSdk(pinCode, callback)

        val status = jcwKit.getEnrollStatus()

        return NfcActionResult.EnrollmentStatusResult(
            maxFingerNumber = status.maxFingerNumber,
            enrolledTouches = status.enrolledTouches,
            remainingTouches = status.remainingTouches,
            biometricMode = status.biometricMode
        )
    }

    override fun verifyPin(pinCode: String): NfcActionResult.VerifyPinResult {
        jcwKit.initWalletSdk(callback)

        val verifyPin = jcwKit.verifyPin(pinCode)

        jcwKit.deinitWalletSdk()

        return NfcActionResult.VerifyPinResult(
            verifyPin
        )
    }

    override fun changePin(
        oldPinCode: String,
        newPinCode: String
    ): NfcActionResult.ChangePinResult {
        jcwKit.initWalletSdk(callback)

        jcwKit.verifyPin(oldPinCode)

        jcwKit.storePin(newPinCode)

        jcwKit.deinitWalletSdk()

        return NfcActionResult.ChangePinResult(
            true
        )
    }

    override fun verifyBiometric(): NfcActionResult.VerifyBiometricResult {
        jcwKit.initWalletSdk(callback)

        val walletVerifyCVM = jcwKit.walletVerifyCVM()

        jcwKit.deinitWalletSdk()

        return NfcActionResult.VerifyBiometricResult(walletVerifyCVM.fingerVerified)
    }

    override fun enrollFinger(
        onBiometricProgressChanged: (Int) -> Unit
    ): NfcActionResult.BiometricEnrollmentResult {
        jcwKit.initEnrollSdk(PIN_BIOMETRIC, callback)
        onBiometricProgressChanged.invoke(0)

        val status = jcwKit.getEnrollStatus()

        var remainingTouches = TOTAL_ENROLL_STEPS
        var currentStep = 0

        if (status.biometricMode == BiometricMode.VERIFY_MODE || status.enrolledTouches > 0) {
            val enrollStatus = jcwKit.enrollReprocess()
            remainingTouches = enrollStatus.remainingTouches

            currentStep++
            val progress = TOTAL_ENROLL_STEPS.calculateProgress(currentStep)
            onBiometricProgressChanged.invoke(progress)
        }

        while (remainingTouches > 0) {
            val enrollStatus = jcwKit.enrollProcess()
            remainingTouches = enrollStatus.remainingTouches

            currentStep++
            if (remainingTouches == 0) {
                jcwKit.verifyEnroll()
                onBiometricProgressChanged.invoke(100)
            } else {
                val progress = TOTAL_ENROLL_STEPS.calculateProgress(currentStep)
                onBiometricProgressChanged.invoke(progress)
            }
        }

        jcwKit.deinitEnrollSdk()

        return NfcActionResult.BiometricEnrollmentResult(true)
    }

    private fun Int.calculateProgress(currentStep: Int): Int {
        return (currentStep.toFloat() / this.toFloat() * 100).toInt()
    }

    private companion object {
        private const val TOTAL_ENROLL_STEPS = 6
    }
}