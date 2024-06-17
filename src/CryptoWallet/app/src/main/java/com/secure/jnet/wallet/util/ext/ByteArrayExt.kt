//package com.secure.jnet.wallet.util.ext
//
//import com.secure.jnet.wallet.util.ByteUtility
//import java.math.BigInteger
//import java.nio.ByteBuffer
//import java.nio.ByteOrder
//
//fun ByteArray.toHexString(): String {
//    return this.joinToString(separator = "") {
//        it.toInt().and(0xff).toString(16).padStart(2, '0')
//    }
//}
//
////fun ByteArray.toReversedHex(): String {
////    return reversedArray().toHexString()
////}
////
////fun String.toByteArray(): ByteArray {
////    val preparedStr = this.replace(" ", "", true)
////    return ByteUtility.hexStringToByteArray(preparedStr)
////}
//
////fun String.hexToByteArray(): ByteArray {
////    return ByteArray(this.length / 2) {
////        this.substring(it * 2, it * 2 + 2).toInt(16).toByte()
////    }
////}
////
////fun String.toReversedByteArray(): ByteArray {
////    return hexToByteArray().reversedArray()
////}
////
////fun Long.toBytes(order: ByteOrder = ByteOrder.LITTLE_ENDIAN): ByteArray {
////    return ByteBuffer.allocate(Long.SIZE_BYTES).order(order).putLong(this).array()
////}
////
////fun Int.toBytes(order: ByteOrder = ByteOrder.LITTLE_ENDIAN): ByteArray {
////    return ByteBuffer.allocate(Int.SIZE_BYTES).order(order).putInt(this).array()
////}
////
////fun ByteArray.toLong(order: ByteOrder = ByteOrder.LITTLE_ENDIAN): Long {
////    return ByteBuffer.wrap(this).order(order).long
////}
////
////fun ByteArray.toInt(order: ByteOrder = ByteOrder.LITTLE_ENDIAN): Int {
////    return ByteBuffer.wrap(this).order(order).int
////}
//
//// Converts positive long values to a byte array without leading zero byte (for sign bit)
//fun Long.toByteArray(): ByteArray {
//    var array = this.toBigInteger().toByteArray()
//    if (array[0].toInt() == 0) {
//        val tmp = ByteArray(array.size - 1)
//        System.arraycopy(array, 1, tmp, 0, tmp.size)
//        array = tmp
//    }
//    return array
//}
//
//// Converts positive int values to a byte array without leading zero byte (for sign bit)
//fun Int.toByteArray(): ByteArray {
//    var array = this.toBigInteger().toByteArray()
//    if (array[0].toInt() == 0) {
//        val tmp = ByteArray(array.size - 1)
//        System.arraycopy(array, 1, tmp, 0, tmp.size)
//        array = tmp
//    }
//    return array
//}
//
//// Converts positive int values to a byte array without leading zero byte (for sign bit)
//fun BigInteger.toByteArrayWithoutLeadingZero(): ByteArray {
//    var array = this.toByteArray()
//    if (array[0].toInt() == 0) {
//        val tmp = ByteArray(array.size - 1)
//        System.arraycopy(array, 1, tmp, 0, tmp.size)
//        array = tmp
//    }
//    return array
//}
//
//fun String.stripHexPrefix(): String {
//    return if (this.startsWith("0x", true)) {
//        this.substring(2)
//    } else {
//        this
//    }
//}