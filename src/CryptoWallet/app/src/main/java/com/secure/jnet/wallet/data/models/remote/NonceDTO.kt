package com.secure.jnet.wallet.data.models.remote

import com.google.gson.annotations.SerializedName

data class NonceDTO(
    @SerializedName("data")
    val data: NonceDataDTO
)

data class NonceDataDTO(
    @SerializedName("id")
    val id: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("attributes")
    val attributes: NonceAttributesDTO
)

data class NonceAttributesDTO(
    @SerializedName("nonce")
    val nonce: Long
)