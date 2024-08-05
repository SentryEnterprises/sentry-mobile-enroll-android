package com.secure.jnet.wallet.presentation

import com.secure.jnet.jcwkit.utils.formatted
import kotlin.test.Test
import kotlin.test.assertEquals

class APDUCommandTest {
    @Test
    fun testAPDU() {
        assertEquals(
            "80 20 00 80 08 24 12 34 FF FF FF FF FF",
            APDUCommand.verifyEnrollCode(byteArrayOf(0x01, 0x02, 0x03, 0x04))
                .formatted()
        )
        assertEquals(
            "80 E2 08 00 0B 90 00 08 25 12 34 5F FF FF FF FF",
            APDUCommand.setEnrollCode(byteArrayOf(1, 2, 3, 4, 5))
                .formatted()
        )
    }
}
