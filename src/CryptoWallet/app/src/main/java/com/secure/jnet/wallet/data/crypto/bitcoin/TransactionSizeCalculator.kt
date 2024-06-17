//package com.secure.jnet.wallet.data.crypto.bitcoin
//
//import javax.inject.Inject
//
//class TransactionSizeCalculator @Inject constructor() {
//
//    /**
//     * Calculates fee for Single-Sig SegWit P2WPKH transaction
//     */
//    fun calculateFee(inputsCount: Int, outputsCount: Int): Int {
//        return INPUT_WEIGHT * inputsCount +
//                OUTPUT_WEIGHT * outputsCount +
//                TRANSACTION_OVERHEAD_WEIGHT
//    }
//
//    companion object {
//        private const val TRANSACTION_OVERHEAD_WEIGHT = 10
//        private const val INPUT_WEIGHT = 69
//        private const val OUTPUT_WEIGHT = 32
//    }
//}