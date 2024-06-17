package com.secure.jnet.wallet.data.models.remote

import com.google.gson.annotations.SerializedName

data class SubmitTxBodyDTO(
    @SerializedName("data")
    val data: SubmitTxBodyDataDTO
)

data class SubmitTxBodyDataDTO(
    @SerializedName("attributes")
    val attributes: SubmitTxBodyAttributesDTO,
    @SerializedName("id")
    val id: String,
    @SerializedName("type")
    val type: String = "encoded_tx"
)

data class SubmitTxBodyAttributesDTO(
    @SerializedName("tx")
    val tx: String,
)