package com.secure.jnet.wallet.data.crypto.ethereum

import org.junit.Assert
import org.junit.Before
import org.junit.Test

class EthereumTransactionBuilderTest {

    private lateinit var ethereumTransactionBuilder: EthereumTransactionBuilder

    @Before
    fun setUp() {
        ethereumTransactionBuilder = EthereumTransactionBuilder()
    }

    @Test
    fun `calculate fee test`() {
        // Given
        val amount = 26000L

        // When
        val result = ethereumTransactionBuilder.calculateFee(3999912026L)

        // Then
        val expectedResult = 83998152546000L
        Assert.assertEquals("Passes", expectedResult, result)
    }
}