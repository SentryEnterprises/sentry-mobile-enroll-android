package com.secure.jnet.wallet.data.crypto.bitcoin

import org.junit.Assert
import org.junit.Test

class TransactionSizeCalculatorTest {

    @Test
    fun `calculate transaction fee with 0 input and 2 outputs`() {
        // Given
        val inputCount = 0
        val outputCount = 2

        // When
        val fee = TransactionSizeCalculator().calculateFee(inputCount, outputCount)

        // Then
        val expectedResult = 74
        Assert.assertTrue(expectedResult == fee)
    }

    @Test
    fun `calculate transaction fee with 1 input and 1 output`() {
        // Given
        val inputCount = 1
        val outputCount = 1

        // When
        val fee = TransactionSizeCalculator().calculateFee(inputCount, outputCount)

        // Then
        val expectedResult = 111
        Assert.assertTrue(expectedResult == fee)
    }

    @Test
    fun `calculate transaction fee with 1 input and 2 outputs`() {
        // Given
        val inputCount = 1
        val outputCount = 2

        // When
        val fee = TransactionSizeCalculator().calculateFee(inputCount, outputCount)

        // Then
        val expectedResult = 143
        Assert.assertTrue(expectedResult == fee)
    }

    @Test
    fun `calculate transaction fee with 3 inputs and 2 outputs`() {
        // Given
        val inputCount = 3
        val outputCount = 2

        // When
        val fee = TransactionSizeCalculator().calculateFee(inputCount, outputCount)

        // Then
        val expectedResult = 281
        Assert.assertTrue(expectedResult == fee)
    }
}