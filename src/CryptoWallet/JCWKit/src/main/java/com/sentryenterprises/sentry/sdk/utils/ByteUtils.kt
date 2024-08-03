package com.sentryenterprises.sentry.sdk.utils

import com.sun.jna.Memory

fun ByteArray.asPointer() = Memory(size.toLong()).apply {
    forEachIndexed { index, i ->
        setByte(index.toLong(), i.toByte())
    }
}