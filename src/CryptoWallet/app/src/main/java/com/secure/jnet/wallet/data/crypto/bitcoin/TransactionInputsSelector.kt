//package com.secure.jnet.wallet.data.crypto.bitcoin
//
//import com.secure.jnet.wallet.domain.models.remote.UtxoEntity
//import javax.inject.Inject
//
//class TransactionInputsSelector @Inject constructor(
//    private val transactionSizeCalculator: TransactionSizeCalculator
//) {
//
//    fun getInputs(
//        utxo: List<UtxoEntity>,
//        outputCount: Int,
//        amount: Long,
//        feeRate: Long,
//    ): List<UtxoEntity> {
//        val selectedInputs = mutableListOf<UtxoEntity>()
//
//        var totalValue = 0L
//        var sentValue = 0L
//        var fee: Long
//
//        // Sort utxo by amount
//        val sortedUtxo = utxo.sortedByDescending { it.amount }
//
//        for (unspentOutput in sortedUtxo) {
//            selectedInputs.add(unspentOutput)
//            totalValue += unspentOutput.amount
//
//            fee = transactionSizeCalculator.calculateFee(
//                selectedInputs.size, outputCount
//            ) * feeRate
//
//            sentValue = amount + fee
//
//            // totalValue is enough
//            if (sentValue <= totalValue) {
//                break
//            }
//        }
//
//        // Insufficient utxo
//        if (totalValue < sentValue) {
//            return emptyList()
//        }
//
//        return selectedInputs
//    }
//}