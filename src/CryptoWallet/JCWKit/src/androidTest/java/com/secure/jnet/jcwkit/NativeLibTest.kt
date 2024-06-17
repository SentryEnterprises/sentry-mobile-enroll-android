package com.secure.jnet.jcwkit

import org.junit.Assert
import org.junit.Test

class NativeLibTest {

    @Test
    fun libSdkInit() {
        // Given
        // select wallet response
        val response = byteArrayOf(0x6F, 0x12, 0x84.toByte(), 0x0A, 0x4C, 0x6F, 0x6B, 0x61, 0x57, 0x61, 0x6C, 0x6C, 0x65, 0x74, 0xA5.toByte(), 0x04, 0x9F.toByte(), 0x65, 0x01, 0xFF.toByte(), 0x90.toByte(), 0x00)

        val callBack = SmartCardApduCallback { dataIn, dataInLen, dataOut, dataOutLen ->
            dataOut!!.write(0, response, 0, response.size)
            dataOutLen!!.setInt(0, response.size)
            0
        }

        // When
        val result = NativeLib.INSTANCE.LibSdkWalletInit(0, callBack)

        // Then
        Assert.assertEquals(0, result)
    }
}