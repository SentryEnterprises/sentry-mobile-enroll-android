package com.secure.jnet.wallet.domain.interactor

import com.secure.jnet.jcwkit.JCWKitUtils
import com.secure.jnet.wallet.data.CryptoWallet
import com.secure.jnet.wallet.data.crypto.bitcoin.BitcoinTransactionBuilder
import com.secure.jnet.wallet.data.crypto.bitcoin.TransactionInputsSelector
import com.secure.jnet.wallet.data.crypto.bitcoin.TransactionSizeCalculator
import com.secure.jnet.wallet.data.crypto.ethereum.EthereumTransactionBuilder
import com.secure.jnet.wallet.data.crypto.models.BitcoinRawTransactionDTO
import com.secure.jnet.wallet.domain.models.enums.CryptoCurrency
import com.secure.jnet.wallet.domain.models.remote.BitcoinFeeEntity
import com.secure.jnet.wallet.domain.models.remote.EthereumFeeEntity
import com.secure.jnet.wallet.domain.models.remote.UtxoEntity
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.math.BigInteger

class WalletInteractorTest {

    @MockK
    lateinit var cryptoWallet: CryptoWallet

    @MockK
    lateinit var jcwKitUtils: JCWKitUtils

    private val transactionSizeCalculator = TransactionSizeCalculator()
    private val transactionInputsSelector = TransactionInputsSelector(transactionSizeCalculator)
    private lateinit var bitcoinTransactionBuilder: BitcoinTransactionBuilder

    private val ethereumTransactionBuilder = EthereumTransactionBuilder()

    private lateinit var walletInteractor: WalletInteractor

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)

        bitcoinTransactionBuilder = BitcoinTransactionBuilder(
            jcwKitUtils,
            transactionSizeCalculator,
            transactionInputsSelector
        )

        walletInteractor = WalletInteractor(
            cryptoWallet,
            bitcoinTransactionBuilder,
            ethereumTransactionBuilder
        )
    }

    @Test
    fun `calculate eth fee test`() {
        // Given
        val cryptoCurrency = CryptoCurrency.Ethereum
        val amount = BigInteger("1000")
        val feeRate = EthereumFeeEntity(
            maxPriorityFeePerGas = 2000000000L,
            maxFeePerGas = 3999912026L
        )

        // When
        val result = walletInteractor.calculateFee(
            cryptoCurrency = cryptoCurrency,
            tokenAmount = amount,
            feeRate = feeRate,
            utxo = null,
            maxAmount = false
        )

        // Then
        val expectedResult = BigInteger("83998152546000")
        Assert.assertEquals("Passes", expectedResult, result)
    }

    @Test
    fun `calculate eth fee max amount`() {
        // Given
        val cryptoCurrency = CryptoCurrency.Ethereum
        val amount = BigInteger("1000")
        val feeRate = EthereumFeeEntity(
            maxPriorityFeePerGas = 2000000000L,
            maxFeePerGas = 3999912026L
        )

        // When
        val result = walletInteractor.calculateFee(
            cryptoCurrency = cryptoCurrency,
            tokenAmount = amount,
            feeRate = feeRate,
            utxo = null,
            maxAmount = true
        )

        // Then
        val expectedResult = BigInteger("83998152546000")
        Assert.assertEquals("Passes", expectedResult, result)
    }

    @Test
    fun `calculate btc fee`() {
        // Given
        val cryptoCurrency = CryptoCurrency.Bitcoin
        val amount = BigInteger("1000")
        val feeRate = BitcoinFeeEntity(23)
        val utxo = listOf(
            UtxoEntity("address1", 23000L, 1, "", "", "")
        )

        // When
        val result = walletInteractor.calculateFee(
            cryptoCurrency = cryptoCurrency,
            tokenAmount = amount,
            feeRate = feeRate,
            utxo = utxo,
            maxAmount = false
        )

        // Then
        val expectedResult = BigInteger("3289")
        Assert.assertEquals("Passes", expectedResult, result)
    }

    @Test
    fun `calculate btc fee max amount`() {
        // Given
        val cryptoCurrency = CryptoCurrency.Bitcoin
        val amount = BigInteger("1000")
        val feeRate = BitcoinFeeEntity(23)
        val utxo = listOf(
            UtxoEntity("address1", 23000L, 1, "", "", "")
        )

        // When
        val result = walletInteractor.calculateFee(
            cryptoCurrency = cryptoCurrency,
            tokenAmount = amount,
            feeRate = feeRate,
            utxo = utxo,
            maxAmount = true
        )

        // Then
        val expectedResult = BigInteger("2553")
        Assert.assertEquals("Passes", expectedResult, result)
    }

    @Test
    fun `calculate btc fee max amount 5 inputs`() {
        // Given
        val cryptoCurrency = CryptoCurrency.Bitcoin
        val amount = BigInteger("1000")
        val feeRate = BitcoinFeeEntity(23)
        val utxo = listOf(
            UtxoEntity("address1", 23000L, 1, "", "", ""),
            UtxoEntity("address2", 4000L, 1, "", "", ""),
            UtxoEntity("address3", 50000L, 0, "", "", ""),
            UtxoEntity("address4", 6000L, 0, "", "", ""),
            UtxoEntity("address5", 7000L, 1, "", "", ""),
        )

        // When
        val result = walletInteractor.calculateFee(
            cryptoCurrency = cryptoCurrency,
            tokenAmount = amount,
            feeRate = feeRate,
            utxo = utxo,
            maxAmount = true
        )

        // Then
        val expectedResult = BigInteger("8901")
        Assert.assertEquals("Passes", expectedResult, result)
    }

    @Test
    fun `build btc transaction max amount`() {
        // Given
        val cryptoCurrency = CryptoCurrency.Bitcoin
        val amount = BigInteger("1000")
        val feeRate = BitcoinFeeEntity(23)
        val addressTo = "0014B2B81463259EC75B89145ACB57F227DDD8A3981D"
        val utxo = listOf(
            UtxoEntity("address11", 23000L, 1, "D8", "", "txId"),
            UtxoEntity("address22", 12000L, 0, "D8", "", "txId"),
            UtxoEntity("address33", 500L, 1, "D8", "", "txId")
        )

        every { jcwKitUtils.addressToScript(any()) } returns addressTo

        every { cryptoWallet.getAddress(cryptoCurrency) } returns "myAddress"

        // When
        val result = walletInteractor.buildTransaction(
            cryptoCurrency = cryptoCurrency,
            utxo = utxo,
            tokenAmount = amount,
            address = addressTo,
            fee = feeRate,
            nonce = null,
            maxAmount = true,
        )

        // Then
        val expectedResult = BitcoinRawTransactionDTO(
            amount = 1000,
            fee = 2553L,
            addressTo = addressTo,
            inputsToSign = "01TXID0100000001D8FFFFFFFFD859000000000000",
            inputsCount = 3,
            outputsToSign = "02E803000000000000160014B2B81463259EC75B89145ACB57F227DDD8A3981D1749000000000000160014B2B81463259EC75B89145ACB57F227DDD8A3981D",
            outputsCount = 1,
        )
        Assert.assertEquals("Passes", expectedResult, result)
    }
}