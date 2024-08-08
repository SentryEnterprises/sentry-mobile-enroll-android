package com.secure.jnet.jcwkit

import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.ptr.IntByReference

@Suppress("FunctionName")
interface NativeLib : Library {

    // Init / Deinit SDK
    fun LibSdkWalletInit(secureChannel: Int, callback: SmartCardApduCallback?): Int
    fun LibSdkWalletDeinit(): Int

    // Get info
    fun LibSdkGetSdkVersion(pointer: Pointer?): Int
    fun LibSdkGetWalletVersion(pointer: Pointer?): Int
    fun LibSdkGetOSVersion(pointer: Pointer?): Int
    fun LibSdkGetCapability(pointer: Pointer?): Int
    fun LibSdkGetGGUID(gguid: Pointer?): Int
    fun LibSdkGetStatus(gwlcs: Pointer?, wpsm: Pointer?, wssm: Pointer?): Int

    fun LibAuthWrap(apdu_in: Pointer?, in_len: Int, apdu_out: Pointer?, out_len: Pointer?, key_enc: Pointer?, key_cmac: Pointer?, chaining_value: Pointer?, encryption_counter: Pointer?): Int;
    fun LibAuthUnwrap(apdu_in: Pointer?, in_len: Int, apdu_out: Pointer?, out_len: Pointer?, key_enc: Pointer?, key_rmac: Pointer?, chaining_value: Pointer?, encryption_counter: Pointer?): Int;

    // Verify PIN / Fingerprint
    fun LibSdkStorePin(pin: Pointer?, pinLength: Int): Int
    fun LibSdkVerifyPin(pin: Pointer?, pinLength: Int): Int
    fun LibSdkWalletGetCVMStatus(cvm: Pointer?, wssm: Pointer?): Int
    fun LibSdkWalletCVMVerify(cvm: Pointer?, wssm: Pointer?): Int

    // Create / Restore / Reset wallet, Account
    fun LibSdkCreateWallet(
        iteration: Int,
        words: Int,
        lang: Int,
        passphrase: Pointer?,
        passphraseLength: Int,
        mnemonics: Pointer?,
        mnemonicsLength: IntByReference?
    ): Int

    fun LibSdkRecoveryWallet(
        iteration: Int,
        mnemonics: Pointer?,
        mnemonicsLength: Int,
        passphrase: Pointer?,
        passphraseLength: Int
    ): Int

    fun LibSdkAccountCreate(
        currencyId: Int,
        networkID: Int,
        accountId: Int,
        chain: Int,
        bip: Int,
        nickname: Pointer?,
        nicknameLength: Int
    ): Int

    fun LibSdkResetBiometrics(): Int
    fun LibSdkResetWallet(): Int
    fun LibSdkSelectWallet(): Int
    fun LibSdkGetAccounts(accountsNumber: Pointer, accountsInfo: Pointer): Int
    fun LibSdkSelectAccount(accountIndex: Int): Int
    fun LibSdkAccountGetPublicKey(
        accountPublicKey: Pointer,
        accountChainCodeKey: Pointer,
        publicKeyParent: Pointer
    ): Int

    fun LibSdkAccountGetReceivePublicKey(receivePublicKey: Pointer): Int
    fun LibSdkAccountGetAddressIndex(addressIndex: Pointer): Int
    fun LibSdkAccountGetChainIndex(chainIndex: Pointer): Int
    fun LibSdkAccountSetAddressIndex(addressIndex: Int): Int
    fun LibSdkAccountSetChainIndex(chainIndex: Int): Int

    fun LibSdkWalletGetAddress(
        address: Pointer,
        addressLength: IntByReference,
    ): Int

    fun LibSdkVerifyInit(
        secureChannel: Int,
        pin: Pointer,
        pinLength: Int,
        callback: SmartCardApduCallback
    ): Int
    /**
     * Biometric
     */
    fun LibSdkEnrollInit(
        secureChannel: Int,
        pin: Pointer,
        pinLength: Int,
        callback: SmartCardApduCallback
    ): Int

    fun LibSdkEnrollDeinit(): Int

    fun LibSdkGetEnrollStatus(
        maxFingersNumbers: Pointer,
        enrolledTouches: Pointer,
        remainingTouches: Pointer,
        biometricMode: Pointer
    ): Int

    fun LibSdkEnrollProcess(
        fingerNumber: Int,
        enrolledTouches: Pointer,
        remainingTouches: Pointer,
        biometricMode: Pointer
    ): Int

    fun LibSdkEnrollReprocess(
        fingerNumber: Int,
        enrolledTouches: Pointer,
        remainingTouches: Pointer,
        biometricMode: Pointer
    ): Int

    fun LibVerifyFingerprint(): Int

    fun LibSdkEnrollVerify(): Int

    /**
     * Tx
     */
    fun LibSdkAddressToScript(
        address: Pointer,
        length: Long,
        script: Pointer,
        scriptLength: IntByReference,
    ): Int

    fun LibSdkBip143(
        inputs: Pointer,
        inputCount: Int,
        outputs: Pointer,
        outputCount: Int,
        lockTime: Int,
        transaction: Pointer,
        transactionLength: IntByReference,
        transactionHash: Pointer,
    ): Int

    fun LibSecureChannelInit(
        apduCommand: Pointer,
        apduCommandLen: Int,
        privateKey: Pointer,
        publicKey: Pointer,
        secretShses: Pointer,
    )

    fun LibCalcSecretKeys(
        pubKey: Pointer,
        shses: Pointer,
        privatekey: Pointer,
        keyRespt: Pointer,
        keyENC: Pointer,
        keyCMAC: Pointer,
        keyRMAC: Pointer,
        chaining: Pointer,
    )

    fun LibSdkEip1559(
        chainId: Pointer,
        nonce: Pointer,
        maxPriorityFeePerGas: Pointer,
        maxFeePerGas: Pointer,
        gasLimit: Pointer,
        destination: Pointer,
        amount: Pointer,
        data: Pointer,
        accessList: Pointer,
        tx: Pointer,
        txLength: IntByReference,
        transactionHash: Pointer,
    ): Int

    companion object {
        val INSTANCE: NativeLib = Native.load("jcwkit", NativeLib::class.java)
    }
}
