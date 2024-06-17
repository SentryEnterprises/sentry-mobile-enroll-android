package com.secure.jnet.wallet.data.crypto.models

import java.math.BigInteger

sealed class RawTransactionDTO

data class BitcoinRawTransactionDTO(
    val amount: Long,
    val fee: Long,
    val addressTo: String,

    // For signing
    val inputsToSign: String,
    val inputsCount: Int,
    val outputsToSign: String,
    val outputsCount: Int,
) : RawTransactionDTO()

data class EthereumRawTransactionDTO(
    val amount: BigInteger,
    val fee: Long,
    val addressTo: String,

    // For signing
    val chainId: ByteArray,
    val nonce: ByteArray,
    val maxPriorityFeePerGas: ByteArray,
    val maxFeePerGas: ByteArray,
    val gasLimit: ByteArray,
    val amountEncoded: ByteArray,
    val addressToEncoded: ByteArray,
) : RawTransactionDTO() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EthereumRawTransactionDTO

        if (fee != other.fee) return false
        if (amount != other.amount) return false
        if (addressTo != other.addressTo) return false
        if (!chainId.contentEquals(other.chainId)) return false
        if (!nonce.contentEquals(other.nonce)) return false
        if (!maxPriorityFeePerGas.contentEquals(other.maxPriorityFeePerGas)) return false
        if (!maxFeePerGas.contentEquals(other.maxFeePerGas)) return false
        if (!gasLimit.contentEquals(other.gasLimit)) return false
        if (!amountEncoded.contentEquals(other.amountEncoded)) return false
        return addressToEncoded.contentEquals(other.addressToEncoded)
    }

    override fun hashCode(): Int {
        var result = fee.hashCode()
        result = 31 * result + amount.hashCode()
        result = 31 * result + addressTo.hashCode()
        result = 31 * result + chainId.contentHashCode()
        result = 31 * result + nonce.contentHashCode()
        result = 31 * result + maxPriorityFeePerGas.contentHashCode()
        result = 31 * result + maxFeePerGas.contentHashCode()
        result = 31 * result + gasLimit.contentHashCode()
        result = 31 * result + amountEncoded.contentHashCode()
        result = 31 * result + addressToEncoded.contentHashCode()
        return result
    }
}