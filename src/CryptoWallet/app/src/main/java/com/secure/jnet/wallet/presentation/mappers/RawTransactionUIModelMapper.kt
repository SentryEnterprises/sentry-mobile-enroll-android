//package com.secure.jnet.wallet.presentation.mappers
//
//import com.secure.jnet.wallet.data.crypto.models.BitcoinRawTransactionDTO
//import com.secure.jnet.wallet.data.crypto.models.EthereumRawTransactionDTO
//import com.secure.jnet.wallet.data.crypto.models.RawTransactionDTO
//import com.secure.jnet.wallet.domain.models.enums.CryptoCurrency
//import com.secure.jnet.wallet.presentation.models.Transaction
//import com.secure.jnet.wallet.util.AmountConverter
//import com.secure.jnet.wallet.util.formatToDollar
//import com.secure.jnet.wallet.util.formatToToken
//import java.math.BigInteger
//import javax.inject.Inject
//
//class RawTransactionUIModelMapper @Inject constructor() {
//
//    fun mapToUIModel(
//        entityModel: RawTransactionDTO,
//        cryptoCurrency: CryptoCurrency,
//        tokenPrice: BigInteger
//    ): Transaction {
//        return when (cryptoCurrency) {
//            CryptoCurrency.Bitcoin -> mapBitcoinTransaction(
//                entityModel as BitcoinRawTransactionDTO, cryptoCurrency, tokenPrice
//            )
//
//            CryptoCurrency.Ethereum -> mapEthereumTransaction(
//                entityModel as EthereumRawTransactionDTO, cryptoCurrency, tokenPrice
//            )
//        }
//    }
//
//    private fun mapBitcoinTransaction(
//        entityModel: BitcoinRawTransactionDTO,
//        cryptoCurrency: CryptoCurrency,
//        tokenPrice: BigInteger
//    ): Transaction {
//        val amountToken = BigInteger(entityModel.amount.toString()).formatToToken(cryptoCurrency)
//
//        val amountFiatBigInteger = AmountConverter
//            .bigIntegerTokenAmountToBigIntegerFiatAmount(
//                cryptoCurrency,
//                BigInteger(entityModel.amount.toString()),
//                tokenPrice
//            )
//        val amountFiat = amountFiatBigInteger.formatToDollar()
//
//        val feeToken = BigInteger(entityModel.fee.toString()).formatToToken(cryptoCurrency)
//
//        val feeFiatBigDecimal = AmountConverter
//            .bigIntegerTokenAmountToBigIntegerFiatAmount(
//                cryptoCurrency,
//                BigInteger(entityModel.fee.toString()),
//                tokenPrice
//            )
//
//        val feeFiat = feeFiatBigDecimal.formatToDollar()
//
//        val totalToken = BigInteger((entityModel.fee + entityModel.amount).toString())
//            .formatToToken(cryptoCurrency)
//
//        val totalFiat = (amountFiatBigInteger + feeFiatBigDecimal).formatToDollar()
//
//        return Transaction(
//            addressTo = entityModel.addressTo,
//            amountToken = amountToken,
//            amountFiat = amountFiat,
//            feeToken = feeToken,
//            feeFiat = feeFiat,
//            totalAmountToken = totalToken,
//            totalAmountFiat = totalFiat,
//        )
//    }
//
//    private fun mapEthereumTransaction(
//        entityModel: EthereumRawTransactionDTO,
//        cryptoCurrency: CryptoCurrency,
//        tokenPrice: BigInteger
//    ): Transaction {
//        val amountToken = entityModel.amount.formatToToken(cryptoCurrency)
//
//        val amountFiatBigInteger = AmountConverter
//            .bigIntegerTokenAmountToBigIntegerFiatAmount(
//                cryptoCurrency,
//                BigInteger(entityModel.amount.toString()),
//                tokenPrice
//            )
//        val amountFiat = amountFiatBigInteger.formatToDollar()
//
//        val feeToken = BigInteger(entityModel.fee.toString()).formatToToken(cryptoCurrency)
//
//        val feeFiatBigDecimal = AmountConverter
//            .bigIntegerTokenAmountToBigIntegerFiatAmount(
//                cryptoCurrency,
//                BigInteger(entityModel.fee.toString()),
//                tokenPrice
//            )
//
//        val feeFiat = feeFiatBigDecimal.formatToDollar()
//
//        val totalToken = BigInteger((entityModel.fee + entityModel.amount.toLong()).toString())
//            .formatToToken(cryptoCurrency)
//
//        val totalFiat = (amountFiatBigInteger + feeFiatBigDecimal).formatToDollar()
//
//        return Transaction(
//            addressTo = entityModel.addressTo,
//            amountToken = amountToken,
//            amountFiat = amountFiat,
//            feeToken = feeToken,
//            feeFiat = feeFiat,
//            totalAmountToken = totalToken,
//            totalAmountFiat = totalFiat,
//        )
//    }
//}