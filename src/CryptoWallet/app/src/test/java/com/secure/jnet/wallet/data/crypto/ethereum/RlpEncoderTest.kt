package com.secure.jnet.wallet.data.crypto.ethereum

import com.secure.jnet.jcwkit.utils.toHexString
import com.secure.jnet.wallet.data.crypto.ethereum.RlpEncoder.encode
import com.secure.jnet.wallet.data.crypto.ethereum.RlpEncoder.encodeAddress
import org.junit.Assert
import org.junit.Test
import java.math.BigInteger

class RlpEncoderTest {

    @Test
    fun `int test`() {
        // Given
        val input0 = 3869758746
        val input1 = 2000000000
        val input2 = 21000
        val input3 = 3

        // When
        val result0 = encode(input0)
        val result1 = encode(input1)
        val result2 = encode(input2)
        val result3 = encode(input3)

        // Then
        val expectedResult0 = "84E6A7D51A"
        val expectedResult1 = "8477359400"
        val expectedResult2 = "825208"
        val expectedResult3 = "03"

        assert(result0.toHexString() == expectedResult0)
        assert(result1.toHexString() == expectedResult1)
        assert(result2.toHexString() == expectedResult2)
        assert(result3.toHexString() == expectedResult3)
    }

    @Test
    fun `long test`() {
        // Given
        val input0 = 3869758746L
        val input1 = 2000000000L
        val input2 = 21000L
        val input3 = 3L

        // When
        val result0 = encode(input0)
        val result1 = encode(input1)
        val result2 = encode(input2)
        val result3 = encode(input3)

        // Then
        val expectedResult0 = "84E6A7D51A"
        val expectedResult1 = "8477359400"
        val expectedResult2 = "825208"
        val expectedResult3 = "03"

        assert(result0.toHexString() == expectedResult0)
        assert(result1.toHexString() == expectedResult1)
        assert(result2.toHexString() == expectedResult2)
        assert(result3.toHexString() == expectedResult3)
    }

    @Test
    fun `big integer test`() {
        // Given
        val input0 = BigInteger("3869758746")
        val input1 = BigInteger("2000000000")
        val input2 = BigInteger("21000")
        val input3 = BigInteger("3")
        val input4 = BigInteger("10000000000000000")

        // When
        val result0 = encode(input0)
        val result1 = encode(input1)
        val result2 = encode(input2)
        val result3 = encode(input3)
        val result4 = encode(input4)

        // Then
        val expectedResult0 = "84E6A7D51A"
        val expectedResult1 = "8477359400"
        val expectedResult2 = "825208"
        val expectedResult3 = "03"
        val expectedResult4 = "872386F26FC10000"

        assert(result0.toHexString() == expectedResult0)
        assert(result1.toHexString() == expectedResult1)
        assert(result2.toHexString() == expectedResult2)
        assert(result3.toHexString() == expectedResult3)
        assert(result4.toHexString() == expectedResult4)
    }

    @Test
    fun `string test`() {
        // Given
        val input0 = "0xc792f60D4C912b46e1EbF160dD09D25C567FD6Ed"

        // When
        val result0 = encodeAddress(input0)

        // Then
        val expectedResult0 = "94C792F60D4C912B46E1EBF160DD09D25C567FD6ED"

        Assert.assertEquals(expectedResult0, result0.toHexString())
    }

    @Test
    fun `integer test`() {
        val integerInput = byteArrayOf(4,0) //1024
        assert(encode(integerInput).toHexString() == "820400")
    }

    @Test
    fun `from 0x00 to 0x7fb test`() {
        val smallInput = "d".toByteArray()
        assert(encode(smallInput).toHexString() == "64")
    }

    @Test
    fun `from 0 to 55 bytes long test`() {
        val mediumInput = "RLP-Kotlin".toByteArray()
        val mediumInputHexString = encode(mediumInput).toHexString()
        assert(mediumInputHexString == "8A524C502D4B6F746C696E")
    }

    @Test
    fun `more than 55 bytes long test`() {
        val longInput = "RLP-Kotlin is an RLP encoder and decoder written in Kotlin for educational purposes".toByteArray()
        assert(encode(longInput).toHexString() == "B853524C502D4B6F746C696E20697320616E20524C5020656E636F64657220616E64206465636F646572207772697474656E20696E204B6F746C696E20666F7220656475636174696F6E616C20707572706F736573")
    }

    @Test
    fun `less than 55 bytes long list test`() {
        val shortListInput = listOf(encode("cat".toByteArray()), encode("dog".toByteArray()))
        assert(encode(shortListInput).toHexString() == "C88363617483646F67")
    }

    @Test
    fun `less than 55 bytes long nested list test`() {
        val nestedShortByteArray = listOf( // [ [], [[]], [ [], [[]] ] ]
            encode(listOf(byteArrayOf())),
            encode(listOf(encode(listOf(byteArrayOf())))),
            encode(listOf(encode(listOf(byteArrayOf())), encode(listOf(encode(listOf(byteArrayOf()))))))
        )
        assert(encode(nestedShortByteArray).toHexString() == "C7C0C1C0C3C0C1C0")
    }

    @Test
    fun `empty test`() {
        val emptyInput = byteArrayOf()
        assert(encode(emptyInput).toHexString() == "80")
    }
}