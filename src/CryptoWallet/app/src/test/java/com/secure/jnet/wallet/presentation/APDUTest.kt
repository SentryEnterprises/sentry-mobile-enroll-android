package com.secure.jnet.wallet.presentation

import com.secure.jnet.jcwkit.utils.toHexString
import com.secure.jnet.wallet.presentation.APDUCommand
import kotlin.test.Test
import kotlin.test.assertEquals

class APDUCommandTest {
    @Test
    fun testAPDU() {
        assertEquals(
            "80 20 00 80 08 24 12 34 FF FF FF FF FF".replace(" ",""),
            APDUCommand.verifyEnrollCode(byteArrayOf(0x01, 0x02, 0x03, 0x04))
                .toHexString()
        )
        assertEquals(
            "80 E2 08 00 0B 90 00 08 25 12 34 5F FF FF FF FF".replace(" ",""),
            APDUCommand.setEnrollCode(byteArrayOf(0x01, 0x02, 0x03, 0x04, 0x05))
                .toHexString()
        )
    }
}
