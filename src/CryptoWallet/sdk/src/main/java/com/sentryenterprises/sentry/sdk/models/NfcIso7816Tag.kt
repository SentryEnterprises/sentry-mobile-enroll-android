package com.sentryenterprises.sentry.sdk.models

fun interface NfcIso7816Tag {
    fun transceive(dataIn: ByteArray): Result<ByteArray>
}