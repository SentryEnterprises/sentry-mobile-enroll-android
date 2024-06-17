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

//    override fun resetWallet(): NfcActionResult.ResetWalletResult {
//        jcwKit.initWalletSdk(callback)
//
//        jcwKit.resetWallet()
//
//        return NfcActionResult.ResetWalletResult
//    }

//    override fun getWalletStatus(pinCode: String): NfcActionResult.GetCardStatusResult {
//        jcwKit.initWalletSdk(callback)
//
//        val walletStatusDataModelMapper = WalletStatusDataModelMapper()
//
//        val walletStatus = walletStatusDataModelMapper.mapToEntity(jcwKit.getWalletStatus())
//
//     //   val accounts = mutableListOf<AccountEntity>()
//
////        if (walletStatus == WalletStatus.HAS_ACCOUNT) {
////            val capabilities = jcwKit.getCapabilities()
////
////            if (!capabilities.isPinDisabled && pinCode.isNotEmpty()) {
////                jcwKit.verifyPin(pinCode)
////            } else {
////                jcwKit.walletVerifyCVM()
////            }
////
////            val walletAccounts = getAccounts()
////            accounts.addAll(walletAccounts)
////        }
//
//        jcwKit.deinitWalletSdk()
//
//        return NfcActionResult.GetCardStatusResult(
//            walletStatus,
//            false,
//            accounts
//        )
//    }

//    override fun getWalletStatusPin(pinCode: String): NfcActionResult.GetCardStatusResult {
//        jcwKit.initWalletSdk(callback)
//
//        val walletStatusDataModelMapper = WalletStatusDataModelMapper()
//
//        val walletStatus = walletStatusDataModelMapper.mapToEntity(jcwKit.getWalletStatus())
//
//        val accounts = mutableListOf<AccountEntity>()
//
//        if (walletStatus == WalletStatus.HAS_ACCOUNT) {
//
//            if (pinCode.isNotEmpty()) {
//                jcwKit.verifyPin(pinCode)
//            } else {
//                return NfcActionResult.GetCardStatusResult(
//                    WalletStatus.NOT_INITIALIZED,
//                    true,
//                    accounts
//                )
//            }
//
//            val walletAccounts = getAccounts()
//            accounts.addAll(walletAccounts)
//        }
//
//        jcwKit.deinitWalletSdk()
//
//        return NfcActionResult.GetCardStatusResult(
//            walletStatus,
//            false,
//            accounts
//        )
//    }

//    private fun getAccounts(): List<AccountEntity> {
//        val walletAccounts = mutableListOf<AccountEntity>()
//
//        val accounts = jcwKit.getAccounts()
//
//        accounts.forEachIndexed { index, accountDTO ->
//
//            jcwKit.selectAccount(index)
//
//            val receiveAddress = jcwKit.getReceiveAddress()
//
//            val cryptoCurrency = when (accountDTO.currencyId) {
//                0, 1 -> CryptoCurrency.Bitcoin
//                60 -> CryptoCurrency.Ethereum
//                else -> {
//                    throw IllegalStateException("No such currency id ${accountDTO.currencyId}")
//                }
//            }
//            walletAccounts.add(
//                AccountEntity(
//                    cryptoCurrency,
//                    receiveAddress,
//                    accountDTO.nickname
//                )
//            )
//        }
//
//        return walletAccounts
//    }

//    override fun createWallet(
//        pinCode: String,
//        wordsCount: Int,
//        progressListener: ProgressListener?,
//    ): NfcActionResult.CreateWalletResult {
//        progressListener?.onProgressUpdated(0)
//
//        jcwKit.initWalletSdk(callback)
//        progressListener?.onProgressUpdated(16)
//
//        jcwKit.resetWallet()
//        progressListener?.onProgressUpdated(28)
//
//        val capabilities = jcwKit.getCapabilities()
//        progressListener?.onProgressUpdated(45)
//
//        if (!capabilities.isPinDisabled) {
//            jcwKit.storePin(pinCode)
//        }
//
//        val seed = jcwKit.createWallet(wordsCount)
//        progressListener?.onProgressUpdated(65)
//
//        createAccounts()
//        progressListener?.onProgressUpdated(81)
//
//        val walletAccounts = getAccounts()
//        progressListener?.onProgressUpdated(100)
//
//        jcwKit.deinitWalletSdk()
//
//        return NfcActionResult.CreateWalletResult(
//            seed,
//            walletAccounts
//        )
//    }
//
//    override fun restoreWallet(
//        pinCode: String,
//        mnemonic: String,
//        progressListener: ProgressListener?,
//    ): NfcActionResult.RestoreWalletResult {
//        progressListener?.onProgressUpdated(0)
//
//        jcwKit.initWalletSdk(callback)
//        progressListener?.onProgressUpdated(16)
//
//        jcwKit.resetWallet()
//        progressListener?.onProgressUpdated(28)
//
//        val capabilities = jcwKit.getCapabilities()
//        progressListener?.onProgressUpdated(45)
//
//        if (!capabilities.isPinDisabled) {
//            jcwKit.storePin(pinCode)
//        }
//
//        jcwKit.restoreWallet(mnemonic)
//        progressListener?.onProgressUpdated(65)
//
//        createAccounts()
//        progressListener?.onProgressUpdated(81)
//
//        val walletAccounts = getAccounts()
//        progressListener?.onProgressUpdated(100)
//
//        jcwKit.deinitWalletSdk()
//
//        return NfcActionResult.RestoreWalletResult(
//            walletAccounts
//        )
//    }
//
//    private fun createAccounts() {
//        val btcAccount = AccountDTO(
//            currencyId = if (testNet) BTC_COIN_ID_TESTNET else BTC_COIN_ID,
//            networkId = 0,
//            accountId = 0,
//            accountStatus = AccountStatus.ACTIVE_ACC,
//            nickname = BTC_ACC_NAME,
//            bip = Bip.BIP_84,
//        )
//        jcwKit.createAccount(btcAccount)
//
//        val ethAccount = AccountDTO(
//            currencyId = ETH_COIN_ID,
//            networkId = 0,
//            accountId = 0,
//            accountStatus = AccountStatus.ACTIVE_ACC,
//            nickname = ETH_ACC_NAME,
//            bip = Bip.BIP_44,
//        )
//        jcwKit.createAccount(ethAccount)
//    }

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

//    override fun createBitcoinTransaction(
//        pinCode: String,
//        inputs: String,
//        inputsCount: Int,
//        outputs: String,
//        outputsCount: Int,
//    ): String {
//
//        jcwKit.initWalletSdk(callback)
//
//        jcwKit.getAccounts()
//
//        val accountIndex = getAccountIndexFor(CryptoCurrency.Bitcoin)
//        jcwKit.selectAccount(accountIndex)
//
//        if (pinCode.isEmpty()) {
//            jcwKit.walletVerifyCVM()
//        } else {
//            jcwKit.verifyPin(pinCode)
//        }
//
//        val transaction = jcwKit.createBitcoinTransaction(
//            inputs, inputsCount, outputs, outputsCount
//        )
//
//        Timber.d("-----> tx = $transaction")
//
//        jcwKit.deinitWalletSdk()
//
//        return transaction
//    }
//
//    override fun createEthereumTransaction(
//        pinCode: String,
//        chainId: ByteArray,
//        nonce: ByteArray,
//        maxPriorityFeePerGas: ByteArray,
//        maxFeePerGas: ByteArray,
//        gasLimit: ByteArray,
//        amount: ByteArray,
//        addressTo: ByteArray,
//    ): String {
//        jcwKit.initWalletSdk(callback)
//
//        jcwKit.getAccounts()
//
//        val accountIndex = getAccountIndexFor(CryptoCurrency.Ethereum)
//        jcwKit.selectAccount(accountIndex)
//
//        if (pinCode.isEmpty()) {
//            jcwKit.walletVerifyCVM()
//        } else {
//            jcwKit.verifyPin(pinCode)
//        }
//
//        val transaction = jcwKit.createEthereumTransaction(
//            chainId = chainId,
//            nonce = nonce,
//            maxPriorityFeePerGas = maxPriorityFeePerGas,
//            maxFeePerGas = maxFeePerGas,
//            gasLimit = gasLimit,
//            amount = amount,
//            addressTo = addressTo
//        )
//
//        Timber.d("-----> tx = $transaction")
//
//        jcwKit.deinitWalletSdk()
//
//        return transaction
//    }
//
//    private fun getAccountIndexFor(cryptoCurrency: CryptoCurrency): Int {
//        val accounts = jcwKit.getAccounts()
//
//        accounts.forEachIndexed { index, accountDTO ->
//            val accountCurrency = when (accountDTO.currencyId) {
//                0, 1 -> CryptoCurrency.Bitcoin
//                60 -> CryptoCurrency.Ethereum
//                else -> {
//                    throw IllegalStateException("No such currency id ${accountDTO.currencyId}")
//                }
//            }
//
//            if (accountCurrency == cryptoCurrency) return index
//        }
//
//        return -1
//    }

    private companion object {
        private const val TOTAL_ENROLL_STEPS = 6

    }
}