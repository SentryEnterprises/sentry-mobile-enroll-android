package com.secure.jnet.wallet.domain.models.remote

data class NetworkEntity(
    val id: String,
    val type: String,
    val balanceType: String,
    val isPriorityFeeUsed: Boolean,
    val name: String,
    val symbol: String
)