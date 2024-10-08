package com.sentryenterprises.sentry.sdk.biometrics


import android.nfc.Tag
import android.nfc.TagLostException
import android.nfc.tech.IsoDep
import com.secure.jnet.jcwkit.NativeLib
import com.sentryenterprises.sentry.sdk.apdu.APDUCommand
import com.sentryenterprises.sentry.sdk.presentation.SentrySDKError
import com.sentryenterprises.sentry.sdk.apdu.APDUResponseCode
import com.sentryenterprises.sentry.sdk.apdu.getDecodedMessage
import com.sentryenterprises.sentry.sdk.models.AuthInitData
import com.sentryenterprises.sentry.sdk.models.BiometricEnrollmentStatus
import com.sentryenterprises.sentry.sdk.models.BiometricMode
import com.sentryenterprises.sentry.sdk.models.Keys
import com.sentryenterprises.sentry.sdk.models.NfcActionResult
import com.sentryenterprises.sentry.sdk.models.VersionInfo
import com.sentryenterprises.sentry.sdk.utils.asPointer
import com.sentryenterprises.sentry.sdk.utils.formatted
import com.sun.jna.Memory
import com.sun.jna.Pointer
import java.nio.ByteBuffer
import kotlin.Int


private data class APDUReturnResult(val data: ByteArray, val statusWord: Int)

private const val SUCCESS = 0

private const val ERROR_KEYGENERATION = -100
private const val ERROR_SHAREDSECRETEXTRACTION = -101

/**
Communicates with the IDEX Enroll applet by sending various `APDU` commands in the appropriate order.
 */
internal class BiometricsApi(
    val isDebugOutputVerbose: Boolean = true,
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

    private fun wrapAPDUCommand(
        apduCommand: ByteArray,
        keyEnc: ByteArray,
        keyCmac: ByteArray,
        chainingValue: ByteArray,
        encryptionCounter: ByteArray
    ): WrapAPDUCommandResponse {

        log("calcSecretKeys encryptionCounter ${encryptionCounter.formatted()} ")

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
     * Retrieves the biometric enrollment status recorded by the Enrollment applet on the card.
     *
     * @param tag The `NFCISO7816` tag supplied by an NFC connection to which `APDU` commands are sent.
     * @return BiometricEnrollmentStatus structure containing information on the fingerprint enrollment status.
     *
     * This method can throw the following exceptions:
     * `SentrySDKError.enrollmentStatusBufferTooSmall` if the buffer returned from the `APDU` command was unexpectedly too small.
     * `SentrySDKError.apduCommandError` that contains the status word returned by the last failed `APDU` command.

     */
    fun getEnrollmentStatus(tag: Tag): Result<BiometricEnrollmentStatus> {
        log("----- BiometricsAPI Get Enrollment Status")
        var dataArray: ByteArray = byteArrayOf()

        log("     Getting enrollment status")

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
            return Result.failure(SentrySDKError.ApduCommandError(returnData.statusWord))
        }

        dataArray = unwrapAPDUResponse(
            response = returnData.data,
            statusWord = returnData.statusWord,
            chainingValue = chainingValue,
            encryptionCounter = encryptionCounter
        )


        // sanity check - this buffer should be at least 40 bytes in length, possibly more
        if (dataArray.size < 40) {
            throw SentrySDKError.EnrollmentStatusBufferTooSmall
        }

        // extract values from specific index in the array
        val maxNumberOfFingers = dataArray[31]
        val enrolledTouches = dataArray[32]
        val remainingTouches = dataArray[33]
        val mode = dataArray[39]

        log(
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


        log("-----------------------------")

        return Result.success(
            BiometricEnrollmentStatus(
                maximumFingers = maxNumberOfFingers.toInt(),
                enrolledTouches = enrolledTouches.toInt().also { println("Enrolled touches: $it") },
                remainingTouches = remainingTouches.toInt(),
                mode = biometricMode
            )
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

        log("unwrapAPDUResponse response ${response.formatted()}")
        log(
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
     * Initializes the BioVerify applet by selecting the applet on the SentryCard. Call this method before calling other methods in this unit that communicate with the BioVerify applet.
     * The BioVerify applet does not currently support secure communication, so a secure channel is not setup during initialization.
     *
     * @param tag The `NFCISO7816` tag supplied by an NFC connection to which `APDU` commands are sent.
     *
     * This method can throw the following exceptions:
     * `SentrySDKError.apduCommandError` that contains the status word returned by the last failed `APDU` command.
     * `SentrySDKError.secureChannelInitializationError` error occurred initializing the secure communication encryption.
     * `SentrySDKError.secureCommunicationNotSupported` the version of the BioVerify applet on the SentryCard does nto support secure communication (highly unlikely).

     */
    fun initializeVerify(tag: Tag) {

        log("----- BiometricsAPI Initialize Verify")
        log("     Selecting Verify Applet")

        APDUCommand.SELECT_VERIFY_APPLET
        sendAndConfirm(
            apduCommand = APDUCommand.SELECT_VERIFY_APPLET.value,
            name = "Select Verify Applet",
            tag = tag
        )
    }


    @OptIn(ExperimentalStdlibApi::class)
    private fun getAuthInitCommand(): AuthInitData {
        val apduCommand: Pointer = Memory(100)
        val apduCommandLen: Pointer = Memory(1)
        val privateKey: Pointer = Memory(32)
        val publicKey: Pointer = Memory(64)
        val secretShses: Pointer = Memory(32)

        log("LibSecureChannelInit")

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

    @OptIn(ExperimentalStdlibApi::class)
    private fun calcSecretKeys(
        receivedPubKey: ByteArray,
        sharedSecret: ByteArray,
        privateKey: ByteArray
    ): Keys {

        val keyRespt = Memory(16)
        val keyENC = Memory(16)
        val keyCMAC = Memory(16)
        val keyRMAC = Memory(16)
        val chaining = Memory(16)


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

        log("calcSecretKeys ")

        return Keys(
            keyRespt = keyRespt.getByteArray(0, 16),
            keyENC = keyENC.getByteArray(0, 16).also {
                log("keyEnc ${it.formatted()}")
            },
            keyCMAC = keyCMAC.getByteArray(0, 16),
            keyRMAC = keyRMAC.getByteArray(0, 16),
            chainingValue = chaining.getByteArray(0, 16)
        )
    }

    /**
     * Initializes the Enroll applet by selecting the applet on the SentryCard and verifying the enroll code. If no enroll code is set, this sets the enroll code to the indicated value. Call this method before calling other methods in this unit that communicate with the Enroll applet.
     *
     * @param tag The `NFCISO7816` tag supplied by an NFC connection to which `APDU` commands are sent.
     * @param enrollCode An array of `UInt8` bytes containing the enroll code digits. This array must be 4-6 bytes in length, and each byte must be in the range 0-9.
     *
     * This method can throw the following exceptions:
     * `SentrySDKError.enrollCodeLengthOutOfbounds` if the indicated `enrollCode` is less than four (4) characters or more than six (6) characters in length.
     * `SentrySDKError.apduCommandError` that contains the status word returned by the last failed `APDU` command.
     * `SentrySDKError.enrollCodeDigitOutOfBounds` if an enroll code digit is not in the range 0-9.
     * `SentrySDKError.secureChannelInitializationError` error occurred initializing the secure communication encryption.
     * `SentrySDKError.secureCommunicationNotSupported` the version of the Enroll applet on the SentryCard does nto support secure communication (highly unlikely).

     */
    fun initializeEnroll(tag: Tag, enrollCode: ByteArray) {
        log("----- BiometricsAPI Initialize Enroll - Enroll Code: $enrollCode")

        // sanity check - enroll code must be between 4 and 6 characters
        if (enrollCode.size < 4 || enrollCode.size > 6) {
            throw SentrySDKError.EnrollCodeLengthOutOfBounds
        }

        log("     Selecting Enroll Applet")
        sendAndConfirm(
            apduCommand = APDUCommand.SELECT_ENROLL_APPLET.value,
            name = "Select Enroll Applet",
            tag = tag
        )

        // if using a secure channel, setup keys
        log("     Initializing Secure Channel")

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
            securityInitResponse.exceptionOrNull()?.printStackTrace()
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
    }

    /// Sends an APDU command, throwing an exception if that command does not respond with a successful operation value.
    private fun sendAndConfirm(
        apduCommand: ByteArray,
        name: String? = null,
        tag: Tag,
    ): Result<APDUReturnResult> {
        val returnData = send(apduCommand = apduCommand, name = name, tag = tag)

        return if (returnData.isSuccess && returnData.getOrThrow().statusWord == APDUResponseCode.OPERATION_SUCCESSFUL.value) {
            returnData
        } else if (returnData.isSuccess) {
            Result.failure(SentrySDKError.ApduCommandError(returnData.getOrThrow().statusWord))
        } else returnData
    }


    private fun send(
        apduCommand: ByteArray,
        name: String? = null,
        tag: Tag
    ): Result<APDUReturnResult> {
        log("     >>> Sending $name => ${(apduCommand.formatted())}\n")

        val result = tag.transceive(apduCommand)

        return if (result.isSuccess) {
            log("     >>> Received $name => ${(result.getOrNull()?.formatted())}\n")

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

    }

    fun resetEnrollAndScanFingerprint(tag: Tag): Result<BiometricEnrollmentStatus> {
        log("----- BiometricsAPI Reset Enroll and Scan Fingerprint")

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

        log("     Getting enrollment status")
        val enrollmentStatus = getEnrollmentStatus(tag = tag).getOrElse {
            return Result.failure(it)
        }

        log("     Remaining: ${enrollmentStatus.remainingTouches}")
        return Result.success(enrollmentStatus)
    }

    fun enrollScanFingerprint(tag: Tag): Result<BiometricEnrollmentStatus> {
        log("----- BiometricsAPI Enroll Scan Fingerprint")

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

        log("     Getting enrollment status")
        val enrollmentStatus = getEnrollmentStatus(tag = tag).getOrThrow()

        log("     Remaining: ${enrollmentStatus.remainingTouches}")
        return Result.success(enrollmentStatus)
    }

    fun verifyEnrolledFingerprint(tag: Tag) {
        log("----- BiometricsAPI Verify Enrolled Fingerprint")

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


    }

    fun resetBiometricData(tag: Tag): NfcActionResult.ResetBiometrics {
        log("----- BiometricsAPI Reset BiometricData")

        val result = try {
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

        return if (result.isSuccess) {
            NfcActionResult.ResetBiometrics.Success
        } else {
            NfcActionResult.ResetBiometrics.Failed(
                result.exceptionOrNull().getDecodedMessage()
            )
        }

    }


    /**
     * Retrieves the version of the Verify applet installed on the scanned card.
     *
     * @throws SentrySDKError.ApduCommandError containing the status word returned by the last failed `APDU` command.
     *
     */
    internal fun getVerifyAppletVersion(tag: Tag): Result<VersionInfo> {
        // Note: Due to the way Apple implemented APDU communication, it's possible to send a select command and receive a 9000 response
        // even though the applet isn't actually installed on the card. The BioVerify applet has always supported a versioning command,
        // so here we'll simply check if the command was processes, and if we get an 'instruction byte not supported' response, we assume
        // the BioVerify applet isn't installed.

        log("----- BiometricsAPI Get Verify Applet Version")
        log("     Selecting Verify Applet")

        send(
            apduCommand = APDUCommand.SELECT_VERIFY_APPLET.value,
            name = "Select Verify Applet",
            tag = tag
        )
        val response = send(
            apduCommand = APDUCommand.GET_VERIFY_APPLET_VERSION.value,
            name = "Get Verify Applet Version",
            tag = tag
        ).getOrElse { return Result.failure(it) }

        return if (response.statusWord == APDUResponseCode.OPERATION_SUCCESSFUL.value) {
            val responseBuffer = response.data

            if (responseBuffer.size == 5) {
                val majorVersion = responseBuffer[3].toInt()
                val minorVersion = responseBuffer[4].toInt()
                Result.success(
                    VersionInfo(
                        isInstalled = true,
                        majorVersion = majorVersion,
                        minorVersion = minorVersion,
                        hotfixVersion = 0,
                        text = null
                    )
                )
            } else if (responseBuffer.size == 4) {
                val majorVersion = responseBuffer[2].toInt()
                val minorVersion = responseBuffer[3].toInt()
                Result.success(
                    VersionInfo(
                        isInstalled = true,
                        majorVersion = majorVersion,
                        minorVersion = minorVersion,
                        hotfixVersion = 0,
                        text = null
                    )
                )
            } else if (responseBuffer.size == 2) {
                val majorVersion = responseBuffer[0].toInt()
                val minorVersion = responseBuffer[1].toInt()

                Result.success(
                    VersionInfo(
                        isInstalled = true,
                        majorVersion = majorVersion,
                        minorVersion = minorVersion,
                        hotfixVersion = 0,
                        text = null
                    )
                )
            } else {
                Result.failure(SentrySDKError.CardOSVersionError)
            }
        } else if (response.statusWord == APDUResponseCode.INSTRUCTION_BYTE_NOT_SUPPORTED.value) {
            Result.failure(SentrySDKError.CardOSVersionError)
        } else {
            Result.failure(SentrySDKError.ApduCommandError(response.statusWord))
        }
    }

    /**
     * Retrieves the version of the Enrollment applet installed on the scanned card (only available on version 2.0 or greater).
     * - Note: If the Enrollment applet version on the card is earlier than 2.0, this returns -1 for all version values.
     *
     * @param tag: The `NFCISO7816` tag supplied by an NFC connection to which `APDU` commands are sent.
     * @return VersionInfo structure containing version information.
     * This method can throw the following exceptions:
     * `SentrySDKError.apduCommandError` that contains the status word returned by the last failed `APDU` command.

     */
    fun getEnrollmentAppletVersion(tag: Tag): Result<VersionInfo> {
        log("----- BiometricsAPI Get Enrollment Applet Version")
        log("     Selecting Enroll Applet")

        return try {
            val response = sendAndConfirm(
                apduCommand = APDUCommand.SELECT_ENROLL_APPLET.value,
                name = "Select Enroll Applet",
                tag = tag
            ).getOrElse { return Result.failure(it) }

            val responseBuffer = response.data

            if (responseBuffer.size < 16) {
                Result.failure(SentrySDKError.CardOSVersionError)
            } else {
                val string = responseBuffer.toString(Charsets.US_ASCII)
                val majorVersion = responseBuffer[13].toInt() - 0x30
                val minorVersion = responseBuffer[15].toInt() - 0x30
                Result.success(
                    VersionInfo(
                        isInstalled = true,
                        majorVersion = majorVersion,
                        minorVersion = minorVersion,
                        hotfixVersion = 0,
                        text = string
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

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
    fun getCVMAppletVersion(tag: Tag): Result<VersionInfo> {
        log("----- BiometricsAPI Get CVM Applet Version")

        val response = sendAndConfirm(
            apduCommand = APDUCommand.SELECT_CVM_APPLET.value,
            name = "Select CVM Applet",
            tag = tag
        ).getOrElse {
            return Result.failure(it)
        }

        val responseBuffer = response.data

        return if (responseBuffer.size > 11) {
            val string = responseBuffer
                .filter { it in 0x20..0x7E }
                .toByteArray()
                .toString(Charsets.US_ASCII)
            val majorVersion = responseBuffer[10].toInt() - 0x30
            val minorVersion = responseBuffer[12].toInt() - 0x30
            Result.success(
                VersionInfo(
                    isInstalled = true,
                    majorVersion = majorVersion,
                    minorVersion = minorVersion,
                    hotfixVersion = 0,
                    text = string
                )
            )
        } else {
            Result.failure(SentrySDKError.CvmAppletError(responseBuffer.size))
        }
    }


    /**
     * Scans the finger currently on the fingerprint sensor, indicating if the scanned fingerprint matches one recorded during enrollment.
     *
     * @param: tag Nfc tag
     *
     * @return fingerprint match
     *
     * This method can throw the following exceptions:
     * `SentrySDKError.apduCommandError` that contains the status word returned by the last failed `APDU` command.
     * `SentrySDKError.cvmAppletNotAvailable` if the CVM applet was unavailable for some reason.
     * `SentrySDKError.cvmAppletBlocked` if the CVM applet is in a blocked state and can no longer be used.
     * `SentrySDKError.cvmAppletError` if the CVM applet returned an unexpected error code.
     *
     */
    fun getFingerprintVerification(tag: Tag): Result<Boolean> {
        log("----- BiometricsAPI Get Fingerprint Verification")


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
                log("     Match")
                return Result.success(true)
            }

            if (returnData.data[4] == 0x5A.toByte()) {
                log("     No match found")
                return Result.success(false)
            }

            return Result.failure(SentrySDKError.CvmAppletError(returnData.data[4].toInt()))
        }

        return Result.failure(SentrySDKError.ApduCommandError(returnData.statusWord))
    }

    fun getCardOSVersion(tag: Tag): Result<VersionInfo> {
        log("----- BiometricsAPI Get Card OS Version")
        log("     Getting card OS version")

        val returnData = sendAndConfirm(
            apduCommand = APDUCommand.GET_OS_VERSION.value,
            name = "Get Card OS Version",
            tag = tag
        ).getOrThrow()

        log("     Processing response")
        val dataBuffer = returnData.data

        val cardVersionError = Result.failure<VersionInfo>(SentrySDKError.CardOSVersionError)

        if (dataBuffer.size < 8) {
            return cardVersionError
        }

        if (dataBuffer[0] != 0xFE.toByte()) {
            return cardVersionError
        }
        if (dataBuffer[1] < 0x40.toByte()) {
            return cardVersionError
        }
        if (dataBuffer[2] != 0x7f.toByte()) {
            return cardVersionError
        }
        if (dataBuffer[3] != 0x00.toByte()) {
            return cardVersionError
        }
        if (dataBuffer[4] < 0x40.toByte()) {
            return cardVersionError
        }
        if (dataBuffer[5] != 0x9f.toByte()) {
            return cardVersionError
        }
        if (dataBuffer[6] != 0x01.toByte()) {
            return cardVersionError
        }

        val n = dataBuffer[7]
        var p: Int = 8 + n

        if (dataBuffer[p] != 0x9F.toByte()) {
            return cardVersionError
        }
        p += 1
        if (dataBuffer[p] != 0x02.toByte()) {
            return cardVersionError
        }
        p += 1
        if (dataBuffer[p].toInt() != 5) {
            return cardVersionError
        }
        p += 1

        val major = dataBuffer[p] - 0x30
        p += 2
        val minor = dataBuffer[p] - 0x30
        p += 2
        val hotfix = dataBuffer[p] - 0x30

        return Result.success(
            VersionInfo(
                isInstalled = true,
                majorVersion = major,
                minorVersion = minor,
                hotfixVersion = hotfix,
                text = null
            )
        )
    }

    private fun log(text: String) {
        if (isDebugOutputVerbose) {
            println(text)
        }
    }

}

private fun Tag.transceive(bytes: ByteArray): Result<ByteArray> = try {
    Result.success(IsoDep.get(this).transceive(bytes))
} catch (e: TagLostException) {
    Result.failure(e)
}
