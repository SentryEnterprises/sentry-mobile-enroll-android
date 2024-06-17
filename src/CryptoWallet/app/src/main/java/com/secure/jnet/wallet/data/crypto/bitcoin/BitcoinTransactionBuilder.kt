//package com.secure.jnet.wallet.data.crypto.bitcoin
//
//import com.secure.jnet.jcwkit.JCWKitUtils
//import com.secure.jnet.wallet.data.crypto.models.BitcoinRawTransactionDTO
//import com.secure.jnet.wallet.data.crypto.models.RawTransactionInputDTO
//import com.secure.jnet.wallet.data.crypto.models.RawTransactionOutputDTO
//import com.secure.jnet.wallet.domain.models.remote.BitcoinFeeEntity
//import com.secure.jnet.wallet.domain.models.remote.UtxoEntity
//import com.secure.jnet.wallet.util.ext.toHexString
//import timber.log.Timber
//import javax.inject.Inject
//
//class BitcoinTransactionBuilder @Inject constructor(
//    private val jcwKitUtils: JCWKitUtils,
//    private val transactionSizeCalculator: TransactionSizeCalculator,
//    private val transactionInputsSelector: TransactionInputsSelector,
//) {
//
//    fun buildTransaction(
//        amount: Long,
//        addressTo: String,
//        addressFrom: String,
//        utxo: List<UtxoEntity>,
//        feeRate: BitcoinFeeEntity,
//        maxAmount: Boolean,
//    ): BitcoinRawTransactionDTO {
//        if (amount == 0L) {
//            throw IllegalStateException("Amount is ZERO")
//        }
//
//        if (utxo.isEmpty()) {
//            throw IllegalStateException("Empty UTXO")
//        }
//
//        if (feeRate.feeRate == 0L) {
//            throw IllegalStateException("Fee rate is ZERO")
//        }
//
//        val outputCount = if (maxAmount) 1 else DEFAULT_OUTPUTS_COUNT
//
//        var inputAmountTotal = 0L
//
//        val inputs = if (maxAmount) {
//            utxo.map {
//                inputAmountTotal += it.amount
//                RawTransactionInputDTO(it.amount, it.script, it.index, it.transactionId)
//            }
//        } else {
//            transactionInputsSelector.getInputs(
//                utxo,
//                outputCount,
//                amount,
//                feeRate.feeRate,
//            ).map {
//                inputAmountTotal += it.amount
//                RawTransactionInputDTO(it.amount, it.script, it.index, it.transactionId)
//            }
//        }
//
//        Timber.d("-----> buildTransaction() input: = ${inputs[0]}]")
//
//        val fee = transactionSizeCalculator.calculateFee(inputs.size, outputCount) * feeRate.feeRate
//
//        val inputsToSign = StringBuilder().apply {
//            append(byteArrayOf(inputs.size.toByte()).toHexString())
//
//            inputs.forEach {
//                append(it.build())
//            }
//        }.toString()
//
//        val outputTo = RawTransactionOutputDTO(
//            amount,
//            jcwKitUtils.addressToScript(addressTo)
//        )
//
//        Timber.d("-----> buildTransaction() outputTo: = $outputTo")
//
//        val outputChange = RawTransactionOutputDTO(
//            inputAmountTotal - amount - fee,
//            jcwKitUtils.addressToScript(addressFrom)
//        )
//
//        Timber.d("-----> buildTransaction() outputChange: = $outputChange")
//
//        val outputs = mutableListOf<RawTransactionOutputDTO>().apply {
//            add(outputTo)
//            add(outputChange)
//        }
//
//        val outputsToSign = StringBuilder().apply {
//            append(byteArrayOf(outputs.size.toByte()).toHexString())
//
//            outputs.forEach {
//                append(it.build())
//            }
//        }.toString()
//
//        return BitcoinRawTransactionDTO(
//            amount = amount,
//            fee = fee,
//            addressTo = addressTo,
//            inputsToSign = inputsToSign,
//            inputsCount = inputs.size,
//            outputsToSign = outputsToSign,
//            outputsCount = outputs.size
//        )
//    }
//
//    fun calculateFee(
//        amount: Long,
//        utxo: List<UtxoEntity>,
//        feeRate: BitcoinFeeEntity,
//        maxAmount: Boolean,
//    ): Long {
//        if (amount == 0L) {
//            return 0
//        }
//
//        if (utxo.isEmpty()) {
//            Timber.d("UTXO is empty")
//            return 0
//        }
//
//        if (feeRate.feeRate == 0L) {
//            Timber.d("Fee rate is ZERO")
//            return 0
//        }
//
//        val outputCount = if (maxAmount) 1 else DEFAULT_OUTPUTS_COUNT
//
//        val inputs = if (maxAmount) {
//            utxo.map {
//                RawTransactionInputDTO(it.amount, it.script, it.index, it.transactionId)
//            }
//        } else {
//            transactionInputsSelector.getInputs(
//                utxo,
//                outputCount,
//                amount,
//                feeRate.feeRate,
//            ).map {
//                RawTransactionInputDTO(it.amount, it.script, it.index, it.transactionId)
//            }
//        }
//
//        val fee = transactionSizeCalculator.calculateFee(inputs.size, outputCount) * feeRate.feeRate
//
//        Timber.d("-----> calculateFee() = $fee")
//
//        return  fee
//    }
//
//    companion object {
//        private const val DEFAULT_OUTPUTS_COUNT = 2
//    }
//}
//
