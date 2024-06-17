package com.secure.jnet.wallet.data.crypto.bitcoin

import com.secure.jnet.wallet.domain.models.remote.UtxoEntity
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@Suppress("SpellCheckingInspection")
class TransactionInputsSelectorTest {

    private lateinit var transactionSizeCalculator: TransactionSizeCalculator
    private lateinit var transactionInputsSelector: TransactionInputsSelector

    @Before
    fun setUp() {
        transactionSizeCalculator = TransactionSizeCalculator()
        transactionInputsSelector = TransactionInputsSelector(transactionSizeCalculator)
    }

    @Test
    fun `test inputs selection 0`() {
        // Given
        val utxo = mutableListOf<UtxoEntity>()
        val amount = 3000L
        val feeRate = 23L

        // Expected fee = 3289
        // Expected total amount = 6289

        utxo.add(
            UtxoEntity(
                "address1",
                6000L,
                1,
                "script1",
                "scriptType1",
                "txId1"
            )
        )

        utxo.add(
            UtxoEntity(
                "address1",
                300L,
                1,
                "script1",
                "scriptType1",
                "txId1"
            )
        )

        utxo.add(
            UtxoEntity(
                "address1",
                490L,
                1,
                "script1",
                "scriptType1",
                "txId1"
            )
        )

        // When
        val selectedInputs = transactionInputsSelector.getInputs(
            utxo, 2, amount, feeRate
        )

        // Then
        Assert.assertEquals("Result:", 0, selectedInputs.size)
    }

    @Test
    fun `test inputs selection 1`() {
        // Given
        val utxo = mutableListOf<UtxoEntity>()
        val amount = 3000L
        val feeRate = 23L

        // Expected fee = 3289
        // Expected total amount = 6289

        utxo.add(
            UtxoEntity(
                "address1",
                6000L,
                1,
                "script1",
                "scriptType1",
                "txId1"
            )
        )

        utxo.add(
            UtxoEntity(
                "address1",
                44000L,
                1,
                "script1",
                "scriptType1",
                "txId1"
            )
        )

        utxo.add(
            UtxoEntity(
                "address1",
                490L,
                1,
                "script1",
                "scriptType1",
                "txId1"
            )
        )

        // When
        val selectedInputs = transactionInputsSelector.getInputs(
            utxo, 2, amount, feeRate
        )

        // Then
        val expectedInputs = mutableListOf<UtxoEntity>()

        expectedInputs.add(
            UtxoEntity(
                "address1",
                44000L,
                1,
                "script1",
                "scriptType1",
                "txId1"
            )
        )

        Assert.assertEquals("Result:", expectedInputs.size, selectedInputs.size)
        Assert.assertEquals("Result:", expectedInputs[0], selectedInputs[0])
    }

    @Test
    fun `test inputs selection 2`() {
        // Given
        val utxo = mutableListOf<UtxoEntity>()
        val amount = 3000L
        val feeRate = 23L

        // Expected fee = 3289
        // Expected total amount = 6289

        utxo.add(
            UtxoEntity(
                "address1",
                6000L,
                1,
                "script1",
                "scriptType1",
                "txId1"
            )
        )

        utxo.add(
            UtxoEntity(
                "address1",
                490L,
                1,
                "script1",
                "scriptType1",
                "txId1"
            )
        )

        utxo.add(
            UtxoEntity(
                "address1",
                4000L,
                1,
                "script1",
                "scriptType1",
                "txId1"
            )
        )

        // When
        val selectedInputs = transactionInputsSelector.getInputs(
            utxo, 2, amount, feeRate
        )

        // Then
        val expectedInputs = mutableListOf<UtxoEntity>()

        expectedInputs.add(
            UtxoEntity(
                "address1",
                6000L,
                1,
                "script1",
                "scriptType1",
                "txId1"
            )
        )

        expectedInputs.add(
            UtxoEntity(
                "address1",
                4000L,
                1,
                "script1",
                "scriptType1",
                "txId1"
            )
        )

        Assert.assertEquals("Result:", expectedInputs.size, selectedInputs.size)
        Assert.assertEquals("Result:", expectedInputs[0], selectedInputs[0])
        Assert.assertEquals("Result:", expectedInputs[1], selectedInputs[1])
    }
}