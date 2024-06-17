package com.secure.jnet.wallet.data.mappers.remote

import com.secure.jnet.wallet.data.mappers.DataModelMapper
import com.secure.jnet.wallet.data.models.remote.TransactionDTO
import com.secure.jnet.wallet.domain.models.TransactionEntity
import com.secure.jnet.wallet.domain.models.TransactionStatus
import com.secure.jnet.wallet.domain.models.TransactionType
import com.secure.jnet.wallet.domain.models.enums.parseCryptoCurrency
import java.math.BigInteger
import javax.inject.Inject

class TransactionDataModelMapper @Inject constructor() :
    DataModelMapper<List<TransactionEntity>, TransactionDTO> {

    override fun mapToEntity(dataModel: TransactionDTO): List<TransactionEntity> {
        val transactionEntity = mutableListOf<TransactionEntity>()

        dataModel.data.forEach {
            it.attributes.simplifiedTransactions.forEach { tx ->
                val transactionStatus = when (tx.status.lowercase()) {
                    "completed" -> TransactionStatus.Completed
                    "pending" -> TransactionStatus.Pending
                    "failed" -> TransactionStatus.Failed
                    else -> {
                        throw IllegalArgumentException("No such '${tx.status}' transaction status")
                    }
                }

                val transactionType = when (tx.type.lowercase()) {
                    "incoming" -> TransactionType.Incoming
                    "outgoing" -> TransactionType.Outgoing
                    else -> {
                        throw IllegalArgumentException("No such '${tx.type}' transaction type")
                    }
                }

                transactionEntity.add(
                    TransactionEntity(
                        cryptoCurrency = tx.denomination.parseCryptoCurrency(),
                        amountToken = BigInteger(tx.amount),
                        timestamp = tx.date,
                        hash = tx.id,
                        status = transactionStatus,
                        type = transactionType
                    )
                )
            }
        }

        return transactionEntity
    }
}