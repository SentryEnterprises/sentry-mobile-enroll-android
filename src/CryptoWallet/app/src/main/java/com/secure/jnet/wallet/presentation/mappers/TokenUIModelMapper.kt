//package com.secure.jnet.wallet.presentation.mappers
//
//import com.secure.jnet.wallet.domain.models.TokenEntity
//import com.secure.jnet.wallet.presentation.models.Balance
//import com.secure.jnet.wallet.presentation.models.TokenItem
//import com.secure.jnet.wallet.util.formatToDollar
//import com.secure.jnet.wallet.util.formatToToken
//import javax.inject.Inject
//
//class TokenUIModelMapper @Inject constructor() : UIModelMapper<TokenItem, TokenEntity> {
//
//    override fun mapToUIModel(entityModel: TokenEntity): TokenItem {
//        return TokenItem.TokenData(
//            cryptoCurrency = entityModel.cryptoCurrency,
//            balance = Balance(
//                cryptoCurrency = entityModel.cryptoCurrency,
//                amountToken = entityModel.amountToken.formatToToken(
//                    cryptoCurrency = entityModel.cryptoCurrency,
//                    withTicker = false
//                ),
//                amountFiat = entityModel.amountFiat.formatToDollar(),
//            ),
//        )
//    }
//}