package com.secure.jnet.jcwkit

import com.secure.jnet.jcwkit.models.AccountDTO
import com.secure.jnet.jcwkit.models.AccountStatus
import com.secure.jnet.jcwkit.models.Bip
import com.secure.jnet.jcwkit.models.CapabilitiesDTO
import com.secure.jnet.jcwkit.models.GWLCS
import com.secure.jnet.jcwkit.models.PublicKeyDTO
import com.secure.jnet.jcwkit.models.ReceivePublicKeyDTO
import com.secure.jnet.jcwkit.models.WPSM
import com.secure.jnet.jcwkit.models.WSSM
import com.secure.jnet.jcwkit.models.WalletVersionDTO
import com.secure.jnet.jcwkit.utils.toHexString
import com.sun.jna.Memory
import com.sun.jna.Pointer
import com.sun.jna.ptr.IntByReference
import kotlin.experimental.and

class WalletHelper {

    /*

    0 bit - 0x01
    1 bit - 0x02
    2 bit - 0x04
    3 bit - 0x08
    4 bit - 0x10
    5 bit - 0x20
    6 bit - 0x40
    7 bit - 0x80

     */

    companion object {
        @Suppress("MoveVariableDeclarationIntoWhen")
        fun parseWalletVersionResult(pointer: Pointer): WalletVersionDTO {
            val versionLength: Int = pointer.getByteArray(0, 1)[0].toInt()

            val walletVersion: String = when (versionLength) {
                2 -> {
                    "${pointer.getByteArray(1, 1)[0].toInt()}" +
                            ".${pointer.getByteArray(2, 1)[0].toInt()}"
                }

                3 -> {
                    "${pointer.getByteArray(1, 1)[0].toInt()}" +
                            ".${pointer.getByteArray(2, 1)[0].toInt()}" +
                            ".${pointer.getByteArray(3, 1)[0].toInt()}"
                }

                else -> {
                    "-1"
                }
            }

            return WalletVersionDTO(walletVersion)
        }

        fun parseCapabilitiesResult(pointer: Pointer): CapabilitiesDTO {
            val capabilities: Byte = pointer.getByte(0)

            return CapabilitiesDTO(
                capabilities and 0x01.toByte() > 0,
                capabilities and 0x02.toByte() > 0,
                capabilities and 0x04.toByte() > 0,
                capabilities and 0x08.toByte() > 0,
                capabilities and 0x010.toByte() > 0,
            )
        }

        fun parseGWLCSResult(pointer: Pointer): GWLCS {
            val gwlcs: Byte = pointer.getByte(0)

            return GWLCS(
                gwlcs and 0x01 > 0,
                gwlcs and 0x02 > 0,
                gwlcs and 0x04 > 0,
                gwlcs.toUByte() and 0x80.toUByte() > 0u,
                gwlcs and 0x10 > 0,
                gwlcs and 0x20 > 0,
                gwlcs and 0x40 > 0
            )
        }

        fun parseWPSMResult(pointer: Pointer): WPSM {
            val byte: Byte = pointer.getByte(0)

            return WPSM(
                byte and 0x08 > 0,
                byte and 0x10 > 0,
                byte and 0x80.toByte() > 0,
            )
        }

        fun parseWSSMResult(pointer: Pointer): WSSM {
            val byte: Byte = pointer.getByte(0)

            return WSSM(
                byte and 0x04 > 0,
                byte and 0x08 > 0,
                byte and 0x10 > 0,
            )
        }

        fun parseCreateWalletResult(mnemonic: Pointer, mnemonicLength: IntByReference): String {
            val mnemonicString = mnemonic.getByteArray(
                0, mnemonicLength.value
            ).joinToString(separator = "") {
                String(byteArrayOf(it), Charsets.UTF_8)
            }

            return mnemonicString
        }

        private const val NETWORK_ID_OFFSET = 4L
        private const val ACCOUNT_ID_OFFSET = 5L
        private const val CHAIN_OFFSET = 6L
        private const val BIP_OFFSET = 7L
        private const val ACCOUNT_STATUS_OFFSET = 8L
        private const val NICKNAME_LENGTH_OFFSET = 9L
        private const val NICKNAME_OFFSET = 10L

        /*
         1-4 byte - CurrencyID
         5 byte - NetworkID
         6 - AccountID
         7 - Chain
         8 - BIP
         9 - Status
         10 - Len Nick (max 16 bytes)
         11-26 - Nickname
         */

        fun parseGetAccountsResult(accountsCountPointer: Pointer, accounts: Pointer): List<AccountDTO> {
            val accountList = mutableListOf<AccountDTO>()

            val accountsCount = accountsCountPointer.getByte(0).toInt()

            var currentOffset = 0

            for (i in 1 .. accountsCount) {

                val byte1 = accounts.getByte((currentOffset + 0).toLong())
                val byte2 = accounts.getByte((currentOffset + 1).toLong())
                val byte3 = accounts.getByte((currentOffset + 2).toLong())
                val byte4 = accounts.getByte((currentOffset + 3).toLong())

                val currencyId = byte4.toInt() shl 24 or
                        byte3.toInt() shl 16 or
                        byte2.toInt() shl 8 or
                        byte1.toInt()

                val networkId = accounts.getByte(currentOffset + NETWORK_ID_OFFSET).toInt()

                val accountId = accounts.getByte(currentOffset + ACCOUNT_ID_OFFSET).toInt()

                val chain = accounts.getByte(currentOffset + CHAIN_OFFSET).toInt()

                val bip = Bip.getByValue(
                    accounts.getByte(currentOffset + BIP_OFFSET).toInt()
                ) ?: Bip.BIP_84

                val accountStatus = AccountStatus.getByValue(
                    accounts.getByte(currentOffset + ACCOUNT_STATUS_OFFSET).toInt()
                ) ?: AccountStatus.ACTIVE_ACC

                val nicknameLength = accounts.getByte(currentOffset + NICKNAME_LENGTH_OFFSET).toLong()
                val nickname = StringBuilder()

                for (n in currentOffset + NICKNAME_OFFSET until (currentOffset + NICKNAME_OFFSET + nicknameLength)) {
                    nickname.append(accounts.getByte(n).toInt().toChar())
                }

                accountList.add(
                    AccountDTO(
                        currencyId = currencyId,
                        networkId = networkId,
                        accountId = accountId,
                        chain = chain,
                        bip = bip,
                        accountStatus = accountStatus,
                        nickname = nickname.toString(),
                    )
                )

                currentOffset = (NICKNAME_OFFSET + nicknameLength).toInt()
            }

            return accountList
        }

        const val PUBLIC_KEY_SIZE = 33
        const val CHAIN_CODE_SIZE = 32

        fun parsePublicKeyResponse(
            publicKeyPointer: Memory,
            chainCodeKeyPointer: Memory,
            parentPublicKeyPointer: Memory
        ): PublicKeyDTO {
            return PublicKeyDTO(
                publicKeyPointer.getByteArray(0, PUBLIC_KEY_SIZE).toHexString(),
                chainCodeKeyPointer.getByteArray(0, CHAIN_CODE_SIZE).toHexString(),
                parentPublicKeyPointer.getByteArray(0, PUBLIC_KEY_SIZE).toHexString(),
            )
        }

        fun parseReceivePublicKeyResponse(
            receivePublicKeyPointer: Memory,
        ): ReceivePublicKeyDTO {
            return ReceivePublicKeyDTO(
                receivePublicKeyPointer.getByteArray(0, PUBLIC_KEY_SIZE).toHexString(),
            )
        }
    }
}