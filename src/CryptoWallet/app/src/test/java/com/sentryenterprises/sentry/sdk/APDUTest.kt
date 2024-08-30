package com.sentryenterprises.sentry.sdk

class APDUTest {
    @Test
    fun testAPDU() {
        assertEquals(
            "80 20 00 80 08 24 12 34 FF FF FF FF FF",
            APDUCommand.Companion.verifyEnrollCode(byteArrayOf(0x01, 0x02, 0x03, 0x04))
                .formatted()
        )
        assertEquals(
            "80 E2 08 00 0B 90 00 08 25 12 34 5F FF FF FF FF",
            APDUCommand.Companion.setEnrollCode(byteArrayOf(1, 2, 3, 4, 5))
                .formatted()
        )
    }
}