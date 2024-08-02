package com.secure.jnet.jcwkit

import com.sun.jna.Callback
import com.sun.jna.Pointer

fun interface SmartCardApduCallback : Callback {
    fun call(dataIn: Pointer?, dataInLen: Int, dataOut: Pointer?, dataOutLen: Pointer?): Int
}

fun interface NonNativeSmartCardApduCallback : Callback {
    fun call(dataIn: ByteArray): Result<ByteArray>
}