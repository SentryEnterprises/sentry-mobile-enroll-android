package com.secure.jnet.wallet.domain.models.remote

data class UtxoEntity(
    val address: String,
    val amount: Long,
    val index: Int,
    val script: String,
    val scriptType: String,
    val transactionId: String,
)