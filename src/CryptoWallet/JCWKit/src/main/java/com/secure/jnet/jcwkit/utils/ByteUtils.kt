package com.secure.jnet.jcwkit.utils

import java.math.BigInteger
import java.util.Locale

fun ByteArray.toHexString(): String {
    return byteArrayToHexString(this)
}

fun ByteArray.byteArrayToHexString(data: ByteArray?): String {
    return if (data == null) {
        ""
    } else {
        val hexString = StringBuffer(data.size * 2)
        for (i in data.indices) {
            val currentByte = data[i].toInt() and 255
            if (currentByte < 16) {
                hexString.append('0')
            }
            hexString.append(Integer.toHexString(currentByte))
        }
        hexString.toString().uppercase(Locale.getDefault())
    }
}

fun ByteArray.toHexString2(): String {
    return this.joinToString(separator = "") {
        it.toInt().and(0xff).toString(16).padStart(2, '0')
    }
}

fun ByteArray.toHexString3() = joinToString("") { "%02x".format(it) }

fun ByteArray.toReversedHex(): String {
    return reversedArray().toHexString()
}

fun String.hexToByteArray(): ByteArray {
    return ByteArray(this.length / 2) {
        this.substring(it * 2, it * 2 + 2).toInt(16).toByte()
    }
}

fun String.toReversedByteArray(): ByteArray {
    return hexToByteArray().reversedArray()
}

fun hexStringToByteArray(hexString: String): ByteArray {
    val byteArray = ByteArray(hexString.length / 2)
    for (i in 0 until hexString.length step 2) {
        byteArray[i / 2] = hexString.substring(i, i + 2).toInt(16).toByte()
    }
    return byteArray
}

fun BigInteger.bigIntegerToByteArray(bigInteger: BigInteger): ByteArray {
    val bitLength = bigInteger.bitLength()
    val byteLength = (bitLength + 7) / 8 // Number of bytes required

    val byteArray = ByteArray(byteLength)
    val bigIntegerBytes = bigInteger.toByteArray()

    // Copy bytes from bigIntegerBytes to byteArray, excluding the leading zero byte if present
    val srcPos = if (bigIntegerBytes.size > 1 && bigIntegerBytes[0] == 0.toByte()) 1 else 0
    val length = minOf(byteArray.size, bigIntegerBytes.size - srcPos)
    System.arraycopy(bigIntegerBytes, srcPos, byteArray, byteArray.size - length, length)

    return byteArray
}