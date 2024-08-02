package com.sentryenterprises.sentry.sdk.biometrics


import com.secure.jnet.jcwkit.NativeLib
import com.secure.jnet.jcwkit.utils.formatted
import com.secure.jnet.wallet.presentation.APDUCommand
import com.secure.jnet.wallet.presentation.SentrySDKError
import com.sentryenterprises.sentry.sdk.apdu.APDUResponseCode
import com.sentryenterprises.sentry.sdk.models.AuthInitData
import com.sentryenterprises.sentry.sdk.models.Keys
import com.sentryenterprises.sentry.sdk.models.NfcIso7816Tag
import com.sun.jna.Memory
import com.sun.jna.Pointer
import kotlin.collections.indices


// A `tuple` containing an `APDU` command result data buffer and a status word.
private data class APDUReturnResult(val data: ByteArray, val statusWord: Int)

/**
Communicates with the IDEX Enroll applet by sending various `APDU` commands in the appropriate order.
 */
internal class BiometricsApi(
    val isDebugOutputVerbose: Boolean = true,
    val useSecureChannel: Boolean = true,
) {

    // Note - This is reset when selecting a new applet (i.e. after initing the secure channel)
    private var encryptionCounter: ByteArray = byteArrayOf()

    // Note - this changes with every wrap, and resets when initing secure channel
    private var chainingValue: ByteArray = byteArrayOf()


    private var privateKey: ByteArray = byteArrayOf()
    private var publicKey: ByteArray = byteArrayOf()
    private var sharedSecret: ByteArray = byteArrayOf()
    private var keyRespt: ByteArray = byteArrayOf()
    private var keyENC: ByteArray = byteArrayOf()
    private var keyCMAC: ByteArray = byteArrayOf()
    private var keyRMAC: ByteArray = byteArrayOf()


    /**
    Initializes the BioVerify applet by selecting the applet on the SentryCard. Call this method before calling other methods in this unit that communicate with the BioVerify applet.

    - Note: The BioVerify applet does not currently support secure communication, so a secure channel is not setup during initialization.

    - Parameters:
    - tag: The `NFCISO7816` tag supplied by an NFC connection to which `APDU` commands are sent.

    This method can throw the following exceptions:
     * `SentrySDKError.apduCommandError` that contains the status word returned by the last failed `APDU` command.
     * `SentrySDKError.secureChannelInitializationError` if `useSecureCommunication` is `true` but an error occurred initializing the secure communication encryption.
     * `SentrySDKError.secureCommunicationNotSupported` if `useSecureCommunication` is `true` but the version of the BioVerify applet on the SentryCard does nto support secure communication (highly unlikely).

     */
    fun initializeVerify(tag: NfcIso7816Tag) {
        var debugOutput = "----- BiometricsAPI Initialize Verify\n"

        if (isDebugOutputVerbose) {
            print(debugOutput)
        }

        debugOutput += "     Selecting Verify Applet\n"

        APDUCommand.SELECT_VERIFY_APPLET
        sendAndConfirm(
            apduCommand = APDUCommand.SELECT_VERIFY_APPLET.value,
            name = "Select Verify Applet",
            tag = tag
        )

//        // use a secure channel, setup keys
        debugOutput += "     Initializing Secure Channel\n"
//
        encryptionCounter = ByteArray(16) { 0 }

        chainingValue = byteArrayOf()
        privateKey = byteArrayOf()
        publicKey = byteArrayOf()
        sharedSecret = byteArrayOf()
        keyRespt = byteArrayOf()
        keyENC = byteArrayOf()
        keyCMAC = byteArrayOf()
        keyRMAC = byteArrayOf()
//
//        // initialize the secure channel. this sets up keys and encryption
//        val authInfo = try getAuthInitCommand()
//        privateKey.append(contentsOf: authInfo.privateKey)
//        publicKey.append(contentsOf: authInfo.publicKey)
//        sharedSecret.append(contentsOf: authInfo.sharedSecret)
//
//        let securityInitResponse = try await sendAndConfirm(apduCommand: authInfo.apduCommand, name: "Auth Init", to: tag)
//
//        if securityInitResponse.statusWord == APDUResponseCode.operationSuccessful.rawValue {
//            let secretKeys = try calcSecretKeys(receivedPubKey: securityInitResponse.data.toArrayOfBytes(), sharedSecret: sharedSecret, privateKey: privateKey)
//
//            keyRespt.append(contentsOf: secretKeys.keyRespt)
//            keyENC.append(contentsOf: secretKeys.keyENC)
//            keyCMAC.append(contentsOf: secretKeys.keyCMAC)
//            keyRMAC.append(contentsOf: secretKeys.keyRMAC)
//            chainingValue.append(contentsOf: secretKeys.chainingValue)
//        } else {
//            throw SentrySDKError.secureChannelInitializationError
//        }

        debugOutput += "------------------------------\n"
    }
// done after select but before verifying pin
    // returns 5F494104 <keys> 8610 <chaining value> 9000
    // 5F494104 D13CD1EDF0CFDC960CB8CC060DEA15203D6C3D7C81B8DA8D020C012652E8A50CE59D462EEBFBC6A3AF55C47E5DCD897EFD371321389DA2B227EEF48FA6143106 8610 498EDA1B2CDF9E20BEE060BA439FAB20 9000
    // whatever processes the response from this command should check:
    //  starts with 5F494104
    //  calls calcSecretKeys
    //  checks 8610 and extracts the chaining value
    // Note: We may need to call this and calcSecretKeys each time a new applet is selected!

    /// Initializes secure communication.
    private fun getAuthInitCommand(): AuthInitData {
//        let apduCommand = UnsafeMutablePointer<Byte>.allocate(capacity: 100)
//        let apduCommandLen = UnsafeMutablePointer<Int32>.allocate(capacity: 1)
//        let privateKey = UnsafeMutablePointer<UInt8>.allocate(capacity: 32)
//        let publicKey = UnsafeMutablePointer<UInt8>.allocate(capacity: 64)
//        let secretShses = UnsafeMutablePointer<UInt8>.allocate(capacity: 32)
//        defer {
//            apduCommand.deallocate()
//            apduCommandLen.deallocate()
//            privateKey.deallocate()
//            publicKey.deallocate()
//            secretShses.deallocate()
//        }

//        val response = LibSecureChannelInit(apduCommand,apduCommandLen, privateKey, publicKey, secretShses)
        val apduCommand: Pointer = Memory(100)
        val apduCommandLen: Int = 0
        val privateKey: Pointer = Memory(32)
        val publicKey: Pointer = Memory(64)
        val secretShses: Pointer = Memory(32)

        val response = NativeLib.INSTANCE.LibSecureChannelInit(
            apduCommand,
            apduCommandLen,
            privateKey,
            publicKey,
            secretShses
        )
//        if response != SUCCESS {
//            if response == ERROR_KEYGENERATION {
//                throw SentrySDKError.keyGenerationError
//            }
//            if response == ERROR_SHAREDSECRETEXTRACTION {
//                throw SentrySDKError.sharedSecretExtractionError
//            }
//
//            // TODO: Fix once we've converted security to pure Swift
//            throw NSError(domain: "Unknown return value", code: -1)
//        }

        var command: ByteArray = byteArrayOf()
        var privKey: ByteArray = byteArrayOf()
        var pubKey: ByteArray = byteArrayOf()
        var sharedSecret: ByteArray = byteArrayOf()

//        for i in 0..<apduCommandLen.pointee {
//            command.append(apduCommand.advanced(by: Int(i)).pointee)
//        }
//
//        for i in 0..<32 {
//            privKey.append(privateKey.advanced(by: i).pointee)
//        }
//
//        for i in 0..<64 {
//            pubKey.append(publicKey.advanced(by: i).pointee)
//        }
//
//        for i in 0..<32 {
//            sharedSecret.append(secretShses.advanced(by: i).pointee)
//        }

        return AuthInitData(
            apduCommand = command,
            privateKey = privKey,
            publicKey = pubKey,
            sharedSecret = sharedSecret
        )
    }

    /// Calculates secret keys.
    private fun calcSecretKeys(
        receivedPubKey: ByteArray,
        sharedSecret: ByteArray,
        privateKey: ByteArray
    ): Keys {
        val pubKey =
            Memory(receivedPubKey.size.toLong())//UnsafeMutablePointer<UInt8>.allocate(capacity: receivedPubKey.count)
        val shses =
            Memory(sharedSecret.size.toLong())//UnsafeMutablePointer<UInt8>.allocate(capacity: sharedSecret.count)
        val privatekey =
            Memory(privateKey.size.toLong())//UnsafeMutablePointer<UInt8>.allocate(capacity: privateKey.count)
        val keyRespt = Memory(16)//UnsafeMutablePointer<UInt8>.allocate(capacity: 16)
        val keyENC = Memory(16)//UnsafeMutablePointer<UInt8>.allocate(capacity: 16)
        val keyCMAC = Memory(16)//UnsafeMutablePointer<UInt8>.allocate(capacity: 16)
        val keyRMAC = Memory(16)//UnsafeMutablePointer<UInt8>.allocate(capacity: 16)
        val chaining = Memory(16)//UnsafeMutablePointer<UInt8>.allocate(capacity: 16)

//        defer {
//            pubKey.deallocate()
//            shses.deallocate()
//            privatekey.deallocate()
//            keyRespt.deallocate()
//            keyENC.deallocate()
//            keyCMAC.deallocate()
//            keyRMAC.deallocate()
//            chaining.deallocate()
//        }

        for (i in receivedPubKey.indices) {
            pubKey.setInt(i.toLong(), receivedPubKey[i].toInt())
        }

        for (i in sharedSecret.indices) {
            shses.setInt(i.toLong(), sharedSecret[i].toInt())
        }

        for (i in privateKey.indices) {
            privatekey.setInt(i.toLong(), privateKey[i].toInt())
        }


        val response = NativeLib.INSTANCE.LibCalcSecretKeys(
            pubKey,
            shses,
            privatekey,
            keyRespt,
            keyENC,
            keyCMAC,
            keyRMAC,
            chaining
        )

//        if response != SUCCESS {
//            if response == ERROR_KEYGENERATION {
//                throw SentrySDKError.keyGenerationError
//            }
//            if response == ERROR_SHAREDSECRETEXTRACTION {
//                throw SentrySDKError.sharedSecretExtractionError
//            }
//
//            // TODO: Fix once we've converted security to pure Swift
//            throw NSError(domain: "Unknown return value", code: -1)
//        }

        var respt: ByteArray = byteArrayOf()
        var enc: ByteArray = byteArrayOf()
        var cmac: ByteArray = byteArrayOf()
        var rmac: ByteArray = byteArrayOf()
        var chainVal: ByteArray = byteArrayOf()

//        for i in 0..<16 {
//            respt.append(keyRespt.advanced(by: i).pointee)
//        }
//
//        for i in 0..<16 {
//            enc.append(keyENC.advanced(by: i).pointee)
//        }
//
//        for i in 0..<16 {
//            cmac.append(keyCMAC.advanced(by: i).pointee)
//        }
//
//        for i in 0..<16 {
//            rmac.append(keyRMAC.advanced(by: i).pointee)
//        }
//
//        for i in 0..<16 {
//            chainVal.append(chaining.advanced(by: i).pointee)
//        }

        return Keys(
            keyRespt = respt,
            keyENC = enc,
            keyCMAC = cmac,
            keyRMAC = rmac,
            chainingValue = chainVal
        )
    }

    /**
    Initializes the Enroll applet by selecting the applet on the SentryCard and verifying the enroll code. If no enroll code is set, this sets the enroll code to the indicated value. Call this method before calling other methods in this unit that communicate with the Enroll applet.

    - Parameters:
    - tag: The `NFCISO7816` tag supplied by an NFC connection to which `APDU` commands are sent.
    - enrollCode: An array of `UInt8` bytes containing the enroll code digits. This array must be 4-6 bytes in length, and each byte must be in the range 0-9.

    This method can throw the following exceptions:
     * `SentrySDKError.enrollCodeLengthOutOfbounds` if the indicated `enrollCode` is less than four (4) characters or more than six (6) characters in length.
     * `SentrySDKError.apduCommandError` that contains the status word returned by the last failed `APDU` command.
     * `SentrySDKError.enrollCodeDigitOutOfBounds` if an enroll code digit is not in the range 0-9.
     * `SentrySDKError.secureChannelInitializationError` if `useSecureCommunication` is `true` but an error occurred initializing the secure communication encryption.
     * `SentrySDKError.secureCommunicationNotSupported` if `useSecureCommunication` is `true` but the version of the Enroll applet on the SentryCard does nto support secure communication (highly unlikely).

     */
    fun initializeEnroll(tag: NfcIso7816Tag, enrollCode: ByteArray) {
        var debugOutput = "----- BiometricsAPI Initialize Enroll - Enroll Code: ${enrollCode}\n"

        if (isDebugOutputVerbose) {
            print(debugOutput)
        }

        // sanity check - enroll code must be between 4 and 6 characters
        if (enrollCode.size < 4 || enrollCode.size > 6) {
            throw SentrySDKError.EnrollCodeLengthOutOfBounds
        }

        debugOutput += "     Selecting Enroll Applet\n"
        sendAndConfirm(
            apduCommand = APDUCommand.SELECT_ENROLL_APPLET.value,
            name = "Select Enroll Applet",
            tag = tag
        )

        // if using a secure channel, setup keys
        if (useSecureChannel) {
            debugOutput += "     Initializing Secure Channel\n"

            encryptionCounter = byteArrayOf()
            chainingValue = byteArrayOf()
            privateKey = byteArrayOf()
            publicKey = byteArrayOf()
            sharedSecret = byteArrayOf()
            keyRespt = byteArrayOf()
            keyENC = byteArrayOf()
            keyCMAC = byteArrayOf()
            keyRMAC = byteArrayOf()

            // initialize the secure channel. this sets up keys and encryption
            val authInfo = getAuthInitCommand()
            privateKey = authInfo.privateKey
            publicKey = authInfo.publicKey
            sharedSecret = authInfo.sharedSecret

            val securityInitResponse =
                sendAndConfirm(apduCommand = authInfo.apduCommand, name = "Auth Init", tag = tag)
            if (securityInitResponse.statusWord == APDUResponseCode.OPERATION_SUCCESSFUL.value) {
                val secretKeys = calcSecretKeys(
                    receivedPubKey = securityInitResponse.data,
                    sharedSecret = sharedSecret,
                    privateKey = privateKey
                )
//                keyRespt = secretKeys.k
            }
//            do {
//                val securityInitResponse = sendAndConfirm (apduCommand= authInfo.apduCommand, name= "Auth Init", tag = tag)
//
//                    if securityInitResponse.statusWord == APDUResponseCode.operationSuccessful.rawValue {
//                        let secretKeys = try calcSecretKeys(receivedPubKey: securityInitResponse. data . toArrayOfBytes (), sharedSecret: sharedSecret, privateKey: privateKey)
//
//                        keyRespt.append(contentsOf: secretKeys. keyRespt)
//                        keyENC.append(contentsOf: secretKeys. keyENC)
//                        keyCMAC.append(contentsOf: secretKeys. keyCMAC)
//                        keyRMAC.append(contentsOf: secretKeys. keyRMAC)
//                        chainingValue.append(contentsOf: secretKeys. chainingValue)
//                    } else {
//                        throw SentrySDKError.secureChannelInitializationError
//                    }
//                } catch SentrySDKError.apduCommandError( let errorCode) {
//                if () {
//                    errorCode == 0x6D00 {
//                        throw SentrySDKError.secureCommunicationNotSupported    // If we get an 'INS byte not supported', the enrollment applet doesn't support secure communication
//                    }
//                } else {
//                    throw SentrySDKError.apduCommandError(errorCode)
//                }
//                }
//
//                debugOutput += "     Verifing Enroll Code\n"
//                var enrollCodeCommand = try APDUCommand.verifyEnrollCode(code: enrollCode)
//                    enrollCodeCommand = try wrapAPDUCommand(
//                        apduCommand: enrollCodeCommand,
//                        keyENC: keyENC,
//                        keyCMAC: keyCMAC,
//                        chainingValue: &chainingValue, encryptionCounter: &encryptionCounter)
//                        try await sendAndConfirm (apduCommand: enrollCodeCommand, name: "Verify Enroll Code", to: tag)
//                        } else {
//            debugOutput += "     Verifing Enroll Code\n"
//            try await sendAndConfirm (apduCommand: APDUCommand.verifyEnrollCode(code: enrollCode), name: "Verify Enroll Code", to: tag)
//            }
//
//            debugOutput += "------------------------------\n"
        }
    }

    /// Sends an APDU command, throwing an exception if that command does not respond with a successful operation value.
    private fun sendAndConfirm(
        apduCommand: ByteArray,
        name: String? = null,
        tag: NfcIso7816Tag
    ): APDUReturnResult {
        val returnData = send(apduCommand = apduCommand, name = name, toTag = tag)

        if (returnData.statusWord != APDUResponseCode.OPERATION_SUCCESSFUL.value) {
            throw SentrySDKError.ApduCommandError(returnData.statusWord)
        }

        return returnData
    }


    /// Sends an APDU command.
    private fun send(
        apduCommand: ByteArray,
        name: String? = null,
        toTag: NfcIso7816Tag
    ): APDUReturnResult {
        var debugOutput = "\n---------- Sending ($name ??  -----------\n"

        if (isDebugOutputVerbose) {
            print(debugOutput)
        }

        debugOutput += "     >>> Sending => ${(apduCommand.formatted())}\n"

//
//    guard let command = NFCISO7816APDU(data: data) else {
//        throw SentrySDKError.invalidAPDUCommand
//    }
        val result = toTag.transceive(apduCommand)

        if (result.isSuccess) {
            return APDUReturnResult(result.getOrNull()!!, result.getOrNull()!![1].toInt())

        }
//    let result = try await
//
//        let resultData = result.0 + Data([result.1]) + Data([result.2])
//        debugOutput += "     <<< Received <= \(resultData.toHex())\n"
//
//        let statusWord: Int = Int(result.1) << 8 + Int(result.2)
//        return APDUReturnResult(data: result.0, statusWord: statusWord)
//    }
        TODO()
    }
}