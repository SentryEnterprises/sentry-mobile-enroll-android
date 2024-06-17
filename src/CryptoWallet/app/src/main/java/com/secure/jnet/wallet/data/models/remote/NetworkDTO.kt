package com.secure.jnet.wallet.data.models.remote

import com.google.gson.annotations.SerializedName

data class NetworkDTO(
    @SerializedName("data")
    val data: NetworkDataDTO,
    @SerializedName("included")
    val included: List<Any>
)

data class NetworkDataDTO(
    @SerializedName("id")
    val id: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("attributes")
    val attributes: NetworkAttributesDTO
)

data class NetworkAttributesDTO(
    @SerializedName("balance-type")
    val balanceType: String,
    @SerializedName("is-priority-fee-used")
    val isPriorityFeeUsed: Boolean,
    @SerializedName("name")
    val name: String,
    @SerializedName("symbol")
    val symbol: String
)