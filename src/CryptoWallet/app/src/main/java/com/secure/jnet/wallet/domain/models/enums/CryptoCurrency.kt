package com.secure.jnet.wallet.domain.models.enums

import androidx.annotation.DrawableRes
import com.secure.jnet.wallet.R

enum class CryptoCurrency {
    Bitcoin,
    Ethereum;

    val ticker: String
        get() {
            return when (this) {
                Bitcoin -> "BTC"
                Ethereum -> "ETH"
            }
        }
}

@DrawableRes
fun CryptoCurrency.getIcon():  Int {
    return when (this) {
        CryptoCurrency.Bitcoin -> R.drawable.ic_token_btc
        CryptoCurrency.Ethereum -> R.drawable.ic_token_eth
    }
}

fun String.parseCryptoCurrency(): CryptoCurrency {
    return when (this.lowercase()) {
        "btc" -> CryptoCurrency.Bitcoin
        "eth" -> CryptoCurrency.Ethereum
        else -> {
            throw IllegalArgumentException("No such '$this' crypto currency")
        }
    }
}