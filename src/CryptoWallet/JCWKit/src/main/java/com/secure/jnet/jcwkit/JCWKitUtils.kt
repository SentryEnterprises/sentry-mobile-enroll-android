package com.secure.jnet.jcwkit

import com.secure.jnet.jcwkit.utils.toHexString
import com.sun.jna.Memory
import com.sun.jna.Pointer
import com.sun.jna.ptr.IntByReference
import java.io.IOException

class JCWKitUtils {

    fun addressToScript(address: String): String {
        val addressLength = address.length + 1L

        val addressPointer: Pointer = Memory(addressLength).apply {
            address.forEachIndexed { index, i ->
                setByte(index.toLong(), i.code.toByte())
            }
        }

        val scriptPointer: Pointer = Memory(addressLength)
        val scriptLengthPointer = IntByReference()

        val result = NativeLib.INSTANCE.LibSdkAddressToScript(
            addressPointer,
            addressLength,
            scriptPointer,
            scriptLengthPointer
        )

        if (result != 0) {
            throw IOException("Result error $result")
        }

        val script = scriptPointer.getByteArray(
            0, 22 // scriptLengthPointer.value
        ).toHexString()

        if (script.isEmpty()) {
            throw IOException("Script is empty")
        }

        return script
    }
}