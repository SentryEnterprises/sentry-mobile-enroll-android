package com.secure.jnet.jcwkit

import android.util.Log
import com.secure.jnet.jcwkit.models.AccountDTO
import com.secure.jnet.jcwkit.models.BiometricMode
import com.secure.jnet.jcwkit.models.CapabilitiesDTO
import com.secure.jnet.jcwkit.models.EnrollStatusDTO
import com.secure.jnet.jcwkit.models.VerifyCVMDTO
import com.secure.jnet.jcwkit.models.WalletStatusDTO
import com.secure.jnet.jcwkit.models.WalletVersionDTO
import com.secure.jnet.jcwkit.models.mapToBiometricMode
import com.secure.jnet.jcwkit.utils.formatted
import com.secure.jnet.jcwkit.utils.hexStringToByteArray
import com.secure.jnet.jcwkit.utils.toHexString
import com.secure.jnet.wallet.presentation.APDUCommand
import com.sun.jna.Memory
import com.sun.jna.Pointer
import com.sun.jna.ptr.IntByReference
import java.io.IOException

class JCWKit {

    fun initWalletSdk(callBack: SmartCardApduCallback) {
        val result = NativeLib.INSTANCE.LibSdkWalletInit(SECURE_CHANNEL, callBack)
        Log.d("------->", "LibSdkWalletInit() result = $result")

        if (result != 0) {
            throw JCWIOException(result)
            //throw IOException("Init Wallet Applet Error")
        }
    }

    fun deinitWalletSdk() {
        val result = NativeLib.INSTANCE.LibSdkWalletDeinit()
        Log.d("------->", "LibSdkWalletDeinit() result = $result")

        if (result != 0) {
            throw IOException("Init Wallet Applet Error")
        }
    }

    fun getWalletVersion(): WalletVersionDTO {
        val pointer: Pointer = Memory(4)

        val result = NativeLib.INSTANCE.LibSdkGetWalletVersion(pointer)
        Log.d("------->", "LibSdkGetWalletVersion() result = $result")

        if (result != 0) {
            throw IOException("Result error $result")
        }

        return WalletHelper.parseWalletVersionResult(pointer)
    }

    fun getOSVersion(): String {
        val version: Pointer = Memory(7)
//        println("getOSVersion ${version.getByteArray(0,4).formatted()}")
        val result = NativeLib.INSTANCE.LibSdkGetOSVersion(version)

        println("getOSVersion ${version.getByteArray(0,4).formatted()}")
        println("getOSVersion $result")
//
//        if dataBuffer[p] != 0x9F { throw SentrySDKError.cardOSVersionError }
//        p += 1
//        if dataBuffer[p] != 0x02 {throw SentrySDKError.cardOSVersionError }
//        p += 1
//        if dataBuffer[p] != 5 { throw SentrySDKError.cardOSVersionError }
//        p += 1
//
//        let major = dataBuffer[p] - 0x30
//        p += 2
//        let minor = dataBuffer[p] - 0x30
//        p += 2
//        let hotfix = dataBuffer[p] - 0x30
//
//        let retVal = VersionInfo(isInstalled: true, majorVersion: Int(major), minorVersion: Int(minor), hotfixVersion: Int(hotfix), text: nil)

        return "${version.getByteArray(1, 1)[0].toInt()}" +
                ".${version.getByteArray(2, 1)[0].toInt()}" +
                ".${version.getByteArray(3, 1)[0].toInt()}"
    }
    fun getCapabilities(): CapabilitiesDTO {
        val capability: Pointer = Memory(1)
        val result = NativeLib.INSTANCE.LibSdkGetCapability(capability)
        Log.d("------->", "LibSdkGetCapability() result = $result")

        if (result != 0) {
            throw IOException("Get Capabilities Error.")
        }

        return WalletHelper.parseCapabilitiesResult(capability)
    }

    fun getWalletStatus(): WalletStatusDTO {
        val gwlcs: Pointer = Memory(1)
        val wpsw: Pointer = Memory(1)
        val wssm: Pointer = Memory(1)

        val result = NativeLib.INSTANCE.LibSdkGetStatus(gwlcs, wpsw, wssm)
        Log.d("------->", "LibSdkGetStatus() result = $result")

        if (result != 0) {
            Log.e("---->", "getWalletStatus() error $result")
            throw IOException("Get Card Status Error.")
        }

        return WalletStatusDTO(
            WalletHelper.parseGWLCSResult(gwlcs),
            WalletHelper.parseWPSMResult(wpsw),
            WalletHelper.parseWSSMResult(wssm),
        )
    }

    fun selectWallet() {
        val result = NativeLib.INSTANCE.LibSdkSelectWallet()
        Log.d("------->", "LibSdkSelectWallet() result = $result")

        if (result != 0) {
            Log.e("---->", "selectWallet() error $result")
            throw IOException("Select Wallet Error.")
        }
    }

    fun resetWallet() {
        val result = NativeLib.INSTANCE.LibSdkResetWallet()
        Log.d("------->", "LibSdkResetWallet() result = $result")

        if (result != 0) {
            Log.e("---->", "resetWallet() error $result")
            throw IOException("Reset Wallet Error.")
        }
    }

    fun storePin(pin: String) {
        val pinArray: IntArray = pin
            .map { it.digitToInt() }
            .toIntArray()

        val pinPointer: Pointer = Memory(pinArray.size.toLong()).apply {
            pinArray.forEachIndexed { index, i ->
                setByte(index.toLong(), i.toByte())
            }
        }

        val result = NativeLib.INSTANCE.LibSdkStorePin(pinPointer, pinArray.size)
        Log.d("------->", "LibSdkStorePin() result = $result")

        if (result != 0) {
            Log.e("---->", "storePin() error $result")
            throw IOException("Store Code Error.")
        }
    }

    fun verifyPin(pin: String): Boolean {
        val pinArray: IntArray = pin
            .map { it.digitToInt() }
            .toIntArray()

        val pinPointer = Memory(pinArray.size.toLong()).apply {
            pinArray.forEachIndexed { index, i ->
                setByte(index.toLong(), i.toByte())
            }
        }

        val result = NativeLib.INSTANCE.LibSdkVerifyPin(pinPointer, pinArray.size)
        Log.d("------->", "LibSdkVerifyPin() result = $result")

        if (result != 0) {
            val attemptsLeft = result xor 0xC0
            throw IOException("Incorrect PIN, $attemptsLeft attempts left")
        }

        return true
    }

    fun createWallet(wordsCount: Int): String {
        val mnemonic: Pointer = Memory(1024)
        val mnemonicLength = IntByReference()
        val passphrase: Pointer = Memory(1)

        val result = NativeLib.INSTANCE.LibSdkCreateWallet(
            ITERATIONS,
            wordsCount,
            0,
            passphrase,
            0,
            mnemonic,
            mnemonicLength
        )

        Log.d("------->", "LibSdkCreateWallet() result = $result")

        if (result != 0) {
            Log.e("---->", "createWallet() error $result")
            throw IOException("Create Wallet Error.")
        }

        return WalletHelper.parseCreateWalletResult(mnemonic, mnemonicLength)
    }

    fun restoreWallet(mnemonic: String) {
        val mnemonicPointer: Pointer = Memory(mnemonic.length.toLong()).apply {
            mnemonic.forEachIndexed { index, i ->
                setByte(index.toLong(), i.code.toByte())
            }
        }
        val passphrasePointer: Pointer = Memory(1)

        val result = NativeLib.INSTANCE.LibSdkRecoveryWallet(
            ITERATIONS,
            mnemonicPointer,
            mnemonic.length,
            passphrasePointer,
            0,
        )

        Log.d("------->", "LibSdkCreateWallet() result = $result")

        if (result != 0) {
            throw IOException("Import Wallet Error.")
        }
    }

    fun createAccount(account: AccountDTO) {
        val nicknamePointer = Memory(account.nickname.length.toLong() + 1).apply {
            setString(0, account.nickname)
        }

        val result = NativeLib.INSTANCE.LibSdkAccountCreate(
            account.currencyId,
            account.networkId,
            account.accountId,
            account.chain,
            account.bip.value,
            nicknamePointer,
            account.nickname.length
        )
        Log.d("------->", "LibSdkAccountCreate() result = $result")

        if (result != 0) {
            throw IOException("Create Account Error.")
        }
    }

    fun getAccounts(): List<AccountDTO> {
        val accountsCountPointer = Memory(4)
        val accountsInfoPointer = Memory(7 * 26)

        val result = NativeLib.INSTANCE.LibSdkGetAccounts(
            accountsCountPointer,
            accountsInfoPointer
        )
        Log.d("------->", "LibSdkGetAccounts() result = $result")

        if (result != 0) {
            throw IOException("Get Account Error.")
        }

        return WalletHelper.parseGetAccountsResult(accountsCountPointer, accountsInfoPointer)
    }

    fun selectAccount(accountIndex: Int) {
        val result = NativeLib.INSTANCE.LibSdkSelectAccount(accountIndex)
        Log.d("------->", "LibSdkSelectAccount() result = $result")

        if (result != 0) {
            throw IOException("Select Account Error.")
        }
    }

    fun getReceiveAddress(): String {
        val addressPointer = Memory(43L)
        val addressLengthPointer = IntByReference()

        val result = NativeLib.INSTANCE.LibSdkWalletGetAddress(
            addressPointer,
            addressLengthPointer
        )
        Log.d("------->", "LibSdkWalletGetAddress() result = $result")

        if (result != 0) {
            throw IOException("Get Account Address Error.")
        }

        val address = addressPointer.getByteArray(
            0, addressLengthPointer.value
        ).joinToString(separator = "") {
            String(byteArrayOf(it), Charsets.UTF_8)
        }

        Log.d("------->","Receive address = $address")

        return address
    }

    fun walletVerifyCVM(): VerifyCVMDTO {
        val cvm: Pointer = Memory(2)
        val wssm: Pointer = Memory(1)

        val result = NativeLib.INSTANCE.LibSdkWalletCVMVerify(cvm, wssm)
        Log.d("------->", "LibSdkWalletCVMVerify() result = $result")

        if (result != 0) {
            //throw IOException("Result error $result")
            throw JCWIOException(result)
        }

        Log.d("------->", "LibSdkWalletCVMVerify() CVM Available = ${cvm.getByte(0).toInt()}")
        Log.d("------->", "LibSdkWalletCVMVerify() Finger Verify = ${cvm.getByte(1).toInt()}")

        val cvmAvailable = cvm.getByte(0).toInt() == 0x01
        val fingerVerified = cvm.getByte(1).toInt() == 0x11

        if (!cvmAvailable) {
            throw IOException("Unable to verify fingerprint, the CVM is not available. Move the card away from the phone and try again.")
        }
        if (!fingerVerified) {
            throw IOException("Biometric Verification failed.")
        }

        return VerifyCVMDTO(
            cvmAvailable = true,
            fingerVerified = true,
            wssm = WalletHelper.parseWSSMResult(wssm)
        )
    }

    /**
     * Biometric
     */
    fun initEnrollSdk(pinCode: String, callBack: SmartCardApduCallback) {
        val pinArray: IntArray = pinCode
            .map { it.digitToInt() }
            .toIntArray()

        val pinPointer: Pointer = Memory(pinArray.size.toLong()).apply {
            pinArray.forEachIndexed { index, i ->
                setByte(index.toLong(), i.toByte())
            }
        }
        val result = NativeLib.INSTANCE.LibSdkEnrollInit(
            1,
            pinPointer,
            pinArray.size,
            callBack
        )
        Log.d("------->", "LibSdkEnrollInit() result = $result")

        if (result != 0) {
            throw JCWIOException(result)
            //throw IOException("Init Biometric Applet Error")
        }
    }

    fun deinitEnrollSdk() {
        val result = NativeLib.INSTANCE.LibSdkEnrollDeinit()
        Log.d("------->", "LibSdkEnrollDeinit() result = $result")

        if (result != 0) {
            throw JCWIOException(result)
            //throw IOException("Deinit Biometric Applet Error")
        }
    }

    fun getEnrollStatus(nonNativeSmartCardApduCallback: NonNativeSmartCardApduCallback): EnrollStatusDTO {

        val response = nonNativeSmartCardApduCallback.call(APDUCommand.GET_ENROLL_STATUS.value)
        if (response.isSuccess) {
            return EnrollStatusDTO(
                maxFingerNumber = response.getOrNull()?.get(31)?.toInt() ?: 0,
                enrolledTouches = response.getOrNull()?.get(32)?.toInt() ?: 0,
                remainingTouches = response.getOrNull()?.get(33)?.toInt() ?: 0,
                biometricMode = response.getOrNull()?.get(39)?.toInt()?.mapToBiometricMode() ?: BiometricMode.UNKNOWN_MODE,
            )
        } else {
            throw response.exceptionOrNull() ?: IllegalStateException()
        }

    }
    fun getEnrollStatus(): EnrollStatusDTO {
        val maxFingersNumbers: Pointer = Memory(1)
        val enrolledTouches: Pointer = Memory(1)
        val remainingTouches: Pointer = Memory(1)
        val biometricMode: Pointer = Memory(1)

        val result = NativeLib.INSTANCE.LibSdkGetEnrollStatus(
            maxFingersNumbers,
            enrolledTouches,
            remainingTouches,
            biometricMode
        )
        Log.d("------->", "LibSdkGetEnrollStatus() result = $result")

        if (result != 0) {
            Log.e("---->", "getEnrollStatus() error $result")
            //throw IOException("Get Enroll Status Error.")
            throw JCWIOException(result)
        }

        return EnrollStatusDTO(
            maxFingersNumbers.getByte(0).toInt(),
            enrolledTouches.getByte(0).toInt(),
            remainingTouches.getByte(0).toInt(),
            biometricMode.getByte(0).toInt().mapToBiometricMode(),
        )
    }

    fun enrollReprocess(): EnrollStatusDTO {
        val enrolledTouches: Pointer = Memory(1)
        val remainingTouches: Pointer = Memory(1)
        val biometricMode: Pointer = Memory(1)

        val result = NativeLib.INSTANCE.LibSdkEnrollReprocess(
            FINGER_NUMBER,
            enrolledTouches,
            remainingTouches,
            biometricMode
        )
        Log.d("------->", "LibSdkEnrollReprocess() result = $result")

        if (result != 0) {
            Log.e("---->", "enrollReprocess() error $result")
            //throw IOException("Finger Scan Error.")
            throw JCWIOException(result)
        }

        return EnrollStatusDTO(
            FINGER_NUMBER,
            enrolledTouches.getByte(0).toInt(),
            remainingTouches.getByte(0).toInt(),
            biometricMode.getByte(0).toInt().mapToBiometricMode(),
        )
    }

    fun enrollProcess(): EnrollStatusDTO {
        val enrolledTouches: Pointer = Memory(1)
        val remainingTouches: Pointer = Memory(1)
        val biometricMode: Pointer = Memory(1)

        val result = NativeLib.INSTANCE.LibSdkEnrollProcess(
            FINGER_NUMBER,
            enrolledTouches,
            remainingTouches,
            biometricMode
        )
        Log.d("------->", "LibSdkEnrollProcess() result = $result")

        if (result != 0) {
            Log.e("---->", "enrollProcess() error $result")
            //throw IOException("Finger Scan Error.")
            throw JCWIOException(result)
        }

        return EnrollStatusDTO(
            FINGER_NUMBER,
            enrolledTouches.getByte(0).toInt(),
            remainingTouches.getByte(0).toInt(),
            biometricMode.getByte(0).toInt().mapToBiometricMode(),
        )
    }

    fun verifyEnroll() {
        val result = NativeLib.INSTANCE.LibSdkEnrollVerify()
        Log.d("------->", "LibSdkEnrollVerify() result = $result")

        if (result != 0) {
            Log.e("---->", "verifyEnroll() error $result")
            //throw IOException("Verify Scan Error.")
            throw JCWIOException(result)
        }
    }

    fun createBitcoinTransaction(
        inputs: String,
        inputsCount: Int,
        outputs: String,
        outputsCount: Int,
        lockTime: Int = 0,
    ): String {
        Log.d("------->", "createTransaction()")

        val inputsByteArray = hexStringToByteArray(inputs)
        val inputsLength = inputsByteArray.size.toLong()

        val inputsPointer: Pointer = Memory(inputsLength).apply {
            inputsByteArray.forEachIndexed { index, i ->
                setByte(index.toLong(), i)
            }
        }

        val outputsByteArray = hexStringToByteArray(outputs)
        val outputsLength = outputsByteArray.size.toLong()

        val outputsPointer: Pointer = Memory(outputsLength).apply {
            outputsByteArray.forEachIndexed { index, i ->
                setByte(index.toLong(), i)
            }
        }

        val transactionHashPointer: Pointer = Memory(TX_HASH_SIZE)
        val transactionPointer: Pointer = Memory(TX_SIZE)
        val transactionLengthPointer = IntByReference()

        val result = NativeLib.INSTANCE.LibSdkBip143(
            inputs = inputsPointer,
            inputCount = inputsCount,
            outputs = outputsPointer,
            outputCount = outputsCount,
            lockTime = lockTime,
            transaction = transactionPointer,
            transactionLength = transactionLengthPointer,
            transactionHash = transactionHashPointer
        )
        Log.d("------->", "LibBip143() result = $result")

        if (result != 0) {
            Log.e("---->", "LibBip143() error $result")
            throw IOException("Sign Transaction Error.")
        }

        val transaction = transactionPointer.getByteArray(
            0, transactionLengthPointer.value
        ).toHexString()

        return transaction
    }

    fun createEthereumTransaction(
        chainId: ByteArray,
        nonce: ByteArray,
        maxPriorityFeePerGas: ByteArray,
        maxFeePerGas: ByteArray,
        gasLimit: ByteArray,
        amount: ByteArray,
        addressTo: ByteArray,
    ): String {
        Log.d("------->", "createEthereumTransaction()")
        Log.d("------->", "createEthereumTransaction() ---------------------------------")
        Log.d("------->", "chainId = ${chainId.toHexString()}")
        Log.d("------->", "nonce = ${nonce.toHexString()}")
        Log.d("------->", "maxPriorityFeePerGas = ${maxPriorityFeePerGas.toHexString()}")
        Log.d("------->", "maxFeePerGas = ${maxFeePerGas.toHexString()}")
        Log.d("------->", "gasLimit = ${gasLimit.toHexString()}")
        Log.d("------->", "amount = ${amount.toHexString()}")
        Log.d("------->", "addressTo = ${addressTo.decodeToString()}")
        Log.d("------->", "createEthereumTransaction() ---------------------------------")

        val chainIdPointer = Memory(chainId.size.toLong()).apply {
            write(0, chainId, 0, chainId.size)
        }

        val noncePointer = Memory(nonce.size.toLong()).apply {
            write(0, nonce, 0, nonce.size)
        }

        val maxPriorityFeePerGasPointer = Memory(maxPriorityFeePerGas.size.toLong()).apply {
            write(0, maxPriorityFeePerGas, 0, maxPriorityFeePerGas.size)
        }

        val maxFeePerGasPointer = Memory(maxFeePerGas.size.toLong()).apply {
            write(0, maxFeePerGas, 0, maxFeePerGas.size)
        }

        val gasLimitPointer = Memory(gasLimit.size.toLong()).apply {
            write(0, gasLimit, 0, gasLimit.size)
        }

        val amountPointer = Memory(amount.size.toLong()).apply {
            write(0, amount, 0, amount.size)
        }

        val destinationPointer = Memory(addressTo.size.toLong()).apply {
            write(0, addressTo, 0, addressTo.size)
        }

        val data = Memory(1).apply {
            setByte(0, ETH_DATA)
        }

        val accessList = Memory(1).apply {
            setByte(0, ETH_ACCESS_LIST)
        }

        val transactionHashPointer: Pointer = Memory(TX_HASH_SIZE)
        val transactionPointer: Pointer = Memory(TX_SIZE)
        val transactionLengthPointer = IntByReference()

        val result = NativeLib.INSTANCE.LibSdkEip1559(
            chainId = chainIdPointer,
            nonce = noncePointer,
            maxPriorityFeePerGas = maxPriorityFeePerGasPointer,
            maxFeePerGas = maxFeePerGasPointer,
            gasLimit = gasLimitPointer,
            destination = destinationPointer,
            amount = amountPointer,
            data = data,
            accessList = accessList,
            tx = transactionPointer,
            txLength = transactionLengthPointer,
            transactionHash = transactionHashPointer
        )
        Log.d("------->", "LibSdkEip1559() result = $result")

        if (result != 0) {
            Log.e("---->", "LibSdkEip1559() error $result")
            throw IOException("Sign Transaction Error.")
        }

        return transactionPointer.getByteArray(
            0, transactionLengthPointer.value
        ).toHexString()
    }

    companion object {
        private const val SECURE_CHANNEL = 1
        private const val ITERATIONS = 2048
        private const val FINGER_NUMBER = 1
        private const val TX_SIZE = (1024 * 8).toLong()
        private const val TX_HASH_SIZE = 32L

        // Eth
        private const val ETH_DATA = 0x80.toByte()
        private const val ETH_ACCESS_LIST = 0xC0.toByte()
    }


    fun resetBiometricData(): Boolean {
        return NativeLib.INSTANCE.LibSdkResetWallet() == 0
    }
}