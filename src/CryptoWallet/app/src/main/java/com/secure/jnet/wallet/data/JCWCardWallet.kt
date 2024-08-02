package com.secure.jnet.wallet.data

import com.secure.jnet.jcwkit.JCWKit
import com.secure.jnet.jcwkit.NonNativeSmartCardApduCallback
import com.secure.jnet.jcwkit.SmartCardApduCallback
import com.secure.jnet.jcwkit.models.BiometricMode
import com.secure.jnet.wallet.data.nfc.NfcActionResult
import com.secure.jnet.wallet.domain.CardWallet
import com.secure.jnet.wallet.util.PIN_BIOMETRIC
import javax.inject.Inject
import javax.inject.Singleton

const val useNonNative = false

@Singleton
class JCWCardWallet @Inject constructor(
    private val callback: SmartCardApduCallback,
    private val nonNativeCallback: NonNativeSmartCardApduCallback,
    private val jcwKit: JCWKit,
) : CardWallet {

    override fun getEnrollmentStatus(pinCode: String): NfcActionResult.EnrollmentStatusResult {
        jcwKit.initEnrollSdk(pinCode, callback)

        val status = if (useNonNative) {
            jcwKit.getEnrollStatus(nonNativeCallback)
        } else {
            jcwKit.getEnrollStatus()
        }

        return NfcActionResult.EnrollmentStatusResult(
            maxFingerNumber = status.maxFingerNumber,
            enrolledTouches = status.enrolledTouches,
            remainingTouches = status.remainingTouches,
            biometricMode = status.biometricMode
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