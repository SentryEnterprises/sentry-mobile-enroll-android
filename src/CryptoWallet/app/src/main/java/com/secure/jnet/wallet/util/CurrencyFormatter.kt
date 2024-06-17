//package com.secure.jnet.wallet.util
//
//import com.secure.jnet.wallet.domain.models.enums.CryptoCurrency
//import java.math.BigInteger
//import java.text.NumberFormat
//import java.util.Locale
//
//fun BigInteger.formatToDollar(withSign: Boolean = true): String {
//    val cents = this.toDouble() / 100
//    return if (withSign) {
//        val format = NumberFormat.getCurrencyInstance(Locale("en", "US"))
//        format.format(cents)
//    } else {
//        val format = NumberFormat.getNumberInstance()
//        format.format(cents)
//    }
//}
//
//fun BigInteger.formatToToken(cryptoCurrency: CryptoCurrency, withTicker: Boolean = true): String {
//    val tokenAmount = AmountConverter.bigIntegerTokenAmountToStringTokenAmount(
//        cryptoCurrency,
//        this
//    )
//
//    return if (withTicker) {
//        "$tokenAmount ${cryptoCurrency.ticker.uppercase()}"
//    } else {
//        tokenAmount
//    }
//}