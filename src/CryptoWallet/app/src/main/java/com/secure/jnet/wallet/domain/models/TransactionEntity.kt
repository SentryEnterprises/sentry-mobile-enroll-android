package com.secure.jnet.wallet.domain.models

import com.secure.jnet.wallet.domain.models.enums.CryptoCurrency
import java.math.BigInteger

data class TransactionEntity(
    val cryptoCurrency: CryptoCurrency,
    val amountToken: BigInteger,
    val hash: String,
    val timestamp: Long,
    val status: TransactionStatus,
    val type: TransactionType,
)

enum class TransactionType {
    Incoming,
    Outgoing,
}

enum class TransactionStatus {
    Completed,
    Pending,
    Failed
}