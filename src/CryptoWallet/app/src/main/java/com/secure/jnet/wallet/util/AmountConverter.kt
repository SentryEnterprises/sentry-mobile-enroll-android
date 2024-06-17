//package com.secure.jnet.wallet.util
//
//import com.secure.jnet.wallet.domain.models.enums.CryptoCurrency
//import java.math.BigDecimal
//import java.math.BigInteger
//import java.math.MathContext
//import java.math.RoundingMode
//
//object AmountConverter {
//
//    /*
//        String token amount to BigInteger token amount
//        String fiat amount to BigInteger token amount
//
//        BigInteger token amount to String fiat amount
//        BigInteger token amount to String token amount
//
//        BigInteger token amount to Long token amount
//     */
//
//    /**
//     * Convert token amount in String to BigInteger
//     * "102.45" to 10245000000
//     * "102.8" to 10280000000
//     * "45" to 4500000000
//     * "0.23" to 23000000
//     */
//    fun stringTokenAmountToBigIntegerTokenAmount(cryptoCurrency: CryptoCurrency, amount: String): BigInteger {
//        val scale = when (cryptoCurrency) {
//            CryptoCurrency.Bitcoin -> BTC_SCALE
//            CryptoCurrency.Ethereum -> ETH_SCALE
//        }
//
//        val clearedString = amount.replace(Regex("[^\\d.]"), "")
//
//        if (clearedString.isBlank() || clearedString == ".")
//            return BigInteger.ZERO
//
//        return BigDecimal(clearedString).setScale(scale, ROUNDING_MODE)
//            .movePointRight(scale).toBigInteger()
//    }
//
//    /**
//     * Convert fiat amount in String to BigInteger token
//     * "102.45" to 10245
//     * "102.8" to 10280
//     * "45" to 4500
//     * "0.23" to 23
//     */
//    fun stringFiatAmountToTokenBigIntegerAmount(
//        cryptoCurrency: CryptoCurrency,
//        fiatAmount: String,
//        tokenPrice: BigInteger,
//    ): BigInteger {
//        if (tokenPrice <= BigInteger.ZERO) return BigInteger.ZERO
//
//        val scale = when (cryptoCurrency) {
//            CryptoCurrency.Bitcoin -> BTC_SCALE
//            CryptoCurrency.Ethereum -> ETH_SCALE
//        }
//
//        val fiatAmountBigInteger = getFiatAmountFromString(fiatAmount)
//            .movePointRight(FIAT_SCALE)
//
//        return if (fiatAmountBigInteger > BigDecimal.ZERO) {
//            val calculated = fiatAmountBigInteger
//                .divide(BigDecimal(tokenPrice), scale, RoundingMode.HALF_UP)
//                .stripTrailingZeros()
//
//            calculated.movePointRight(scale).toBigInteger()
//        } else {
//            BigInteger("0")
//        }
//    }
//
//    /**
//     * Convert token amount in BigInteger to String token amount
//     * 3333 to 0.00003333
//     */
//    fun bigIntegerTokenAmountToStringTokenAmount(
//        cryptoCurrency: CryptoCurrency,
//        tokenAmount: BigInteger,
//    ): String {
//        if (tokenAmount <= BigInteger.ZERO) return "0"
//
//        val scale = when (cryptoCurrency) {
//            CryptoCurrency.Bitcoin -> BTC_SCALE
//            CryptoCurrency.Ethereum -> ETH_SCALE
//        }
//
//        return BigDecimal(tokenAmount).movePointLeft(scale).stripTrailingZeros().toPlainString()
//    }
//
//    /**
//     * Convert token amount in BiInteger to String fiat amount
//     * 3333 to 0.00003333
//     */
//    fun bigIntegerTokenAmountToStringFiatAmount(
//        cryptoCurrency: CryptoCurrency,
//        tokenAmount: BigInteger,
//        tokenPrice: BigInteger,
//    ): String {
//        if (tokenAmount <= BigInteger.ZERO) return "0"
//
//        if (tokenPrice <= BigInteger.ZERO) return "0"
//
//        val scale = when (cryptoCurrency) {
//            CryptoCurrency.Bitcoin -> BTC_SCALE
//            CryptoCurrency.Ethereum -> ETH_SCALE
//        }
//
//        val multiply = BigDecimal(tokenPrice).setScale(scale, ROUNDING_MODE)
//            .multiply(BigDecimal(tokenAmount).setScale(FIAT_SCALE, ROUNDING_MODE))
//
//        val fiat = multiply.movePointLeft(scale).stripTrailingZeros()
//
//        return fiat.movePointLeft(FIAT_SCALE).setScale(FIAT_SCALE, ROUNDING_MODE).toPlainString()
//    }
//
//    /**
//     * Convert token amount in BiInteger to String fiat amount
//     * 3333 to 0.00003333
//     */
//    fun bigIntegerTokenAmountToBigIntegerFiatAmount(
//        cryptoCurrency: CryptoCurrency,
//        tokenAmount: BigInteger,
//        tokenPrice: BigInteger,
//    ): BigInteger {
//        if (tokenAmount <= BigInteger.ZERO) return BigInteger.ZERO
//
//        if (tokenPrice <= BigInteger.ZERO) return BigInteger.ZERO
//
//        val scale = when (cryptoCurrency) {
//            CryptoCurrency.Bitcoin -> BTC_SCALE
//            CryptoCurrency.Ethereum -> ETH_SCALE
//        }
//
//        val tokenBigDecimal = BigDecimal(tokenAmount).movePointLeft(scale)
//        val tokenPriceBigDecimal = BigDecimal(tokenPrice).movePointLeft(FIAT_SCALE)
//
//        val multiply = tokenBigDecimal.multiply(tokenPriceBigDecimal)
//
//        val multiplyRounded = multiply.setScale(FIAT_SCALE, ROUNDING_MODE)
//
//        return BigInteger(multiplyRounded.movePointRight(FIAT_SCALE).toPlainString())
//    }
//
//    fun BigInteger.tokenToFiat(cryptoCurrency: CryptoCurrency, tokenPrice: BigInteger): BigInteger {
//        return bigIntegerTokenAmountToBigIntegerFiatAmount(cryptoCurrency, this, tokenPrice)
//    }
//
//    /**
//     * Convert token amount in BigInteger to Long token amount
//     * 3333 to 0.00003333
//     */
//    fun bigIntegerTokenAmountToLongTokenAmount(
//        tokenAmount: BigInteger,
//    ): Long {
//        if (tokenAmount <= BigInteger.ZERO) return 0L
//
//        return tokenAmount.toLong()
//    }
//
//    /**
//     * BigDecimal
//     *
//     *
//     *
//     */
//
//    /**
//     * Convert fiat amount in String to BigDecimal
//     * "102.45" to 10245
//     * "102.8" to 10280
//     * "45" to 4500
//     * "0.23" to 23
//     */
//    fun getFiatAmountFromString(amount: String): BigDecimal {
//        val clearedString = amount.replace(Regex("[^\\d.]"), "")
//
//        if (clearedString.isBlank() || clearedString == ".")
//            return BigDecimal.ZERO.setScale(FIAT_SCALE, ROUNDING_MODE)
//
//        return BigDecimal(clearedString).setScale(FIAT_SCALE, ROUNDING_MODE)
//    }
//
//    /**
//     * Convert fiat amount in String to BigDecimal
//     * "102.45" to 10245
//     * "102.8" to 10280
//     * "45" to 4500
//     * "0.23" to 23
//     */
//    fun getCryptoAmountFromString(cryptoCurrency: CryptoCurrency, amount: String): BigDecimal {
//        val scale = when (cryptoCurrency) {
//            CryptoCurrency.Bitcoin -> BTC_SCALE
//            CryptoCurrency.Ethereum -> ETH_SCALE
//        }
//
//        val clearedString = amount.replace(Regex("[^\\d.]"), "")
//
//        if (clearedString.isBlank() || clearedString == ".")
//            return BigDecimal.ZERO.setScale(scale, ROUNDING_MODE)
//
//        return BigDecimal(clearedString).setScale(scale, ROUNDING_MODE)
//    }
//
//    fun fiatToToken(
//        cryptoCurrency: CryptoCurrency,
//        fiatAmount: BigDecimal,
//        tokenPrice: BigDecimal,
//    ): BigDecimal {
//        if (tokenPrice == BigDecimal.ZERO) return BigDecimal.ZERO
//
//        val scale = when (cryptoCurrency) {
//            CryptoCurrency.Bitcoin -> BTC_SCALE
//            CryptoCurrency.Ethereum -> ETH_SCALE
//        }
//
//        return if (fiatAmount > BigDecimal.ZERO) fiatAmount.divide(
//            tokenPrice,
//            scale,
//            ROUNDING_MODE
//        ) else BigDecimal.ZERO
//    }
//
//    fun tokenToFiat(
//        tokenAmount: BigDecimal,
//        tokenPrice: BigDecimal,
//    ): BigDecimal {
//        return if (tokenAmount > BigDecimal.ZERO) {
//            tokenAmount.multiply(tokenPrice).setScale(FIAT_SCALE, ROUNDING_MODE)
//        } else {
//            BigDecimal.ZERO.setScale(FIAT_SCALE, ROUNDING_MODE)
//        }
//    }
//
//    /**
//     * Convert fiat amount in String to token amount in BigDecimal
//     * "25" to 0.00081908
//     * "31000" to 1.01566080
//     */
//    fun getTokenAmountFromFiatString(
//        cryptoCurrency: CryptoCurrency,
//        fiatAmount: String,
//        tokenPrice: BigDecimal,
//    ): BigDecimal {
//        val scale = when (cryptoCurrency) {
//            CryptoCurrency.Bitcoin -> BTC_SCALE
//            CryptoCurrency.Ethereum -> ETH_SCALE
//        }
//
//        val mc = MathContext(scale)
//
//        val cleaned = fiatAmount.replace(Regex("[^\\d.]"), "")
//
//        if (fiatAmount.isBlank()) return BigDecimal.ZERO.setScale(scale, ROUNDING_MODE)
//
//        val fiatAmountDouble = cleaned.toDouble()
//        val tokenPriceDouble = tokenPrice.toDouble()
//
//        println("fiatAmountDouble = $fiatAmountDouble")
//        println("tokenPriceDouble = $tokenPriceDouble")
//
//        if (fiatAmountDouble == 0.0) return BigDecimal.ZERO.setScale(scale, ROUNDING_MODE)
//
//        val tokenAmount = fiatAmountDouble / tokenPriceDouble
//        println("tokenAmount = $tokenAmount")
//
//        val tokenAmountBigDecimal = BigDecimal(tokenAmount, mc).setScale(scale, ROUNDING_MODE)
//        println("tokenAmountBigDecimal = $tokenAmountBigDecimal")
//
//        return tokenAmountBigDecimal
//    }
//
//    fun tokenAmountToLong(
//        cryptoCurrency: CryptoCurrency,
//        tokenAmount: BigDecimal,
//    ): Long {
//        val scale = when (cryptoCurrency) {
//            CryptoCurrency.Bitcoin -> BTC_SCALE
//            CryptoCurrency.Ethereum -> ETH_SCALE
//        }
//
//        if (tokenAmount.stripTrailingZeros() == BigDecimal.ZERO) return 0L
//
//        val tokenAmountBigDecimal = tokenAmount.setScale(scale, ROUNDING_MODE)
//
//        val tokenAmountStringCleaned = tokenAmountBigDecimal.toString()
//            .trimStart('0')
//            .replace(".", "")
//
//        return tokenAmountStringCleaned.toLong()
//    }
//
//    fun parseTokenAmount(
//        cryptoCurrency: CryptoCurrency,
//        tokenAmount: String,
//    ): BigDecimal {
//        val scale = when (cryptoCurrency) {
//            CryptoCurrency.Bitcoin -> BTC_SCALE
//            CryptoCurrency.Ethereum -> ETH_SCALE
//        }
//
//        if (tokenAmount.isBlank()) return BigDecimal.ZERO
//
//        return BigDecimal(tokenAmount)
//            .movePointLeft(scale)
//            .setScale(scale, ROUNDING_MODE)
//    }
//
//    private val ROUNDING_MODE = RoundingMode.HALF_UP
//    private const val FIAT_SCALE = 2
//    private const val BTC_SCALE = 8
//    private const val ETH_SCALE = 18
//}