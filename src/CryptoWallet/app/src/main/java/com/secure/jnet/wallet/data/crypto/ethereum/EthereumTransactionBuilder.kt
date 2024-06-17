//package com.secure.jnet.wallet.data.crypto.ethereum
//
//import com.secure.jnet.wallet.data.crypto.models.EthereumRawTransactionDTO
//import com.secure.jnet.wallet.util.TESTNET
//import java.math.BigInteger
//import javax.inject.Inject
//
//class EthereumTransactionBuilder @Inject constructor(){
//
//    fun buildTransaction(
//        nonce: Long,
//        maxPriorityFeePerGas: Long,
//        maxFeePerGas: Long,
//        amount: BigInteger,
//        addressTo: String,
//    ): EthereumRawTransactionDTO {
//
//        if (amount == BigInteger.ZERO) {
//            throw IllegalStateException("Amount is ZERO")
//        }
//
//        if (maxPriorityFeePerGas == 0L || maxFeePerGas == 0L) {
//            throw IllegalStateException("Fee rate is ZERO")
//        }
//
//        val chainId = if (TESTNET) TESTNET_CHAIN_ID else MAINNET_CHAIN_ID
//
//        return EthereumRawTransactionDTO(
//            amount = amount,
//            fee = maxFeePerGas * GAS_LIMIT,
//            addressTo = addressTo,
//
//            chainId = RlpEncoder.encode(chainId),
//            nonce = RlpEncoder.encode(nonce),
//            maxPriorityFeePerGas = RlpEncoder.encode(maxPriorityFeePerGas),
//            maxFeePerGas = RlpEncoder.encode(maxFeePerGas),
//            gasLimit = RlpEncoder.encode(GAS_LIMIT),
//            amountEncoded = RlpEncoder.encode(amount),
//            addressToEncoded = RlpEncoder.encodeAddress(addressTo),
//        )
//    }
//
//    fun calculateFee(
//        maxFeePerGas: Long,
//    ): Long {
//        return maxFeePerGas * GAS_LIMIT
//    }
//
//    private companion object {
//        private const val TESTNET_CHAIN_ID = 17000
//        private const val MAINNET_CHAIN_ID = 1
//        private const val GAS_LIMIT = 21000L
//    }
//}