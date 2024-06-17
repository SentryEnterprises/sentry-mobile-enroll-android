package com.secure.jnet.wallet.util.ext

import java.math.BigDecimal

fun BigDecimal.onePercent(): BigDecimal {
    return this.multiply(BigDecimal(0.01))
}