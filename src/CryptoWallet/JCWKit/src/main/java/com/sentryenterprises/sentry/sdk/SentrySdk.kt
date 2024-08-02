package com.sentryenterprises.sentry.sdk

import com.sentryenterprises.sentry.sdk.biometrics.BiometricsApi

/**
Entry point for the `SentrySDK` functionality. Provides methods exposing all available functionality.

This class controls and manages an `NFCReaderSession` to communicate with an `NFCISO7816Tag` via `APDU` commands.

The bioverify.cap, com.idex.enroll.cap, and com.jnet.CDCVM.cap applets must be installed on the SentryCard for full access to all functionality of this SDK.
 */
class SentrySdk(
    private val enrollCode: ByteArray,
    private val verboseDebugOutput: Boolean = true,
    private val useSecureCommunication: Boolean = true,

) {
    private val biometricsAPI: BiometricsApi = BiometricsApi(verboseDebugOutput, useSecureChannel = useSecureCommunication)

    private var session: NFCReaderSession?
    private var connectedTag: NFCISO7816Tag?
    private var callback: ((Result<NFCISO7816Tag, Error>) -> Void)?

}