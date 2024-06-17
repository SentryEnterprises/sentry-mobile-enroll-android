package com.secure.jnet.wallet.util.ext

import java.text.NumberFormat
import java.util.*

fun String.currencyFormat(): String {
    var current = this
    if (current.isEmpty()) current = "0"
    return try {
        if (current.contains('.')) {
            NumberFormat.getNumberInstance(Locale.getDefault())
                .format(current.replace(",", "").toDouble())
        } else {
            NumberFormat.getNumberInstance(Locale.getDefault())
                .format(current.replace(",", "").toLong())
        }
    } catch (e: Exception) {
        "0"
    }
}