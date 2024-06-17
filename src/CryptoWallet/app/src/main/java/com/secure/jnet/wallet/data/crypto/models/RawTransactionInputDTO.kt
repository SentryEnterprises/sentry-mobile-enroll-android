//package com.secure.jnet.wallet.data.crypto.models
//
//import com.secure.jnet.wallet.util.ext.toByteArray
//import com.secure.jnet.wallet.util.ext.toBytes
//import com.secure.jnet.wallet.util.ext.toHexString
//
//data class RawTransactionInputDTO(
//    val amount: Long,
//    val script: String,
//    val index: Int,
//    val transactionHash: String,
//) {
//
//    // <Transaction_ID:16> <Index:4> <ScriptLen:1> <InputScript> <InputSequence:4> <Amount:8>
//    fun build(): String {
//        if (amount == 0L) {
//            throw IllegalStateException("Input amount is ZERO")
//        }
//
//        if (script.isEmpty()) {
//            throw IllegalStateException("Empty input script")
//        }
//
//        if (transactionHash.isEmpty()) {
//            throw IllegalStateException("Empty input tx hash")
//        }
//
//        return StringBuilder().apply {
//            append(transactionHash) // <Transaction_ID:16>
//            append(index.toBytes().toHexString()) // <Index:4>
//            append(byteArrayOf(script.toByteArray().size.toByte()).toHexString()) // <ScriptLen:1>
//            append(script) // <InputScript>
//            append(INPUT_SEQUENCE) // <InputSequence:4>
//            append(amount.toBytes().toHexString()) // <Amount:8>
//        }.toString().uppercase()
//    }
//
//    companion object {
//        private const val INPUT_SEQUENCE = "FFFFFFFF"
//    }
//}