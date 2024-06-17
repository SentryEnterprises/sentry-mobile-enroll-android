package com.secure.jnet.wallet.data.models.remote

import com.google.gson.annotations.SerializedName

data class BalanceBodyDTO(
    @SerializedName("data")
    val data: List<BalanceBodyDataDTO>
)

data class BalanceBodyDataDTO(
    @SerializedName("attributes")
    val attributes: BalanceBodyAttributesDTO,
    @SerializedName("id")
    val id: String,
    @SerializedName("type")
    val type: String = "balances_networks"
)

data class BalanceBodyAttributesDTO(
    @SerializedName("network")
    val network: String,
    @SerializedName("addresses")
    val addresses: List<String>,
    @SerializedName("tokens")
    val tokens: List<Any>
)