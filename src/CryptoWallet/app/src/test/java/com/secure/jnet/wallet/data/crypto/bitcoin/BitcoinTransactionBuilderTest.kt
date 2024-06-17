package com.secure.jnet.wallet.data.crypto.bitcoin

import com.secure.jnet.jcwkit.JCWKitUtils
import com.secure.jnet.wallet.data.crypto.models.BitcoinRawTransactionDTO
import com.secure.jnet.wallet.data.crypto.models.RawTransactionInputDTO
import com.secure.jnet.wallet.data.crypto.models.RawTransactionOutputDTO
import com.secure.jnet.wallet.domain.models.enums.CryptoCurrency
import com.secure.jnet.wallet.domain.models.remote.BitcoinFeeEntity
import com.secure.jnet.wallet.domain.models.remote.UtxoEntity
import com.secure.jnet.wallet.presentation.mappers.RawTransactionUIModelMapper
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.math.BigInteger
import java.util.Locale

@Suppress("SpellCheckingInspection")
class BitcoinTransactionBuilderTest {

    @MockK
    lateinit var jcwKitUtils: JCWKitUtils

    private val transactionSizeCalculator = TransactionSizeCalculator()
    private val transactionInputsSelector = TransactionInputsSelector(transactionSizeCalculator)
    private lateinit var bitcoinTransactionBuilder: BitcoinTransactionBuilder
    private val rawTransactionUIModelMapper = RawTransactionUIModelMapper()

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        bitcoinTransactionBuilder = BitcoinTransactionBuilder(
            jcwKitUtils,
            transactionSizeCalculator,
            transactionInputsSelector
        )
    }

    @Test
    fun `build transaction input`() {
        // Given
        val amount = 26000L
        val script = "001471179fa63fc9f2396c53967862cb302caea3ac78"
        val index = 1
        val transactionHash = "0e8d5067446000db09850bfff81ae3bfe73aaf3b4b3c3d8949dfabc2680095c1"

        val input = RawTransactionInputDTO(
            amount = amount,
            script = script,
            index = index,
            transactionHash = transactionHash,
        )

        // When
        val result = input.build()

        // Then
        val expectedResult = "0E8D5067446000DB09850BFFF81AE3BFE73AAF3B4B3C3D8949DFABC2680095C10100000016001471179FA63FC9F2396C53967862CB302CAEA3AC78FFFFFFFF9065000000000000"
        Assert.assertTrue("Passes", result.equals(expectedResult, true))
    }

    @Test(expected = IllegalStateException::class)
    fun `build transaction input script is empty`() {
        // Given
        val amount = 26000L
        val script = ""
        val index = 1
        val transactionHash = "0e8d5067446000db09850bfff81ae3bfe73aaf3b4b3c3d8949dfabc2680095c1"

        val input = RawTransactionInputDTO(
            amount = amount,
            script = script,
            index = index,
            transactionHash = transactionHash,
        )

        // When
        input.build()

        // Then
    }

    @Test(expected = IllegalStateException::class)
    fun `build transaction input tx hash is empty`() {
        // Given
        val amount = 26000L
        val script = "001471179fa63fc9f2396c53967862cb302caea3ac78"
        val index = 1
        val transactionHash = ""

        val input = RawTransactionInputDTO(
            amount = amount,
            script = script,
            index = index,
            transactionHash = transactionHash,
        )

        // When
        input.build()

        // Then
        // Exception
    }

    @Test
    fun `build transaction output`() {
        // Given
        val amount = 3333L
        val script = "001471179fa63fc9f2396c53967862cb302caea3ac78"

        val input = RawTransactionOutputDTO(
            amount = amount,
            script = script,
        )

        // When
        val result = input.build().uppercase(Locale.getDefault())

        // Then
        val expectedResult = "050D00000000000016001471179FA63FC9F2396C53967862CB302CAEA3AC78"
        Assert.assertTrue("Passes", result.equals(expectedResult, true))
    }

    @Test(expected = IllegalStateException::class)
    fun `build transaction output script is empty`() {
        // Given
        val amount = 3333L
        val script = ""

        val input = RawTransactionOutputDTO(
            amount = amount,
            script = script,
        )

        // When
        input.build().uppercase(Locale.getDefault())

        // Then
    }

    @Test(expected = IllegalStateException::class)
    fun `build transaction output amount is zero`() {
        // Given
        val amount = 0L
        val script = "001471179fa63fc9f2396c53967862cb302caea3ac78"

        val input = RawTransactionOutputDTO(
            amount = amount,
            script = script,
        )

        // When
        input.build().uppercase(Locale.getDefault())

        // Then
    }

    @Test
    fun `test build transaction`() {
        // Given
        val amount = 3000L
        val addressTo = "tb1qk2upgce9nmr4hzg5tt940u38mhv28xqaswuy59"
        val scriptTo = "0014B2B81463259EC75B89145ACB57F227DDD8A3981D"
        val addressFrom = "tb1qwytelf3le8erjmznjeux9jes9jh28trc7lddn9"
        val scriptFrom = "001471179FA63FC9F2396C53967862CB302CAEA3AC78"
        val feeRate = BitcoinFeeEntity(23)

        every { jcwKitUtils.addressToScript(addressTo) } returns scriptTo
        every { jcwKitUtils.addressToScript(addressFrom) } returns scriptFrom

        val utxo = mutableListOf<UtxoEntity>()

        utxo.add(
            UtxoEntity(
                addressFrom,
                70000L,
                1,
                scriptFrom,
                "witness_v0_keyhash",
                "78B9259121F7340AB147E8163FC2EEE959C8E0AAD5EC4C9F4F4517B147C9680D"
            )
        )

        // When
        val transaction = bitcoinTransactionBuilder.buildTransaction(
            amount = amount,
            addressTo = addressTo,
            addressFrom = addressFrom,
            utxo = utxo,
            feeRate = feeRate,
            maxAmount = false,
        )

        // Then
        val expectedFee = 3289L
        val inputsToSign = "0178B9259121F7340AB147E8163FC2EEE959C8E0AAD5EC4C9F4F4517B147C9680D0100000016001471179FA63FC9F2396C53967862CB302CAEA3AC78FFFFFFFF7011010000000000"
        val outputsToSign = "02B80B000000000000160014B2B81463259EC75B89145ACB57F227DDD8A3981DDFF800000000000016001471179FA63FC9F2396C53967862CB302CAEA3AC78"

        Assert.assertEquals("Result:", expectedFee, transaction.fee)
        Assert.assertEquals("Result:", inputsToSign, transaction.inputsToSign)
        Assert.assertEquals("Result:", 1, transaction.inputsCount)
        Assert.assertEquals("Result:", outputsToSign, transaction.outputsToSign)
        Assert.assertEquals("Result:", 2, transaction.outputsCount)
    }

    @Test
    fun `test transaction mapping`() {
        // Given
        val cryptoCurrency = CryptoCurrency.Bitcoin
        val tokenPrice = BigInteger("5149370")
        val amount = 1000000L
        val fee = 1430L
        val addressTo = "tb1qk2upgce9nmr4hzg5tt940u38mhv28xqaswuy59"
        val addressFrom = "tb1qwytelf3le8erjmznjeux9jes9jh28trc7lddn9"

        // When
        val tx = BitcoinRawTransactionDTO(
            amount,
            fee,
            addressTo,
            "",
            1,
            "",
            2,
        )

        val mappedTx = rawTransactionUIModelMapper.mapToUIModel(
            tx,
            cryptoCurrency,
            tokenPrice
        )

        // Then
        Assert.assertEquals("Result:", addressTo, mappedTx.addressTo)
        Assert.assertEquals("Result:", "0.01 BTC", mappedTx.amountToken)
        Assert.assertEquals("Result:", "$514.94", mappedTx.amountFiat)
        Assert.assertEquals("Result:", "0.0000143 BTC", mappedTx.feeToken)
        Assert.assertEquals("Result:", "$0.74", mappedTx.feeFiat)
        Assert.assertEquals("Result:", "0.0100143 BTC", mappedTx.totalAmountToken)
        Assert.assertEquals("Result:", "$515.68", mappedTx.totalAmountFiat)
    }
}