//package com.secure.jnet.wallet.presentation.models
//
//import com.secure.jnet.wallet.domain.models.enums.CryptoCurrency
//
//sealed class TokenItem {
//
//    data class TokenData(
//        val cryptoCurrency: CryptoCurrency,
//        val balance: Balance,
//    ) : TokenItem()
//
//    data object TokenFooter : TokenItem()
//
//}