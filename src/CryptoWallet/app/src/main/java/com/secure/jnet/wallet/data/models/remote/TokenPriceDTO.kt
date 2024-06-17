package com.secure.jnet.wallet.data.models.remote

import com.google.gson.annotations.SerializedName

data class TokenPriceDTO(
    @SerializedName("id")
    val id: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("attributes")
    val attributes: TokenPriceAttributesDTO
)

data class TokenPriceAttributesDTO(
    @SerializedName("fiat")
    val fiat: String,
    @SerializedName("network")
    val network: String,
    @SerializedName("timestamp")
    val timestamp: Int,
    @SerializedName("value")
    val value: String
)