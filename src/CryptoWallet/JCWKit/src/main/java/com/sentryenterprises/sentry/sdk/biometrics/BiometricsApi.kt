package com.sentryenterprises.sentry.sdk.biometrics


import com.secure.jnet.jcwkit.NativeLib
import com.secure.jnet.jcwkit.utils.formatted
import com.secure.jnet.wallet.presentation.APDUCommand
import com.secure.jnet.wallet.presentation.SentrySDKError
import com.sentryenterprises.sentry.sdk.apdu.APDUResponseCode
import com.sentryenterprises.sentry.sdk.biometrics.SUCCESS
import com.sentryenterprises.sentry.sdk.models.AuthInitData
import com.sentryenterprises.sentry.sdk.models.BiometricEnrollmentStatus
import com.sentryenterprises.sentry.sdk.models.BiometricMode
import com.sentryenterprises.sentry.sdk.models.Keys
import com.sentryenterprises.sentry.sdk.models.NfcIso7816Tag
import com.sentryenterprises.sentry.sdk.utils.asPointer
import com.sun.jna.Memory
import com.sun.jna.Pointer
import java.nio.ByteBuffer
import kotlin.Int
import kotlin.collections.indices


// A `tuple` containing an `APDU` command result data buffer and a status word.
private data class APDUReturnResult(val data: ByteArray, val statusWord: Int)

private const val SUCCESS = 0

private const val ERROR_KEYGENERATION = -100
private const val ERROR_SHAREDSECRETEXTRACTION = -101
private const val ERROR_INVALIDPARAMETER = -1
private const val ERROR_CRITERION = -5

/**
Communicates with the IDEX Enroll applet by sending various `APDU` commands in the appropriate order.
 */
internal class BiometricsApi(
    val isDebugOutputVerbose: Boolean = true,
    val useSecureChannel: Boolean = true,
) {

    // Note - This is reset when selecting a new applet (i.e. after initing the secure channel)
    private var encryptionCounter: ByteArray = ByteArray(16) { 0 }

    // Note - this changes with every wrap, and resets when initing secure channel
    private var chainingValue: ByteArray = ByteArray(16) { 0 }


    private var privateKey: ByteArray = byteArrayOf()
    private var publicKey: ByteArray = byteArrayOf()
    private var sharedSecret: ByteArray = byteArrayOf()
    private var keyRespt: ByteArray = byteArrayOf()
    private var keyENC: ByteArray = byteArrayOf()
    private var keyCMAC: ByteArray = byteArrayOf()
    private var keyRMAC: ByteArray = byteArrayOf()

    data class WrapAPDUCommandResponse(
        val encryptionCounter: ByteArray,
        val chainingValue: ByteArray,
        val wrapped: ByteArray
    )

    // Encodes an APDU command.
    private fun wrapAPDUCommand(
        apduCommand: ByteArray,
        keyEnc: ByteArray,
        keyCmac: ByteArray,
        chainingValue: ByteArray,
        encryptionCounter: ByteArray
    ): WrapAPDUCommandResponse {

        println("calcSecretKeys encryptionCounter ${encryptionCounter.formatted()} ")

        val command = apduCommand.asPointer()
        val wrappedCommand = Memory(300)
        val enc = keyEnc.asPointer()
        val cmac = keyCmac.asPointer()
        val chaining = chainingValue.asPointer()
        val counter = encryptionCounter.asPointer()
        val wrappedLength = Memory(1)

        val response = NativeLib.INSTANCE.LibAuthWrap(
            command,
            apduCommand.size,
            wrappedCommand,
            wrappedLength,
            enc,
            cmac,
            chaining,
            counter
        )


        if (response != SUCCESS) {
            if (response == ERROR_KEYGENERATION) {
                throw SentrySDKError.KeyGenerationError
            }
            if (response == ERROR_SHAREDSECRETEXTRACTION) {
                throw SentrySDKError.SharedSecretExtractionError
            }

            // TODO: Fix once we've converted security to pure Swift
            error("Unknown return value $response")
        }

        counter.getByteArray(0, 16).copyInto(encryptionCounter)
        chaining.getByteArray(0, 16).copyInto(chainingValue)

        return WrapAPDUCommandResponse(
            encryptionCounter = counter.getByteArray(0, encryptionCounter.size),
            chainingValue = chaining.getByteArray(0, chainingValue.size),
            wrapped = wrappedCommand.getByteArray(0, wrappedLength.getByte(0).toInt())
        )
    }

    /**
    Retrieves the biometric enrollment status recorded by the Enrollment applet on the card.

    - Parameters:
    - tag: The `NFCISO7816` tag supplied by an NFC connection to which `APDU` commands are sent.

    - Returns: A `BiometricEnrollmentStatus` structure containing information on the fingerprint enrollment status.

    This method can throw the following exceptions:
     * `SentrySDKError.enrollmentStatusBufferTooSmall` if the buffer returned from the `APDU` command was unexpectedly too small.
     * `SentrySDKError.apduCommandError` that contains the status word returned by the last failed `APDU` command.

     */
    fun getEnrollmentStatus(tag: NfcIso7816Tag): BiometricEnrollmentStatus {
        println("----- BiometricsAPI Get Enrollment Status")
        var dataArray: ByteArray = byteArrayOf()

//        defer {
//            if isDebugOutputVerbose { print(debugOutput) }
//        }

        println("     Getting enrollment status")

        if (useSecureChannel) {
            val enrollStatusCommand = wrapAPDUCommand(
                apduCommand = APDUCommand.GET_ENROLL_STATUS.value,
                keyEnc = keyENC,
                keyCmac = keyCMAC,
                chainingValue = chainingValue,
                encryptionCounter = encryptionCounter
            )
            val returnData =
                send(
                    apduCommand = enrollStatusCommand.wrapped,
                    name = "Get Enroll Status",
                    tag = tag
                )

            if (returnData.statusWord != APDUResponseCode.OPERATION_SUCCESSFUL.value) {
                throw SentrySDKError.ApduCommandError(returnData.statusWord)
            }

            dataArray = unwrapAPDUResponse(
                response = returnData.data,
                statusWord = returnData.statusWord,
                chainingValue = chainingValue,
                encryptionCounter = encryptionCounter
            )
        } else {
            val returnData = sendAndConfirm(
                apduCommand = APDUCommand.GET_ENROLL_STATUS.value,
                name = "Get Enrollment Status",
                tag = tag
            )
            dataArray = returnData.data
        }

        // sanity check - this buffer should be at least 40 bytes in length, possibly more
        if (dataArray.size < 40) {
            throw SentrySDKError.EnrollmentStatusBufferTooSmall
        }

        // extract values from specific index in the array
        val maxNumberOfFingers = dataArray[31]
        val enrolledTouches = dataArray[32]
        val remainingTouches = dataArray[33]
        val mode = dataArray[39]

        println(
            "     # Fingers: $maxNumberOfFingers\n" +
                    "     Enrolled Touches: $enrolledTouches\n" +
                    "     Remaining Touches: $remainingTouches\n" +
                    "     Mode: $mode\n"
        )
        val biometricMode: BiometricMode = if (mode == 0.toByte()) {
            BiometricMode.Enrollment
        } else {
            BiometricMode.Verification
        }


        println("-----------------------------")

        return BiometricEnrollmentStatus(
            maximumFingers = maxNumberOfFingers.toInt(),
            enrolledTouches = enrolledTouches.toInt(),
            remainingTouches = remainingTouches.toInt(),
            mode = biometricMode
        )
    }


    /**
     * Decodes an APDU command response.
     */
    private fun unwrapAPDUResponse(
        response: ByteArray,
        statusWord: Int,
        chainingValue: ByteArray,
        encryptionCounter: ByteArray
    ): ByteArray {
        val responseData = Memory(response.size + 2L).apply {
            response.forEachIndexed { index, i ->
                setByte(index.toLong(), i.toByte())
            }
            setByte(response.size.toLong(), (statusWord shr 8).toByte())
            setByte(response.size + 1L, (statusWord and 0x00FF).toByte())
        }

        println("unwrapAPDUResponse response ${response.formatted()}")
        println(
            "unwrapAPDUResponse responseData ${
                responseData.getByteArray(0, response.size + 2).formatted()
            }"
        )

        val unwrappedResponse = Memory(300)
        val unwrappedLength = Memory(1)

        val response = NativeLib.INSTANCE.LibAuthUnwrap(
            apdu_in = responseData,
            in_len = response.size + 2,
            apdu_out = unwrappedResponse,
            out_len = unwrappedLength,
            key_enc = keyENC.asPointer(),
            key_rmac = keyRMAC.asPointer(),
            chaining_value = chainingValue.asPointer(),
            encryption_counter = encryptionCounter.asPointer()
        )

        if (response != SUCCESS) {
            if (response == ERROR_KEYGENERATION) {
                throw SentrySDKError.KeyGenerationError
            }
            if (response == ERROR_SHAREDSECRETEXTRACTION) {
                throw SentrySDKError.SharedSecretExtractionError
            }

            // TODO: Fix once we've converted security to pure Swift
            error("Unknown return value $response")
        }

        return unwrappedResponse.getByteArray(0, unwrappedLength.getByte(0).toInt())
    }

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
    @OptIn(ExperimentalStdlibApi::class)
    private fun getAuthInitCommand(): AuthInitData {
        val apduCommand: Pointer = Memory(100)
        val apduCommandLen: Pointer = Memory(1)
        val privateKey: Pointer = Memory(32)
        val publicKey: Pointer = Memory(64)
        val secretShses: Pointer = Memory(32)

        println("LibSecureChannelInit")

        val response = NativeLib.INSTANCE.LibSecureChannelInit(
            apduCommand,
            apduCommandLen,
            privateKey,
            publicKey,
            secretShses
        )
        if (response != SUCCESS) {
            if (response == ERROR_KEYGENERATION) {
                throw SentrySDKError.KeyGenerationError
            }
            if (response == ERROR_SHAREDSECRETEXTRACTION) {
                throw SentrySDKError.SharedSecretExtractionError
            }

            // TODO: Fix once we've converted security to pure Swift
            error("Unknown return value $response")
        }

        return AuthInitData(
            apduCommand = apduCommand.getByteArray(0, apduCommandLen.getByte(0).toInt()),
            privateKey = privateKey.getByteArray(0, 32),
            publicKey = publicKey.getByteArray(0, 64),
            sharedSecret = secretShses.getByteArray(0, 32),
        )
    }

    /// Calculates secret keys.
    @OptIn(ExperimentalStdlibApi::class)
    private fun calcSecretKeys(
        receivedPubKey: ByteArray,
        sharedSecret: ByteArray,
        privateKey: ByteArray
    ): Keys {

        val keyRespt = Memory(16)//UnsafeMutablePointer<UInt8>.allocate(capacity: 16)
        val keyENC = Memory(16)//UnsafeMutablePointer<UInt8>.allocate(capacity: 16)
        val keyCMAC = Memory(16)//UnsafeMutablePointer<UInt8>.allocate(capacity: 16)
        val keyRMAC = Memory(16)//UnsafeMutablePointer<UInt8>.allocate(capacity: 16)
        val chaining = Memory(16)//UnsafeMutablePointer<UInt8>.allocate(capacity: 16)


        val response = NativeLib.INSTANCE.LibCalcSecretKeys(
            pubKey = receivedPubKey.asPointer(),
            shses = sharedSecret.asPointer(),
            privatekey = privateKey.asPointer(),
            keyRespt = keyRespt,
            keyENC = keyENC,
            keyCMAC = keyCMAC,
            keyRMAC = keyRMAC,
            chaining = chaining
        )

        if (response != SUCCESS) {
            if (response == ERROR_KEYGENERATION) {
                throw SentrySDKError.KeyGenerationError
            }
            if (response == ERROR_SHAREDSECRETEXTRACTION) {
                throw SentrySDKError.SharedSecretExtractionError
            }

            // TODO: Fix once we've converted security to pure Swift
            error("Unknown return value ${response.toByte().toHexString()}")
        }

        println("calcSecretKeys ")

        return Keys(
            keyRespt = keyRespt.getByteArray(0, 16),
            keyENC = keyENC.getByteArray(0, 16).also {
                println("keyEnc ${it.formatted()}")
            },
            keyCMAC = keyCMAC.getByteArray(0, 16),
            keyRMAC = keyRMAC.getByteArray(0, 16),
            chainingValue = chaining.getByteArray(0, 16)
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

            encryptionCounter = ByteArray(16) { 0 }
            chainingValue = byteArrayOf()
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
            val secretKeys =
                if (securityInitResponse.statusWord == APDUResponseCode.OPERATION_SUCCESSFUL.value) {
                    calcSecretKeys(
                        receivedPubKey = securityInitResponse.data,
                        sharedSecret = sharedSecret,
                        privateKey = privateKey
                    )


                } else {
                    throw SentrySDKError.SecureChannelInitializationError
                }
            keyRespt = secretKeys.keyRespt
            keyENC = secretKeys.keyENC
            keyCMAC = secretKeys.keyCMAC
            keyRMAC = secretKeys.keyRMAC
            chainingValue = secretKeys.chainingValue

            val enrollCodeCommand = wrapAPDUCommand(
                apduCommand = APDUCommand.verifyEnrollCode(enrollCode),
                keyEnc = secretKeys.keyENC,
                keyCmac = secretKeys.keyCMAC,
                chainingValue = secretKeys.chainingValue,
                encryptionCounter = encryptionCounter
            )
            sendAndConfirm(enrollCodeCommand.wrapped, "Verify Enroll Code", tag = tag)

        } else {
            TODO("are we using non secure channels?")
        }
    }

    /// Sends an APDU command, throwing an exception if that command does not respond with a successful operation value.
    private fun sendAndConfirm(
        apduCommand: ByteArray,
        name: String? = null,
        tag: NfcIso7816Tag
    ): APDUReturnResult {
        val returnData = send(apduCommand = apduCommand, name = name, tag = tag)

        if (returnData.statusWord != APDUResponseCode.OPERATION_SUCCESSFUL.value) {
            throw SentrySDKError.ApduCommandError(returnData.statusWord)
        }

        return returnData
    }


    /// Sends an APDU command.
    private fun send(
        apduCommand: ByteArray,
        name: String? = null,
        tag: NfcIso7816Tag
    ): APDUReturnResult {


        println("     >>> Sending $name => ${(apduCommand.formatted())}\n")

//
//    guard let command = NFCISO7816APDU(data: data) else {
//        throw SentrySDKError.invalidAPDUCommand
//    }
        val result = tag.transceive(apduCommand)

        if (result.isSuccess) {
            println("     >>> Received $name => ${(result.getOrNull()?.formatted())}\n")

            val resultArray = result.getOrThrow()
            val statusWord =
                ByteBuffer.wrap(
                    byteArrayOf(
                        0x00,
                        0x00,
                        resultArray[resultArray.size - 2],
                        resultArray.last()
                    )
                ).int

            return APDUReturnResult(
                data = result.getOrThrow().copyOf(resultArray.size - 2),
                statusWord = statusWord
            )

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

    fun resetEnrollAndScanFingerprint(tag: NfcIso7816Tag): Int {
        println("----- BiometricsAPI Reset Enroll and Scan Fingerprint")


        if (useSecureChannel) {
            val processFingerprintCommand = wrapAPDUCommand(
                apduCommand = APDUCommand.RESTART_ENROLL_AND_PROCESS_FINGERPRINT.value,
                keyEnc = keyENC,
                keyCmac = keyCMAC,
                chainingValue = chainingValue,
                encryptionCounter = encryptionCounter
            )
            sendAndConfirm(
                apduCommand = processFingerprintCommand.wrapped,
                name = "Reset And Process Fingerprint",
                tag = tag
            )
        } else {
            sendAndConfirm(
                apduCommand = APDUCommand.RESTART_ENROLL_AND_PROCESS_FINGERPRINT.value,
                name = "Reset And Process Fingerprint",
                tag = tag
            )
        }

        println("     Getting enrollment status")
        val enrollmentStatus = getEnrollmentStatus(tag = tag)

        println("     Remaining: ${enrollmentStatus.remainingTouches}")
        return enrollmentStatus.remainingTouches.toInt()
    }

    fun enrollScanFingerprint(tag: NfcIso7816Tag): Int {
        println("----- BiometricsAPI Enroll Scan Fingerprint")

        if (useSecureChannel) {
            val processFingerprintCommand = wrapAPDUCommand(
                apduCommand = APDUCommand.PROCESS_FINGERPRINT.value,
                keyEnc = keyENC,
                keyCmac = keyCMAC,
                chainingValue = chainingValue,
                encryptionCounter = encryptionCounter
            )
            sendAndConfirm(
                apduCommand = processFingerprintCommand.wrapped,
                name = "Process Fingerprint",
                tag = tag
            )
        } else {
            sendAndConfirm(
                apduCommand = APDUCommand.PROCESS_FINGERPRINT.value,
                name = "Process Fingerprint",
                tag = tag
            )
        }
        println("     Getting enrollment status")

        val enrollmentStatus = getEnrollmentStatus(tag = tag)

        println("     Remaining: ${enrollmentStatus.remainingTouches}")
        return enrollmentStatus.remainingTouches.toInt()
    }

    fun verifyEnrolledFingerprint(tag: NfcIso7816Tag) {
        println("----- BiometricsAPI Verify Enrolled Fingerprint")

        if (useSecureChannel) {
            val verifyEnrollCommand = wrapAPDUCommand(
                apduCommand = APDUCommand.VERIFY_FINGERPRINT_ENROLLMENT.value,
                keyEnc = keyENC,
                keyCmac = keyCMAC,
                chainingValue = chainingValue,
                encryptionCounter = encryptionCounter
            )
            sendAndConfirm(
                apduCommand = verifyEnrollCommand.wrapped,
                name = "Verify Enrolled Fingerprint",
                tag = tag
            )
        } else {
            sendAndConfirm(
                apduCommand = APDUCommand.VERIFY_FINGERPRINT_ENROLLMENT.value,
                name = "Verify Enrolled Fingerprint",
                tag = tag
            )
        }

    }
}