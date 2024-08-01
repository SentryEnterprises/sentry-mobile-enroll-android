package com.secure.jnet.wallet.util;

@OptIn(ExperimentalStdlibApi::class)
fun byteArrayToHexString(data: ByteArray?): String =
    data
        ?.map {
            it.toHexString()
        }?.joinToString("")
        ?.toUpperCase()
        ?: ""

fun intToByteArray(vararg elements: Int): ByteArray =
    elements
        .map { it.toByte() }
        .toByteArray()