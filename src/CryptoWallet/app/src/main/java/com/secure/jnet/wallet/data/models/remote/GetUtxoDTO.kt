package com.secure.jnet.wallet.data.models.remote

import com.google.gson.annotations.SerializedName

data class GetUtxoDTO(
    @SerializedName("data")
    val data: List<GetUtxoDataDTO>,
    @SerializedName("included")
    val included: List<Any>,
)

data class GetUtxoDataDTO(
    @SerializedName("id")
    val id: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("attributes")
    val attributes: GetUtxoAttributesDTO
)

data class GetUtxoAttributesDTO(
    @SerializedName("address")
    val address: String,
    @SerializedName("next_page_token")
    val nextPageToken: String,
    @SerializedName("utxo")
    val utxo: List<UtxoDTO>
)

data class UtxoDTO(
    @SerializedName("amount")
    val amount: String,
    @SerializedName("denomination")
    val denomination: String,
    @SerializedName("index")
    val index: Int,
    @SerializedName("script")
    val script: String,
    @SerializedName("script_type")
    val scriptType: String,
    @SerializedName("transaction_id")
    val transactionId: String
)