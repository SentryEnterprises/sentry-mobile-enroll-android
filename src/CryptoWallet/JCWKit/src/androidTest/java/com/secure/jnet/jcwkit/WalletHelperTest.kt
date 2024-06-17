package com.secure.jnet.jcwkit

import com.secure.jnet.jcwkit.models.CapabilitiesDTO
import com.secure.jnet.jcwkit.models.GWLCS
import com.secure.jnet.jcwkit.models.WalletVersionDTO
import com.sun.jna.Memory
import com.sun.jna.Pointer
import com.sun.jna.ptr.IntByReference
import org.junit.Assert
import org.junit.Test

class WalletHelperTest {

    @Test
    fun parseWalletVersionResult() {
        // Given
        val expectedResult = WalletVersionDTO("1.26")

        val pointer: Pointer = Memory(4)
        pointer.write(0, byteArrayOf(0x02, 0x01, 0x1A, 0x4E), 0, 4)

        // When
        val result = WalletHelper.parseWalletVersionResult(pointer)

        // Then
        Assert.assertEquals(expectedResult, result)
    }

    @Test
    fun parseCapabilitiesResult() {
        // Given
        val expectedResult = CapabilitiesDTO(
            isBiometricEnabled = true,
            isSecureChannelEnabled = true,
            is1MillionIterationsEnabled = true,
            isStorePin = true,
            isPinDisabled = true
        )

        val pointer: Pointer = Memory(1)
        pointer.write(0, byteArrayOf(0x1F), 0, 1)

        // When
        val result = WalletHelper.parseCapabilitiesResult(pointer)

        // Then
        Assert.assertEquals(expectedResult, result)
    }

    @Test
    fun parseCapabilitiesResultAllFalse() {
        // Given
        val expectedResult = CapabilitiesDTO(
            isBiometricEnabled = false,
            isSecureChannelEnabled = false,
            is1MillionIterationsEnabled = false,
            isStorePin = false,
            isPinDisabled = false
        )

        val pointer: Pointer = Memory(1)
        pointer.write(0, byteArrayOf(0x0), 0, 1)

        // When
        val result = WalletHelper.parseCapabilitiesResult(pointer)

        // Then
        Assert.assertEquals(expectedResult, result)
    }

    @Test
    fun parseCapabilitiesResult_Only_Bio() {
        // Given
        val expectedResult = CapabilitiesDTO(
            isBiometricEnabled = true,
            isSecureChannelEnabled = false,
            is1MillionIterationsEnabled = false,
            isStorePin = false,
            isPinDisabled = false
        )

        val pointer: Pointer = Memory(1)
        pointer.write(0, byteArrayOf(0x1), 0, 1)

        // When
        val result = WalletHelper.parseCapabilitiesResult(pointer)

        // Then
        Assert.assertEquals(expectedResult, result)
    }

    @Test
    fun parseCreateWalletResult() {
        // Given
        val expectedMnemonicWords24 = "baby mass dust captain baby mass dust captain baby mass dust captain baby mass dust captain baby mass dust captain baby mass dust cake"

        val length = 134
        val mnemonicLength = IntByReference(length)
        val mnemonic: Pointer = Memory(1024)
        mnemonic.write(0, byteArrayOf(0x62, 0x61, 0x62, 0x79, 0x20, 0x6D, 0x61, 0x73, 0x73, 0x20, 0x64, 0x75, 0x73, 0x74, 0x20, 0x63, 0x61, 0x70, 0x74, 0x61, 0x69, 0x6E, 0x20, 0x62, 0x61, 0x62, 0x79, 0x20, 0x6D, 0x61, 0x73, 0x73, 0x20, 0x64, 0x75, 0x73, 0x74, 0x20, 0x63, 0x61, 0x70, 0x74, 0x61, 0x69, 0x6E, 0x20, 0x62, 0x61, 0x62, 0x79, 0x20, 0x6D, 0x61, 0x73, 0x73, 0x20, 0x64, 0x75, 0x73, 0x74, 0x20, 0x63, 0x61, 0x70, 0x74, 0x61, 0x69, 0x6E, 0x20, 0x62, 0x61, 0x62, 0x79, 0x20, 0x6D, 0x61, 0x73, 0x73, 0x20, 0x64, 0x75, 0x73, 0x74, 0x20, 0x63, 0x61, 0x70, 0x74, 0x61, 0x69, 0x6E, 0x20, 0x62, 0x61, 0x62, 0x79, 0x20, 0x6D, 0x61, 0x73, 0x73, 0x20, 0x64, 0x75, 0x73, 0x74, 0x20, 0x63, 0x61, 0x70, 0x74, 0x61, 0x69, 0x6E, 0x20, 0x62, 0x61, 0x62, 0x79, 0x20, 0x6D, 0x61, 0x73, 0x73, 0x20, 0x64, 0x75, 0x73, 0x74, 0x20, 0x63, 0x61, 0x6B, 0x65), 0, length)

        // When
        val result = WalletHelper.parseCreateWalletResult(mnemonic, mnemonicLength)

        // Then
        Assert.assertEquals(true, result == expectedMnemonicWords24)
    }

    @Test
    fun parseGWLCSResult() {
        // Given
        val expectedResult = GWLCS(
            persoStart = false,
            persoReady = true,
            persoAcc = false,
            persoDone = false,
            selectable = false,
            walletRecovery = true,
            walletCreation = false,
        )

        val pointer: Pointer = Memory(1)
        pointer.write(0, byteArrayOf(0x22), 0, 1)

        // When
        val result = WalletHelper.parseGWLCSResult(pointer)

        // Then
        Assert.assertEquals(expectedResult, result)
    }
}