package com.secure.jnet.wallet.domain.models.remote

sealed class FeeEntity

data class BitcoinFeeEntity(
    val feeRate: Long
) : FeeEntity()

data class EthereumFeeEntity(
    val maxFeePerGas: Long,
    val maxPriorityFeePerGas: Long,
) : FeeEntity()