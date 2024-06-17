package com.secure.jnet.wallet.domain.models.remote

import com.secure.jnet.wallet.domain.models.enums.CryptoCurrency
import java.math.BigInteger

data class BalanceEntity(
    val cryptoCurrency: CryptoCurrency,
    val amountToken: BigInteger,
    val rate: BigInteger,
)