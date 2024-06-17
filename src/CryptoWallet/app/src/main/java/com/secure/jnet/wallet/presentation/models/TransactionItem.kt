//package com.secure.jnet.wallet.presentation.models
//
//import com.secure.jnet.wallet.domain.models.TransactionStatus
//import com.secure.jnet.wallet.domain.models.TransactionType
//import com.secure.jnet.wallet.domain.models.enums.CryptoCurrency
//
//sealed class TransactionItem {
//
//    data class HeaderItem(
//        val timestamp: Long,
//    ) : TransactionItem()
//
//    data class Data(
//        val cryptoCurrency: CryptoCurrency,
//        val amountToken: String,
//        val hash: String,
//        val timestamp: Long,
//        val status: TransactionStatus,
//        val type: TransactionType,
//    ) : TransactionItem()
//
//}