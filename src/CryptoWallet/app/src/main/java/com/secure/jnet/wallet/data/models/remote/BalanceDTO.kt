package com.secure.jnet.wallet.data.models.remote

import com.google.gson.annotations.SerializedName

data class BalanceDTO(
    @SerializedName("data")
    val data: List<BalanceDataDTO>,
    @SerializedName("included")
    val included: List<TokenPriceDTO>,
)

data class BalanceDataDTO(
    @SerializedName("id")
    val id: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("attributes")
    val attributes: BalanceAttributesDTO
)

data class BalanceAttributesDTO(
    @SerializedName("address")
    val address: String,
    @SerializedName("assets_path")
    val assetsPath: String,
    @SerializedName("confirmed_balance")
    val confirmedBalance: String,
    @SerializedName("confirmed_block")
    val confirmedBlock: Int,
    @SerializedName("decimals")
    val decimals: Int,
    @SerializedName("pending_balance")
    val pendingBalance: String,
    @SerializedName("symbol")
    val symbol: String
)