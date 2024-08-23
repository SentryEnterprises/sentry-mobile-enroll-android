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
import com.sentryenterprises.sentry.sdk.models.NfcActionResult
import com.sentryenterprises.sentry.sdk.models.NfcIso7816Tag
import com.sentryenterprises.sentry.sdk.models.VersionInfo
import com.sentryenterprises.sentry.sdk.utils.asPointer
import com.sun.jna.Memory
import com.sun.jna.Pointer
import java.nio.ByteBuffer
import kotlin.Int
import kotlin.collections.get
import kotlin.collections.indices
import kotlin.compareTo
import kotlin.text.toInt


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
                ).getOrThrow()

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
            ).getOrThrow()
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

            if (securityInitResponse.isFailure) {
                throw SentrySDKError.SecureChannelInitializationError
            }

            val secretKeys =
                if (securityInitResponse.getOrThrow().statusWord == APDUResponseCode.OPERATION_SUCCESSFUL.value) {
                    calcSecretKeys(
                        receivedPubKey = securityInitResponse.getOrThrow().data,
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
    ): Result<APDUReturnResult> {
        val returnData = send(apduCommand = apduCommand, name = name, tag = tag)

        return if (returnData.isSuccess && returnData.getOrThrow().statusWord == APDUResponseCode.OPERATION_SUCCESSFUL.value) {
            returnData
        } else if (returnData.isSuccess){
            Result.failure(SentrySDKError.ApduCommandError(returnData.getOrThrow().statusWord))
        } else returnData
    }


    /// Sends an APDU command.
    private fun send(
        apduCommand: ByteArray,
        name: String? = null,
        tag: NfcIso7816Tag
    ): Result<APDUReturnResult> {


        println("     >>> Sending $name => ${(apduCommand.formatted())}\n")

//
//    guard let command = NFCISO7816APDU(data: data) else {
//        throw SentrySDKError.invalidAPDUCommand
//    }
        val result = tag.transceive(apduCommand)

        return if (result.isSuccess) {
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

            Result.success(
                APDUReturnResult(
                    data = result.getOrThrow().copyOf(resultArray.size - 2),
                    statusWord = statusWord
                )
            )

        } else {
            Result.failure(result.exceptionOrNull()!!)
        }


//    let result = try await
//
//        let resultData = result.0 + Data([result.1]) + Data([result.2])
//        debugOutput += "     <<< Received <= \(resultData.toHex())\n"
//
//        let statusWord: Int = Int(result.1) << 8 + Int(result.2)
//        return APDUReturnResult(data: result.0, statusWord: statusWord)
//    }
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

    fun resetBiometricData(tag: NfcIso7816Tag): NfcActionResult.ResetBiometrics {
        println("----- BiometricsAPI Reset BiometricData")

        try {
            sendAndConfirm(
                apduCommand = APDUCommand.RESET_BIOMETRIC_DATA.value,
                name = "Reset Biometric Data",
                tag = tag
            )

        } catch (e: SentrySDKError.ApduCommandError) {
            return if (e.code == APDUResponseCode.HOST_INTERFACE_TIMEOUT_EXPIRED.value) {
                NfcActionResult.ResetBiometrics.Failed("Operation Timeout")
            } else {
                NfcActionResult.ResetBiometrics.Failed("Reason code: ${e.code}")
            }
        }

        return NfcActionResult.ResetBiometrics.Success

    }


    /**
     * Retrieves the version of the Verify applet installed on the scanned card.
     *
     * @throws SentrySDKError.ApduCommandError containing the status word returned by the last failed `APDU` command.
     *
     */
    internal fun getVerifyAppletVersion(tag: NfcIso7816Tag): VersionInfo {
        // Note: Due to the way Apple implemented APDU communication, it's possible to send a select command and receive a 9000 response
        // even though the applet isn't actually installed on the card. The BioVerify applet has always supported a versioning command,
        // so here we'll simply check if the command was processes, and if we get an 'instruction byte not supported' response, we assume
        // the BioVerify applet isn't installed.

        var version = VersionInfo(
            isInstalled = false,
            majorVersion = -1,
            minorVersion = -1,
            hotfixVersion = -1,
            text = null
        )
        println("----- BiometricsAPI Get Verify Applet Version")

        println("     Selecting Verify Applet")

        send(
            apduCommand = APDUCommand.SELECT_VERIFY_APPLET.value,
            name = "Select Verify Applet",
            tag = tag
        )
        val response = send(
            apduCommand = APDUCommand.GET_VERIFY_APPLET_VERSION.value,
            name = "Get Verify Applet Version",
            tag = tag
        ).getOrThrow()

        if (response.statusWord == APDUResponseCode.OPERATION_SUCCESSFUL.value) {
            val responseBuffer = response.data

            if (responseBuffer.size == 5) {
                val majorVersion = responseBuffer[3].toInt()
                val minorVersion = responseBuffer[4].toInt()
                version = VersionInfo(
                    isInstalled = true,
                    majorVersion = majorVersion,
                    minorVersion = minorVersion,
                    hotfixVersion = 0,
                    text = null
                )
            } else if (responseBuffer.size == 4) {
                val majorVersion = responseBuffer[2].toInt()
                val minorVersion = responseBuffer[3].toInt()
                version = VersionInfo(
                    isInstalled = true,
                    majorVersion = majorVersion,
                    minorVersion = minorVersion,
                    hotfixVersion = 0,
                    text = null
                )
            } else if (responseBuffer.size == 2) {
                val majorVersion = responseBuffer[0].toInt()
                val minorVersion = responseBuffer[1].toInt()
                version = VersionInfo(
                    isInstalled = true,
                    majorVersion = majorVersion,
                    minorVersion = minorVersion,
                    hotfixVersion = 0,
                    text = null
                )
            }
        } else if (response.statusWord == APDUResponseCode.INSTRUCTION_BYTE_NOT_SUPPORTED.value) {
            version = VersionInfo(
                isInstalled = false,
                majorVersion = -1,
                minorVersion = -1,
                hotfixVersion = -1,
                text = null
            )
        } else {
            throw SentrySDKError.ApduCommandError(response.statusWord)
        }

        println("     Verify Applet Version: ${version.isInstalled} - ${version.majorVersion}.${version.minorVersion}.${version.hotfixVersion}")
        return version
    }

    /**
    Retrieves the version of the Enrollment applet installed on the scanned card (only available on version 2.0 or greater).

    - Note: If the Enrollment applet version on the card is earlier than 2.0, this returns -1 for all version values.

    - Parameters:
    - tag: The `NFCISO7816` tag supplied by an NFC connection to which `APDU` commands are sent.

    - Returns: A `VersionInfo` structure containing version information.

    This method can throw the following exceptions:
     * `SentrySDKError.apduCommandError` that contains the status word returned by the last failed `APDU` command.

     */
    fun getEnrollmentAppletVersion(tag: NfcIso7816Tag): VersionInfo {
        var version = VersionInfo(
            isInstalled = true,
            majorVersion = -1,
            minorVersion = -1,
            hotfixVersion = -1,
            text = null
        )
        println("----- BiometricsAPI Get Enrollment Applet Version")

        println("     Selecting Enroll Applet")

        try {
            val response = sendAndConfirm(
                apduCommand = APDUCommand.SELECT_ENROLL_APPLET.value,
                name = "Select Enroll Applet",
                tag = tag
            ).getOrThrow()

            val responseBuffer = response.data

            if (responseBuffer.size < 16) {
                return VersionInfo(
                    isInstalled = true,
                    majorVersion = -1,
                    minorVersion = -1,
                    hotfixVersion = -1,
                    text = null
                )
            } else {
                val string = responseBuffer.toString()
                val majorVersion = responseBuffer[13].toInt() - 0x30
                val minorVersion = responseBuffer[15].toInt() - 0x30
                version = VersionInfo(
                    isInstalled = true,
                    majorVersion = majorVersion,
                    minorVersion = minorVersion,
                    hotfixVersion = 0,
                    text = string
                )
            }
        } catch (e: Exception) {
//            if (error as NSError).domain == "NFCError" && (error as NSError).code == 2 {
//                version = VersionInfo(isInstalled= false, majorVersion= -1, minorVersion= -1, hotfixVersion= -1, text= null)
//            } else {
            throw e
//            }
        }

        println("     Enrollment Applet Version: ${version.isInstalled} - ${version.majorVersion}.${version.minorVersion}.${version.hotfixVersion}")
        return version
    }


    /**
    Retrieves the version of the CDCVM applet installed on the scanned card (only available on version 2.0 or greater).

    - Note: If the CDCVM applet version on the card is earlier than 2.0, this returns -1 for all version values.

    - Parameters:
    - tag: The `NFCISO7816` tag supplied by an NFC connection to which `APDU` commands are sent.

    - Returns: A `VersionInfo` structure containing version information.

    This method can throw the following exceptions:
     * `SentrySDKError.apduCommandError` that contains the status word returned by the last failed `APDU` command.

     */
    fun getCVMAppletVersion(tag: NfcIso7816Tag): VersionInfo {
        var version = VersionInfo(
            isInstalled = true,
            majorVersion = -1,
            minorVersion = -1,
            hotfixVersion = -1,
            text = null
        )
        var debugOutput = "----- BiometricsAPI Get CVM Applet Version\n"


        debugOutput += "     Selecting CVM Applet\n"

        try {
            val response = sendAndConfirm(
                apduCommand = APDUCommand.SELECT_CVM_APPLET.value,
                name = "Select CVM Applet",
                tag = tag
            ).getOrThrow()

            val responseBuffer = response.data

            if (responseBuffer.size > 11) {
                val string = responseBuffer.toString()
                val majorVersion = responseBuffer[10].toInt() - 0x30
                val minorVersion = responseBuffer[12].toInt() - 0x30
                version = VersionInfo(
                    isInstalled = true,
                    majorVersion = majorVersion,
                    minorVersion = minorVersion,
                    hotfixVersion = 0,
                    text = string
                )
            }
        } catch (e: Exception) {
//                if (error as NSError).domain == "NFCError" && (error as NSError).code == 2 {
//                    version = VersionInfo(isInstalled: false, majorVersion: -1, minorVersion: -1, hotfixVersion: -1, text: nil)
//                } else {
            throw e
//                }
        }

        println("     CVM Applet Version: ${version.isInstalled} - ${version.majorVersion}.${version.minorVersion}.${version.hotfixVersion}")
        return version
    }


    /**
    Scans the finger currently on the fingerprint sensor, indicating if the scanned fingerprint matches one recorded during enrollment.

    - Parameters:
    - tag: The `NFCISO7816` tag supplied by an NFC connection to which `APDU` commands are sent.

    - Returns: `True` if the scanned fingerprint matches one recorded during enrollment, otherwise returns `false`.

    This method can throw the following exceptions:
     * `SentrySDKError.apduCommandError` that contains the status word returned by the last failed `APDU` command.
     * `SentrySDKError.cvmAppletNotAvailable` if the CVM applet was unavailable for some reason.
     * `SentrySDKError.cvmAppletBlocked` if the CVM applet is in a blocked state and can no longer be used.
     * `SentrySDKError.cvmAppletError` if the CVM applet returned an unexpected error code.

     */
    fun getFingerprintVerification(tag: NfcIso7816Tag): NfcActionResult.VerifyBiometric {

        // TODO: !!! implement encryption !!!

        println("----- BiometricsAPI Get Fingerprint Verification")


        val returnData = send(
            apduCommand = APDUCommand.GET_FINGERPRINT_VERIFY.value,
            name = "Fingerprint Verification",
            tag = tag
        ).getOrThrow()

        if (returnData.statusWord == APDUResponseCode.OPERATION_SUCCESSFUL.value) {
            if (returnData.data[3] == 0x00.toByte()) {
                throw SentrySDKError.CvmAppletNotAvailable
            }

            if (returnData.data[5] == 0x01.toByte()) {
                throw SentrySDKError.CvmAppletBlocked
            }

            if (returnData.data[4] == 0xA5.toByte()) {
                println("     Match")
                return NfcActionResult.VerifyBiometric(true)
            }

            if (returnData.data[4] == 0x5A.toByte()) {
                println("     No match found")
                return NfcActionResult.VerifyBiometric(false)
            }

            throw SentrySDKError.CvmAppletError(returnData.data[4].toInt())
        }

        throw SentrySDKError.ApduCommandError(returnData.statusWord)
    }

    fun getCardOSVersion(tag: NfcIso7816Tag): VersionInfo {
        println("----- BiometricsAPI Get Card OS Version")

        println("     Getting card OS version")
        val returnData = sendAndConfirm(
            apduCommand = APDUCommand.GET_OS_VERSION.value,
            name = "Get Card OS Version",
            tag = tag
        ).getOrThrow()

        println("     Processing response")
        val dataBuffer = returnData.data

        if (dataBuffer.size < 8) {
            throw SentrySDKError.CardOSVersionError
        }

        if (dataBuffer[0] != 0xFE.toByte()) {
            throw SentrySDKError.CardOSVersionError
        }
        if (dataBuffer[1] < 0x40.toByte()) {
            throw SentrySDKError.CardOSVersionError
        }
        if (dataBuffer[2] != 0x7f.toByte()) {
            throw SentrySDKError.CardOSVersionError
        }
        if (dataBuffer[3] != 0x00.toByte()) {
            throw SentrySDKError.CardOSVersionError
        }
        if (dataBuffer[4] < 0x40.toByte()) {
            throw SentrySDKError.CardOSVersionError
        }
        if (dataBuffer[5] != 0x9f.toByte()) {
            throw SentrySDKError.CardOSVersionError
        }
        if (dataBuffer[6] != 0x01.toByte()) {
            throw SentrySDKError.CardOSVersionError
        }

        val n = dataBuffer[7]
        var p: Int = 8 + n

        if (dataBuffer[p] != 0x9F.toByte()) {
            throw SentrySDKError.CardOSVersionError
        }
        p += 1
        if (dataBuffer[p] != 0x02.toByte()) {
            throw SentrySDKError.CardOSVersionError
        }
        p += 1
        if (dataBuffer[p].toInt() != 5) {
            throw SentrySDKError.CardOSVersionError
        }
        p += 1

        val major = dataBuffer[p] - 0x30
        p += 2
        val minor = dataBuffer[p] - 0x30
        p += 2
        val hotfix = dataBuffer[p] - 0x30

        val retVal = VersionInfo(
            isInstalled = true,
            majorVersion = major,
            minorVersion = minor,
            hotfixVersion = hotfix,
            text = null
        )

        println("     Card OS Version: ${retVal.majorVersion}.${retVal.minorVersion}.${retVal.hotfixVersion}")
        return retVal
    }
}
