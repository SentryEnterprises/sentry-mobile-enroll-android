package com.secure.jnet.jcwkit.models

data class AccountDTO(
    val currencyId: Int,
    val networkId: Int,
    val accountId: Int,
    val chain: Int = 0,
    val bip: Bip,
    val accountStatus: AccountStatus,
    val nickname: String,
)