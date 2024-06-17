package com.secure.jnet.wallet.util.ext

import java.util.Calendar
import java.util.Date

fun Date.isToday(): Boolean {
    val calendar1 = Calendar.getInstance().apply {
        time = this@isToday
    }
    val calendar2 = Calendar.getInstance().apply {
        time = Date()
    }

    return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
            calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR)
}

fun Date.isYesterday(): Boolean {
    val calendar1 = Calendar.getInstance().apply {
        time = this@isYesterday
    }
    val calendar2 = Calendar.getInstance().apply {
        time = Date()
    }

    calendar2.add(Calendar.DAY_OF_YEAR, -1)

    return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
            calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR)
}