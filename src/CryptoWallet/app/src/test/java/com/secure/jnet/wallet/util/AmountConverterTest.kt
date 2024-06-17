package com.secure.jnet.wallet.util

import com.secure.jnet.wallet.domain.models.enums.CryptoCurrency
import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

class AmountConverterTest {

    @Test
    fun `test convert bitcoin token amount in String to BigInteger`() {
        // Given
        val cryptoCurrency = CryptoCurrency.Bitcoin

        val amount0 = ""
        val expectedResult0 = BigInteger("0")

        val amount1 = "0.00003333"
        val expectedResult1 = BigInteger("3333")

        val amount2 = "102.8"
        val expectedResult2 = BigInteger("10280000000")

        val amount3 = "0.23"
        val expectedResult3 = BigInteger("23000000")

        val amount4 = "0.00000004"
        val expectedResult4 = BigInteger("4")

        val amount5 = "22"
        val expectedResult5 = BigInteger("2200000000")

        val amount6 = "0.0005"
        val expectedResult6 = BigInteger("50000")

        // When
        val result0 = AmountConverter.stringTokenAmountToBigIntegerTokenAmount(cryptoCurrency, amount0)
        val result1 = AmountConverter.stringTokenAmountToBigIntegerTokenAmount(cryptoCurrency, amount1)
        val result2 = AmountConverter.stringTokenAmountToBigIntegerTokenAmount(cryptoCurrency, amount2)
        val result3 = AmountConverter.stringTokenAmountToBigIntegerTokenAmount(cryptoCurrency, amount3)
        val result4 = AmountConverter.stringTokenAmountToBigIntegerTokenAmount(cryptoCurrency, amount4)
        val result5 = AmountConverter.stringTokenAmountToBigIntegerTokenAmount(cryptoCurrency, amount5)
        val result6 = AmountConverter.stringTokenAmountToBigIntegerTokenAmount(cryptoCurrency, amount6)

        print("result0 = \"$result0\"\n")
        print("result1 = \"$result1\"\n")
        print("result2 = \"$result2\"\n")
        print("result3 = \"$result3\"\n")
        print("result4 = \"$result4\"\n")
        print("result5 = \"$result5\"\n")
        print("result6 = \"$result6\"\n")

        // Then
        assertEquals("Passes", expectedResult0, result0)
        assertEquals("Passes", 0, expectedResult0.toInt())

        assertEquals("Passes", expectedResult1, result1)
        assertEquals("Passes", 3333, expectedResult1.toInt())

        assertEquals("Passes", expectedResult2, result2)
        assertEquals("Passes", 10280000000L, expectedResult2.toLong())

        assertEquals("Passes", expectedResult3, result3)
        assertEquals("Passes", 23000000, expectedResult3.toInt())

        assertEquals("Passes", expectedResult4, result4)
        assertEquals("Passes", 4, expectedResult4.toInt())

        assertEquals("Passes", expectedResult5, result5)
        assertEquals("Passes", 2200000000L, expectedResult5.toLong())

        assertEquals("Passes", expectedResult6, result6)
        assertEquals("Passes", 50000, expectedResult6.toInt())
    }

    @Test
    fun `test convert ethereum token amount in String to BigInteger`() {
        // Given
        val cryptoCurrency = CryptoCurrency.Ethereum

        val amount0 = ""
        val expectedResult0 = BigInteger("0")

        val amount1 = "0.000000000000003333"
        val expectedResult1 = BigInteger("3333")

        val amount2 = "102.8"
        val expectedResult2 = BigInteger("102800000000000000000")

        val amount3 = "0.23"
        val expectedResult3 = BigInteger("230000000000000000")

        val amount4 = "0.00000004"
        val expectedResult4 = BigInteger("40000000000")

        // When
        val result0 = AmountConverter.stringTokenAmountToBigIntegerTokenAmount(cryptoCurrency, amount0)
        val result1 = AmountConverter.stringTokenAmountToBigIntegerTokenAmount(cryptoCurrency, amount1)
        val result2 = AmountConverter.stringTokenAmountToBigIntegerTokenAmount(cryptoCurrency, amount2)
        val result3 = AmountConverter.stringTokenAmountToBigIntegerTokenAmount(cryptoCurrency, amount3)
        val result4 = AmountConverter.stringTokenAmountToBigIntegerTokenAmount(cryptoCurrency, amount4)

        print("result0 = \"$result0\"\n")
        print("result1 = \"$result1\"\n")
        print("result2 = \"$result2\"\n")
        print("result3 = \"$result3\"\n")
        print("result4 = \"$result4\"\n")

        // Then
        assertEquals("Passes", expectedResult0, result0)
        assertEquals("Passes", 0L, expectedResult0.toLong())

        assertEquals("Passes", expectedResult1, result1)
        assertEquals("Passes", 3333L, expectedResult1.toLong())

        assertEquals("Passes", expectedResult2, result2)

        assertEquals("Passes", expectedResult3, result3)
        assertEquals("Passes", 230000000000000000L, expectedResult3.toLong())

        assertEquals("Passes", expectedResult4, result4)
        assertEquals("Passes", 40000000000L, expectedResult4.toLong())
    }

    @Test
    fun `test convert fiat amount to token amount bitcoin`() {
        // Given
        val cryptoCurrency = CryptoCurrency.Bitcoin

        val fiatAmount0 = "0"
        val tokenPrice0 = BigInteger("5198956")
        val expectedTokenAmount0 = BigInteger("0")

        val fiatAmount1 = "100.00"
        val tokenPrice1 = BigInteger("5198956")
        val expectedTokenAmount1 = BigInteger("192346")

        val fiatAmount2 = ".0"
        val tokenPrice2 = BigInteger("5198956")
        val expectedTokenAmount2 = BigInteger("0")

        val fiatAmount3 = "100"
        val tokenPrice3 = BigInteger("5198956")
        val expectedTokenAmount3 = BigInteger("192346")

        val fiatAmount4 = "53000"
        val tokenPrice4 = BigInteger("5198956")
        val expectedTokenAmount4 = BigInteger("101943544")

        // When
        val result0 = AmountConverter.stringFiatAmountToTokenBigIntegerAmount(cryptoCurrency, fiatAmount0, tokenPrice0)
        val result1 = AmountConverter.stringFiatAmountToTokenBigIntegerAmount(cryptoCurrency, fiatAmount1, tokenPrice1)
        val result2 = AmountConverter.stringFiatAmountToTokenBigIntegerAmount(cryptoCurrency, fiatAmount2, tokenPrice2)
        val result3 = AmountConverter.stringFiatAmountToTokenBigIntegerAmount(cryptoCurrency, fiatAmount3, tokenPrice3)
        val result4 = AmountConverter.stringFiatAmountToTokenBigIntegerAmount(cryptoCurrency, fiatAmount4, tokenPrice4)

        print("result0 = \"$result0\"\n")
        print("result1 = \"$result1\"\n")
        print("result2 = \"$result2\"\n")
        print("result3 = \"$result3\"\n")
        print("result4 = \"$result4\"\n")

        // Then
        assertEquals("Passes", expectedTokenAmount0, result0)
        assertEquals("Passes", expectedTokenAmount1, result1)
        assertEquals("Passes", expectedTokenAmount2, result2)
        assertEquals("Passes", expectedTokenAmount3, result3)
        assertEquals("Passes", expectedTokenAmount4, result4)
    }

    @Test
    fun `test convert fiat amount to token amount ethereum`() {
        // Given
        val cryptoCurrency = CryptoCurrency.Ethereum

        val fiatAmount0 = "0"
        val tokenPrice0 = BigInteger("289675")
        val expectedTokenAmount0 = BigInteger("0")

        val fiatAmount1 = "100.00"
        val tokenPrice1 = BigInteger("289952")
        val expectedTokenAmount1 = BigInteger("34488467056616268")

        val fiatAmount2 = ".0"
        val tokenPrice2 = BigInteger("289675")
        val expectedTokenAmount2 = BigInteger("0")

        val fiatAmount3 = "100"
        val tokenPrice3 = BigInteger("289952")
        val expectedTokenAmount3 = BigInteger("34488467056616268")

        val fiatAmount4 = "16000"
        val tokenPrice4 = BigInteger("289952")
        val expectedTokenAmount4 = BigInteger("5518154729058602803")

        // When
        val result0 = AmountConverter.stringFiatAmountToTokenBigIntegerAmount(cryptoCurrency, fiatAmount0, tokenPrice0)
        val result1 = AmountConverter.stringFiatAmountToTokenBigIntegerAmount(cryptoCurrency, fiatAmount1, tokenPrice1)
        val result2 = AmountConverter.stringFiatAmountToTokenBigIntegerAmount(cryptoCurrency, fiatAmount2, tokenPrice2)
        val result3 = AmountConverter.stringFiatAmountToTokenBigIntegerAmount(cryptoCurrency, fiatAmount3, tokenPrice3)
        val result4 = AmountConverter.stringFiatAmountToTokenBigIntegerAmount(cryptoCurrency, fiatAmount4, tokenPrice4)

        print("result0 = \"$result0\"\n")
        print("result1 = \"$result1\"\n")
        print("result2 = \"$result2\"\n")
        print("result3 = \"$result3\"\n")
        print("result4 = \"$result4\"\n")

        // Then
        assertEquals("Passes", expectedTokenAmount0, result0)
        assertEquals("Passes", expectedTokenAmount1, result1)
        assertEquals("Passes", expectedTokenAmount2, result2)
        assertEquals("Passes", expectedTokenAmount3, result3)
        assertEquals("Passes", expectedTokenAmount4, result4)
    }

    @Test
    fun `convert token amount to token string`() {
        // Given
        val cryptoCurrency0 = CryptoCurrency.Bitcoin
        val tokenAmount0 = BigInteger("3000")
        val expectedTokenAmount0 = "0.00003"

        val cryptoCurrency1 = CryptoCurrency.Bitcoin
        val tokenAmount1 = BigInteger("2200000000")
        val expectedTokenAmount1 = "22"

        val cryptoCurrency2 = CryptoCurrency.Bitcoin
        val tokenAmount2 = BigInteger("150700001")
        val expectedTokenAmount2 = "1.50700001"

        val cryptoCurrency3 = CryptoCurrency.Ethereum
        val tokenAmount3 = BigInteger("3000")
        val expectedTokenAmount3 = "0.000000000000003"

        val cryptoCurrency4 = CryptoCurrency.Ethereum
        val tokenAmount4 = BigInteger("5")
        val expectedTokenAmount4 = "0.000000000000000005"

        // When
        val result0 = AmountConverter.bigIntegerTokenAmountToStringTokenAmount(cryptoCurrency0, tokenAmount0)
        val result1 = AmountConverter.bigIntegerTokenAmountToStringTokenAmount(cryptoCurrency1, tokenAmount1)
        val result2 = AmountConverter.bigIntegerTokenAmountToStringTokenAmount(cryptoCurrency2, tokenAmount2)
        val result3 = AmountConverter.bigIntegerTokenAmountToStringTokenAmount(cryptoCurrency3, tokenAmount3)
        val result4 = AmountConverter.bigIntegerTokenAmountToStringTokenAmount(cryptoCurrency4, tokenAmount4)

        print("result0 = \"$result0\"\n")
        print("result1 = \"$result1\"\n")
        print("result2 = \"$result2\"\n")
        print("result3 = \"$result3\"\n")
        print("result4 = \"$result4\"\n")

        // Then
        assertEquals("Passes", expectedTokenAmount0, result0)
        assertEquals("Passes", expectedTokenAmount1, result1)
        assertEquals("Passes", expectedTokenAmount2, result2)
        assertEquals("Passes", expectedTokenAmount3, result3)
        assertEquals("Passes", expectedTokenAmount4, result4)
    }

    @Test
    fun `convert token amount to fiat string`() {
        // Given
        val cryptoCurrency0 = CryptoCurrency.Bitcoin
        val tokenPrice0 = BigInteger("5200000")
        val tokenAmount0 = BigInteger("3000")
        val expectedFiatAmount0 = "1.56"

        val cryptoCurrency1 = CryptoCurrency.Bitcoin
        val tokenPrice1 = BigInteger("5209900")
        val tokenAmount1 = BigInteger("100000000")
        val expectedFiatAmount1 = "52099.00"

        val cryptoCurrency2 = CryptoCurrency.Bitcoin
        val tokenPrice2 = BigInteger("5209900")
        val tokenAmount2 = BigInteger("150000000")
        val expectedFiatAmount2 = "78148.50"

        val cryptoCurrency3 = CryptoCurrency.Ethereum
        val tokenPrice3 = BigInteger("293295")
        val tokenAmount3 = BigInteger("3409536473516425")
        val expectedFiatAmount3 = "10.00"

        val cryptoCurrency4 = CryptoCurrency.Ethereum
        val tokenPrice4 = BigInteger("293295")
        val tokenAmount4 = BigInteger("1500000000000000000")
        val expectedFiatAmount4 = "4399.43"

        val cryptoCurrency5 = CryptoCurrency.Bitcoin
        val tokenPrice5 = BigInteger("6858633")
        val tokenAmount5 = BigInteger("1729834")
        val expectedFiatAmount5 = "1186.43"

        // When
        val result0 = AmountConverter.bigIntegerTokenAmountToStringFiatAmount(cryptoCurrency0, tokenAmount0, tokenPrice0)
        val result1 = AmountConverter.bigIntegerTokenAmountToStringFiatAmount(cryptoCurrency1, tokenAmount1, tokenPrice1)
        val result2 = AmountConverter.bigIntegerTokenAmountToStringFiatAmount(cryptoCurrency2, tokenAmount2, tokenPrice2)
        val result3 = AmountConverter.bigIntegerTokenAmountToStringFiatAmount(cryptoCurrency3, tokenAmount3, tokenPrice3)
        val result4 = AmountConverter.bigIntegerTokenAmountToStringFiatAmount(cryptoCurrency4, tokenAmount4, tokenPrice4)
        val result5 = AmountConverter.bigIntegerTokenAmountToStringFiatAmount(cryptoCurrency5, tokenAmount5, tokenPrice5)

        print("result0 = \"$result0\"\n")
        print("result1 = \"$result1\"\n")
        print("result2 = \"$result2\"\n")
        print("result3 = \"$result3\"\n")
        print("result4 = \"$result4\"\n")
        print("result5 = \"$result5\"\n")

        // Then
        assertEquals("Passes", expectedFiatAmount0, result0)
        assertEquals("Passes", expectedFiatAmount1, result1)
        assertEquals("Passes", expectedFiatAmount2, result2)
        assertEquals("Passes", expectedFiatAmount3, result3)
        assertEquals("Passes", expectedFiatAmount4, result4)
        assertEquals("Passes", expectedFiatAmount5, result5)
    }

    @Test
    fun `convert token amount to fiat amount`() {
        // Given
        val cryptoCurrency0 = CryptoCurrency.Bitcoin
        val tokenPrice0 = BigInteger("5200000")
        val tokenAmount0 = BigInteger("3000")
        val expectedFiatAmount0 = BigInteger("156")

        val cryptoCurrency1 = CryptoCurrency.Bitcoin
        val tokenPrice1 = BigInteger("5209900")
        val tokenAmount1 = BigInteger("100000000")
        val expectedFiatAmount1 = BigInteger("5209900")

        val cryptoCurrency2 = CryptoCurrency.Bitcoin
        val tokenPrice2 = BigInteger("5209900")
        val tokenAmount2 = BigInteger("150000000")
        val expectedFiatAmount2 = BigInteger("7814850")

        val cryptoCurrency3 = CryptoCurrency.Ethereum
        val tokenPrice3 = BigInteger("293295")
        val tokenAmount3 = BigInteger("3409536473516425")
        val expectedFiatAmount3 = BigInteger("1000")

        val cryptoCurrency4 = CryptoCurrency.Ethereum
        val tokenPrice4 = BigInteger("293295")
        val tokenAmount4 = BigInteger("1500000000000000000")
        val expectedFiatAmount4 = BigInteger("439943")

        val cryptoCurrency5 = CryptoCurrency.Bitcoin
        val tokenPrice5 = BigInteger("6858633")
        val tokenAmount5 = BigInteger("1729834")
        val expectedFiatAmount5 = BigInteger("118643")

        val cryptoCurrency6 = CryptoCurrency.Bitcoin
        val tokenPrice6 = BigInteger("6737341")
        val tokenAmount6 = BigInteger("1729834")
        val expectedFiatAmount6 = BigInteger("116545")

        // When
        val result0 = AmountConverter.bigIntegerTokenAmountToBigIntegerFiatAmount(cryptoCurrency0, tokenAmount0, tokenPrice0)
        val result1 = AmountConverter.bigIntegerTokenAmountToBigIntegerFiatAmount(cryptoCurrency1, tokenAmount1, tokenPrice1)
        val result2 = AmountConverter.bigIntegerTokenAmountToBigIntegerFiatAmount(cryptoCurrency2, tokenAmount2, tokenPrice2)
        val result3 = AmountConverter.bigIntegerTokenAmountToBigIntegerFiatAmount(cryptoCurrency3, tokenAmount3, tokenPrice3)
        val result4 = AmountConverter.bigIntegerTokenAmountToBigIntegerFiatAmount(cryptoCurrency4, tokenAmount4, tokenPrice4)
        val result5 = AmountConverter.bigIntegerTokenAmountToBigIntegerFiatAmount(cryptoCurrency5, tokenAmount5, tokenPrice5)
        val result6 = AmountConverter.bigIntegerTokenAmountToBigIntegerFiatAmount(cryptoCurrency6, tokenAmount6, tokenPrice6)

        print("result0 = \"$result0\"\n")
        print("result1 = \"$result1\"\n")
        print("result2 = \"$result2\"\n")
        print("result3 = \"$result3\"\n")
        print("result4 = \"$result4\"\n")
        print("result5 = \"$result5\"\n")
        print("result6 = \"$result6\"\n")

        // Then
        assertEquals("Passes", expectedFiatAmount0, result0)
        assertEquals("Passes", expectedFiatAmount1, result1)
        assertEquals("Passes", expectedFiatAmount2, result2)
        assertEquals("Passes", expectedFiatAmount3, result3)
        assertEquals("Passes", expectedFiatAmount4, result4)
        assertEquals("Passes", expectedFiatAmount5, result5)
        assertEquals("Passes", expectedFiatAmount6, result6)
    }

    @Test
    fun `convert token amount to long`() {
        // Given
        val tokenAmount0 = BigInteger("3000")

        val tokenAmount1 = BigInteger("100000000")

        // When
        val result0 = AmountConverter.bigIntegerTokenAmountToLongTokenAmount(tokenAmount0)
        val result1 = AmountConverter.bigIntegerTokenAmountToLongTokenAmount(tokenAmount1)

        print("result0 = \"$result0\"\n")
        print("result1 = \"$result1\"\n")

        // Then
        assertEquals("Passes", 3000, result0)
        assertEquals("Passes", 100000000, result1)
    }

    @Test
    fun `test convert fiat amount in String to BigDecimal`() {
        // Given
        val amount0 = ""
        val expectedResult0 = BigDecimal(0).setScale(2, RoundingMode.HALF_UP)

        val amount1 = "0.00"
        val expectedResult1 = BigDecimal(0).setScale(2, RoundingMode.HALF_UP)

        val amount2 = "$102.8"
        val expectedResult2 = BigDecimal(102.8).setScale(2, RoundingMode.HALF_UP)

        val amount3 = "$0.23"
        val expectedResult3 = BigDecimal(0.23).setScale(2, RoundingMode.HALF_UP)

        val amount4 = "$0.04"
        val expectedResult4 = BigDecimal(0.04).setScale(2, RoundingMode.HALF_UP)

        val amount5 = "$10,990.22"
        val expectedResult5 = BigDecimal(10990.22).setScale(2, RoundingMode.HALF_UP)

        val amount6 = "$444"
        val expectedResult6 = BigDecimal(444.00).setScale(2, RoundingMode.HALF_UP)

        val amount7 = "100"
        val expectedResult7 = BigDecimal(100.00).setScale(2, RoundingMode.HALF_UP)

        val amount8 = "$102.80969"
        val expectedResult8 = BigDecimal(102.81).setScale(2, RoundingMode.HALF_UP)

        val amount9 = "$"
        val expectedResult9 = BigDecimal(0).setScale(2, RoundingMode.HALF_UP)

        val amount10 = "."
        val expectedResult10 = BigDecimal(0).setScale(2, RoundingMode.HALF_UP)

        // When
        val result0 = AmountConverter.getFiatAmountFromString(amount0)
        val result1 = AmountConverter.getFiatAmountFromString(amount1)
        val result2 = AmountConverter.getFiatAmountFromString(amount2)
        val result3 = AmountConverter.getFiatAmountFromString(amount3)
        val result4 = AmountConverter.getFiatAmountFromString(amount4)
        val result5 = AmountConverter.getFiatAmountFromString(amount5)
        val result6 = AmountConverter.getFiatAmountFromString(amount6)
        val result7 = AmountConverter.getFiatAmountFromString(amount7)
        val result8 = AmountConverter.getFiatAmountFromString(amount8)
        val result9 = AmountConverter.getFiatAmountFromString(amount9)
        val result10 = AmountConverter.getFiatAmountFromString(amount10)

        print("result0 = \"$result0\"\n")
        print("result1 = \"$result1\"\n")
        print("result2 = \"$result2\"\n")
        print("result3 = \"$result3\"\n")
        print("result4 = \"$result4\"\n")
        print("result5 = \"$result5\"\n")
        print("result6 = \"$result6\"\n")
        print("result7 = \"$result7\"\n")
        print("result8 = \"$result8\"\n")
        print("result9 = \"$result9\"\n")
        print("result10 = \"$result10\"\n")

        // Then
        assertEquals("Passes", expectedResult0, result0)
        assertEquals("Passes", expectedResult1, result1)
        assertEquals("Passes", expectedResult2, result2)
        assertEquals("Passes", expectedResult3, result3)
        assertEquals("Passes", expectedResult4, result4)
        assertEquals("Passes", expectedResult5, result5)
        assertEquals("Passes", expectedResult6, result6)
        assertEquals("Passes", expectedResult7, result7)
        assertEquals("Passes", expectedResult8, result8)
        assertEquals("Passes", expectedResult9, result9)
        assertEquals("Passes", expectedResult10, result10)
    }

    @Test
    fun `test convert crypto amount in String to BigDecimal BTC`() {
        val cryptoCurrency = CryptoCurrency.Bitcoin
        val tokenScale = 8

        // Given
        val amount0 = ""
        val expectedResult0 = BigDecimal.ZERO.setScale(tokenScale, RoundingMode.HALF_UP)

        val amount1 = "0.00"
        val expectedResult1 = BigDecimal.ZERO.setScale(tokenScale, RoundingMode.HALF_UP)

        val amount2 = "1.0"
        val expectedResult2 = BigDecimal(1.0).setScale(tokenScale, RoundingMode.HALF_UP)

        val amount3 = "0.23"
        val expectedResult3 = BigDecimal(0.23).setScale(tokenScale, RoundingMode.HALF_UP)

        val amount4 = "0.0001"
        val expectedResult4 = BigDecimal(0.0001).setScale(tokenScale, RoundingMode.HALF_UP)

        val amount5 = "."
        val expectedResult5 = BigDecimal.ZERO.setScale(tokenScale, RoundingMode.HALF_UP)

        val amount6 = ".23"
        val expectedResult6 = BigDecimal(0.23).setScale(tokenScale, RoundingMode.HALF_UP)

        // When
        val result0 = AmountConverter.getCryptoAmountFromString(cryptoCurrency, amount0)
        val result1 = AmountConverter.getCryptoAmountFromString(cryptoCurrency, amount1)
        val result2 = AmountConverter.getCryptoAmountFromString(cryptoCurrency, amount2)
        val result3 = AmountConverter.getCryptoAmountFromString(cryptoCurrency, amount3)
        val result4 = AmountConverter.getCryptoAmountFromString(cryptoCurrency, amount4)
        val result5 = AmountConverter.getCryptoAmountFromString(cryptoCurrency, amount5)
        val result6 = AmountConverter.getCryptoAmountFromString(cryptoCurrency, amount6)

        print("result0 = \"$result0\"\n")
        print("result1 = \"$result1\"\n")
        print("result2 = \"$result2\"\n")
        print("result3 = \"$result3\"\n")
        print("result4 = \"$result4\"\n")
        print("result5 = \"$result5\"\n")
        print("result6 = \"$result6\"\n")

        // Then
        assertEquals("Passes", expectedResult0, result0)
        assertEquals("Passes", expectedResult1, result1)
        assertEquals("Passes", expectedResult2, result2)
        assertEquals("Passes", expectedResult3, result3)
        assertEquals("Passes", expectedResult4, result4)
        assertEquals("Passes", expectedResult5, result5)
        assertEquals("Passes", expectedResult6, result6)
    }

    @Test
    fun `test convert crypto amount in String to BigDecimal ETH`() {
        val cryptoCurrency = CryptoCurrency.Ethereum
        val tokenScale = 18

        // Given
        val amount0 = ""
        val expectedResult0 = BigDecimal.ZERO.setScale(tokenScale, RoundingMode.HALF_UP)

        val amount1 = "0.00"
        val expectedResult1 = BigDecimal.ZERO.setScale(tokenScale, RoundingMode.HALF_UP)

        val amount2 = "1.0"
        val expectedResult2 = BigDecimal(1.0).setScale(tokenScale, RoundingMode.HALF_UP)

        val amount3 = "0.23"
        val expectedResult3 = "0.230000000000000000"

        val amount4 = "0.0001"
        val expectedResult4 = BigDecimal(0.0001).setScale(tokenScale, RoundingMode.HALF_UP)

        val amount5 = "0.00000000000000000001"
        val expectedResult5 = "0.000000000000000000"

        val amount6 = "0.0000000000000000009"
        val expectedResult6 = "0.000000000000000001"

        val amount7 = "0.000000000000000004"
        val expectedResult7 = "0.000000000000000004"

        // When
        val result0 = AmountConverter.getCryptoAmountFromString(cryptoCurrency, amount0)
        val result1 = AmountConverter.getCryptoAmountFromString(cryptoCurrency, amount1)
        val result2 = AmountConverter.getCryptoAmountFromString(cryptoCurrency, amount2)
        val result3 = AmountConverter.getCryptoAmountFromString(cryptoCurrency, amount3)
        val result4 = AmountConverter.getCryptoAmountFromString(cryptoCurrency, amount4)
        val result5 = AmountConverter.getCryptoAmountFromString(cryptoCurrency, amount5)
        val result6 = AmountConverter.getCryptoAmountFromString(cryptoCurrency, amount6)
        val result7 = AmountConverter.getCryptoAmountFromString(cryptoCurrency, amount7)

        print("result0 = \"$result0\"\n")
        print("result1 = \"$result1\"\n")
        print("result2 = \"$result2\"\n")
        print("result3 = \"${result3.toPlainString()}\"\n")
        print("result4 = \"$result4\"\n")
        print("result5 = \"${result5.toPlainString()}\"\n")
        print("result6 = \"${result6.toPlainString()}\"\n")
        print("result7 = \"${result7.toPlainString()}\"\n")

        // Then
        assertEquals("Passes", expectedResult0, result0)
        assertEquals("Passes", expectedResult1, result1)
        assertEquals("Passes", expectedResult2, result2)
        assertEquals("Passes", expectedResult3, result3.toPlainString())
        assertEquals("Passes", expectedResult4, result4)
        assertEquals("Passes", expectedResult5, result5.toPlainString())
        assertEquals("Passes", expectedResult6, result6.toPlainString())
        assertEquals("Passes", expectedResult7, result7.toPlainString())
    }


    @Test
    fun `test convert fiat amount to token amount BTC`() {
        // Given
        val cryptoCurrency = CryptoCurrency.Bitcoin
        val tokenScale = 8

        val fiatAmount0 = BigDecimal(30102.40)
        val tokenPrice0 = BigDecimal(30102.40)
        val expectedTokenAmount0 = BigDecimal(1.0).setScale(tokenScale, RoundingMode.HALF_UP)

        val fiatAmount1 = BigDecimal(100.00)
        val tokenPrice1 = BigDecimal(30102.40)
        val expectedTokenAmount1 = BigDecimal(0.00332199).setScale(tokenScale, RoundingMode.HALF_UP)

        val fiatAmount2 = BigDecimal(555.00)
        val tokenPrice2 = BigDecimal(30102.40)
        val expectedTokenAmount2 = BigDecimal(0.01843707).setScale(tokenScale, RoundingMode.HALF_UP)

        val fiatAmount3 = BigDecimal(50000.00)
        val tokenPrice3 = BigDecimal(30102.40)
        val expectedTokenAmount3 = BigDecimal(1.66099713).setScale(tokenScale, RoundingMode.HALF_UP)

        // When
        val result0 = AmountConverter.fiatToToken(cryptoCurrency, fiatAmount0, tokenPrice0)
        val result1 = AmountConverter.fiatToToken(cryptoCurrency, fiatAmount1, tokenPrice1)
        val result2 = AmountConverter.fiatToToken(cryptoCurrency, fiatAmount2, tokenPrice2)
        val result3 = AmountConverter.fiatToToken(cryptoCurrency, fiatAmount3, tokenPrice3)

        print("result0 = \"$result0\"\n")
        print("result1 = \"$result1\"\n")
        print("result2 = \"$result2\"\n")
        print("result3 = \"$result3\"\n")

        // Then
        assertEquals("Passes", expectedTokenAmount0, result0)
        assertEquals("Passes", expectedTokenAmount1, result1)
        assertEquals("Passes", expectedTokenAmount2, result2)
        assertEquals("Passes", expectedTokenAmount3, result3)
    }

    @Test
    fun `test convert fiat amount to token amount BTC, token price is zero`() {
        // Given
        val cryptoCurrency = CryptoCurrency.Bitcoin
        val tokenScale = 8

        val fiatAmount0 = BigDecimal(30102.40)
        val tokenPrice0 = BigDecimal(0.0)
        val expectedTokenAmount0 = BigDecimal(0.0)

        // When
        val result0 = AmountConverter.fiatToToken(cryptoCurrency, fiatAmount0, tokenPrice0)

        print("result0 = \"$result0\"\n")

        // Then
        assertEquals("Passes", expectedTokenAmount0, result0)
    }

    @Test
    fun `test convert fiat amount to token amount ETH`() {
        // Given
        val cryptoCurrency = CryptoCurrency.Ethereum
        val tokenScale = 18

        val fiatAmount0 = BigDecimal(1861.28)
        val tokenPrice0 = BigDecimal(1861.28)
        val expectedTokenAmount0 = BigDecimal(1.0).setScale(tokenScale, RoundingMode.HALF_UP)

        val fiatAmount1 = BigDecimal(100.00)
        val tokenPrice1 = BigDecimal(1861.51)
        val expectedTokenAmount1 = "0.053719829600700507"

        val fiatAmount2 = BigDecimal(555.00)
        val tokenPrice2 = BigDecimal(1861.28)
        val expectedTokenAmount2 = BigDecimal(0.298181896329407724).setScale(tokenScale, RoundingMode.HALF_UP)

        val fiatAmount3 = BigDecimal(50000.00)
        val tokenPrice3 = BigDecimal(1861.28)
        val expectedTokenAmount3 = "26.863233903550245386"

        // When
        val result0 = AmountConverter.fiatToToken(cryptoCurrency, fiatAmount0, tokenPrice0)
        val result1 = AmountConverter.fiatToToken(cryptoCurrency, fiatAmount1, tokenPrice1)
        val result2 = AmountConverter.fiatToToken(cryptoCurrency, fiatAmount2, tokenPrice2)
        val result3 = AmountConverter.fiatToToken(cryptoCurrency, fiatAmount3, tokenPrice3)

        print("result0 = \"$result0\"\n")
        print("result1 = \"$result1\"\n")
        print("result2 = \"$result2\"\n")
        print("result3 = \"$result3\"\n")

        // Then
        assertEquals("Passes", expectedTokenAmount0, result0)
        assertEquals("Passes", expectedTokenAmount1, result1.toPlainString())
        assertEquals("Passes", expectedTokenAmount3, result3.toPlainString())
    }

    @Test
    fun `test convert token amount to fiat amount BTC`() {
        // Given
        val tokenAmount0 = BigDecimal(0.0)
        val tokenPrice0 = BigDecimal(1861.28)
        val expectedTokenAmount0 = BigDecimal(0.0).setScale(2, RoundingMode.HALF_UP)

        val tokenAmount1 = BigDecimal(1.0)
        val tokenPrice1 = BigDecimal(1861.28)
        val expectedTokenAmount1 = BigDecimal(1861.28).setScale(2, RoundingMode.HALF_UP)

        val tokenAmount2 = BigDecimal(0.5)
        val tokenPrice2 = BigDecimal(1861.28)
        val expectedTokenAmount2 = BigDecimal(930.64).setScale(2, RoundingMode.HALF_UP)

        val tokenAmount3 = BigDecimal(0.000000001)
        val tokenPrice3 = BigDecimal(1861.28)
        val expectedTokenAmount3 = BigDecimal(0.00000186128).setScale(2, RoundingMode.HALF_UP)

        // When
        val result0 = AmountConverter.tokenToFiat(tokenAmount0, tokenPrice0)
        val result1 = AmountConverter.tokenToFiat(tokenAmount1, tokenPrice1)
        val result2 = AmountConverter.tokenToFiat(tokenAmount2, tokenPrice2)
        val result3 = AmountConverter.tokenToFiat(tokenAmount3, tokenPrice3)

        print("result0 = \"$result0\"\n")
        print("result1 = \"$result1\"\n")
        print("result2 = \"$result2\"\n")
        print("result3 = \"$result3\"\n")

        // Then
        assertEquals("Passes", expectedTokenAmount0, result0)
        assertEquals("Passes", expectedTokenAmount1, result1)
        assertEquals("Passes", expectedTokenAmount2, result2)
        assertEquals("Passes", expectedTokenAmount3, result3)
    }

    @Test
    fun `test get token amount in BigDecimal from fiat amount String BTC`() {
        val cryptoCurrency = CryptoCurrency.Bitcoin
        val tokenScale = 8

        // Given
        val amount0 = ""
        val tokenPrice0 = BigDecimal(30102.40)
        val expectedTokenAmount0 = BigDecimal.ZERO.setScale(tokenScale, RoundingMode.HALF_UP)

        // Given
        val amount1 = "0"
        val tokenPrice1 = BigDecimal(30102.40)
        val expectedTokenAmount1 = BigDecimal.ZERO.setScale(tokenScale, RoundingMode.HALF_UP)

        val amount2 = "5"
        val tokenPrice2 = BigDecimal(30102.40)
        val expectedTokenAmount2 = BigDecimal(0.00016610).setScale(tokenScale, RoundingMode.HALF_UP)

        val amount3 = "20.50"
        val tokenPrice3 = BigDecimal(30102.40)
        val expectedTokenAmount3 = BigDecimal(0.00068101).setScale(tokenScale, RoundingMode.HALF_UP)

        val amount4 = "100"
        val tokenPrice4 = BigDecimal(30102.40)
        val expectedTokenAmount4 = BigDecimal(0.00332199).setScale(tokenScale, RoundingMode.HALF_UP)

        val amount5 = "31000"
        val tokenPrice5 = BigDecimal(30102.40)
        val expectedTokenAmount5 = BigDecimal(1.02981820).setScale(tokenScale, RoundingMode.HALF_UP)

        // When
        val result0 = AmountConverter.getTokenAmountFromFiatString(cryptoCurrency, amount0, tokenPrice0)
        val result1 = AmountConverter.getTokenAmountFromFiatString(cryptoCurrency, amount1, tokenPrice1)
        val result2 = AmountConverter.getTokenAmountFromFiatString(cryptoCurrency, amount2, tokenPrice2)
        val result3 = AmountConverter.getTokenAmountFromFiatString(cryptoCurrency, amount3, tokenPrice3)
        val result4 = AmountConverter.getTokenAmountFromFiatString(cryptoCurrency, amount4, tokenPrice4)
        val result5 = AmountConverter.getTokenAmountFromFiatString(cryptoCurrency, amount5, tokenPrice5)

        print("result0 = \"$result0\"\n")
        print("result1 = \"$result1\"\n")
        print("result2 = \"$result2\"\n")
        print("result3 = \"$result3\"\n")
        print("result4 = \"$result4\"\n")
        print("result5 = \"$result5\"\n")

        // Then
        assertEquals("Passes", expectedTokenAmount0, result0)
        assertEquals("Passes", expectedTokenAmount1, result1)
        assertEquals("Passes", expectedTokenAmount2, result2)
        assertEquals("Passes", expectedTokenAmount3, result3)
        assertEquals("Passes", expectedTokenAmount4, result4)
        assertEquals("Passes", expectedTokenAmount5, result5)
    }


    @Test
    fun `test get token amount in BigDecimal from fiat amount String ETH`() {
        val cryptoCurrency = CryptoCurrency.Ethereum
        val tokenScale = 18

        // Given
        val amount0 = ""
        val tokenPrice0 = BigDecimal(1861.28)
        val expectedTokenAmount0 = BigDecimal.ZERO.setScale(tokenScale, RoundingMode.HALF_UP)

        // Given
        val amount1 = "0"
        val tokenPrice1 = BigDecimal(1861.28)
        val expectedTokenAmount1 = BigDecimal.ZERO.setScale(tokenScale, RoundingMode.HALF_UP)

        val amount2 = "5"
        val tokenPrice2 = BigDecimal(1861.28)
        val expectedTokenAmount2 = BigDecimal(0.002686323390355024).setScale(tokenScale, RoundingMode.HALF_UP)

        val amount3 = "20.50"
        val tokenPrice3 = BigDecimal(1861.28)
        val expectedTokenAmount3 = BigDecimal(0.0110139259004556).setScale(tokenScale, RoundingMode.HALF_UP)

        val amount4 = "100"
        val tokenPrice4 = BigDecimal(1861.28)
        val expectedTokenAmount4 = BigDecimal(0.053726467807100489).setScale(tokenScale, RoundingMode.HALF_UP)

        // When
        val result0 = AmountConverter.getTokenAmountFromFiatString(cryptoCurrency, amount0, tokenPrice0)
        val result1 = AmountConverter.getTokenAmountFromFiatString(cryptoCurrency, amount1, tokenPrice1)
        val result2 = AmountConverter.getTokenAmountFromFiatString(cryptoCurrency, amount2, tokenPrice2)
        val result3 = AmountConverter.getTokenAmountFromFiatString(cryptoCurrency, amount3, tokenPrice3)
        val result4 = AmountConverter.getTokenAmountFromFiatString(cryptoCurrency, amount4, tokenPrice4)

        print("result0 = \"$result0\"\n")
        print("result1 = \"$result1\"\n")
        print("result2 = \"$result2\"\n")
        print("result3 = \"$result3\"\n")
        print("result4 = \"$result4\"\n")

        // Then
        assertEquals("Passes", expectedTokenAmount0, result0)
        assertEquals("Passes", expectedTokenAmount1, result1)
        assertEquals("Passes", expectedTokenAmount2, result2)
        assertEquals("Passes", expectedTokenAmount3, result3)
        assertEquals("Passes", expectedTokenAmount4, result4)
    }

    @Test
    fun `test convert token amount to Long`() {
        val cryptoCurrency = CryptoCurrency.Bitcoin

        // Given
        val amount0 = BigDecimal(0.0)
        val expectedResult0 = 0L

        val amount1 = BigDecimal(0.10000000)
        val expectedResult1 = 10000000L

        val amount2 = BigDecimal(14)
        val expectedResult2 = 1400000000L

        val amount3 = BigDecimal(0.00345600)
        val expectedResult3 = 345600L

        val amount4 = BigDecimal(0.0).setScale(8, RoundingMode.HALF_UP)
        val expectedResult4 = 0L

        // When
        val result0 = AmountConverter.tokenAmountToLong(cryptoCurrency, amount0)
        val result1 = AmountConverter.tokenAmountToLong(cryptoCurrency, amount1)
        val result2 = AmountConverter.tokenAmountToLong(cryptoCurrency, amount2)
        val result3 = AmountConverter.tokenAmountToLong(cryptoCurrency, amount3)
        val result4 = AmountConverter.tokenAmountToLong(cryptoCurrency, amount4)

        print("result0 = \"$result0\"\n")
        print("result1 = \"$result1\"\n")
        print("result2 = \"$result2\"\n")
        print("result3 = \"$result3\"\n")
        print("result4 = \"$result4\"\n")

        // Then
        assertEquals("Passes", expectedResult0, result0)
        assertEquals("Passes", expectedResult1, result1)
        assertEquals("Passes", expectedResult2, result2)
        assertEquals("Passes", expectedResult3, result3)
        assertEquals("Passes", expectedResult4, result4)
    }

    @Test
    fun `test parse token amount from string`() {
        // Given
        val cryptoCurrency0 = CryptoCurrency.Bitcoin
        val amount0 = "33001"
        val expectedResult0 = BigDecimal(0.00033001).setScale(8, RoundingMode.HALF_UP)

        val cryptoCurrency1 = CryptoCurrency.Ethereum
        val amount1 = "330019900000000"
        val expectedResult1 = BigDecimal(0.000330019900000000).setScale(18, RoundingMode.HALF_UP)

        val cryptoCurrency2 = CryptoCurrency.Bitcoin
        val amount2 = "511111111"
        val expectedResult2 = BigDecimal(5.11111111).setScale(8, RoundingMode.HALF_UP)

        // When
        val result0 = AmountConverter.parseTokenAmount(cryptoCurrency0, amount0)
        val result1 = AmountConverter.parseTokenAmount(cryptoCurrency1, amount1)
        val result2 = AmountConverter.parseTokenAmount(cryptoCurrency2, amount2)

        print("result0 = \"$result0\"\n")
        print("result1 = \"$result1\"\n")
        print("result2 = \"$result2\"\n")

        // Then
        assertEquals("Passes", expectedResult0, result0)
        assertEquals("Passes", expectedResult1, result1)
        assertEquals("Passes", expectedResult2, result2)
    }

    @Test
    fun `test format to dollar`() {
        // Given
        val amount0 = BigInteger("5141767")

        // When
        val result0 = amount0.formatToDollar(false)

        print("result0 = \"$result0\"\n")

        // Then
        assertEquals("Passes", "51,417.67", result0)
    }
}