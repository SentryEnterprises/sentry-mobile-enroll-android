//package com.secure.jnet.wallet.data.crypto.models
//
//import com.secure.jnet.wallet.util.ext.toByteArray
//import com.secure.jnet.wallet.util.ext.toBytes
//import com.secure.jnet.wallet.util.ext.toHexString
//
//data class RawTransactionOutputDTO(
//    val amount: Long,
//    val script: String,
//) {
//
//    // <Value:8> <ScriptLen:1> <OutputScript>
//    fun build(): String {
//        if (amount == 0L) {
//            throw IllegalStateException("Output amount is ZERO")
//        }
//
//        if (script.isEmpty()) {
//            throw IllegalStateException("Empty output script")
//        }
//
//        return StringBuilder().apply {
//            append(amount.toBytes().toHexString()) // <Value:8>
//            append(byteArrayOf(script.toByteArray().size.toByte()).toHexString()) // <ScriptLen:1>
//            append(script) // <OutputScript>
//        }.toString().uppercase()
//    }
//}