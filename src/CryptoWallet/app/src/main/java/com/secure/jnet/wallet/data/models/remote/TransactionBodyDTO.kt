package com.secure.jnet.wallet.data.models.remote

import com.google.gson.annotations.SerializedName

data class TransactionBodyDTO(
    @SerializedName("data")
    val data: TransactionBodyDataDTO
)

data class TransactionBodyDataDTO(
    @SerializedName("attributes")
    val attributes: TransactionBodyAttributesDTO,
    @SerializedName("id")
    val id: String,
    @SerializedName("type")
    val type: String = "tx_history"
)

data class TransactionBodyAttributesDTO(
    @SerializedName("address_data")
    val addressData: List<AddressDataDTO>
)

data class AddressDataDTO(
    @SerializedName("address")
    val address: String,
    @SerializedName("page_limit")
    val pageLimit: Int = 50,
    @SerializedName("page_token")
    val pageToken: String,
    @SerializedName("order")
    val order: String = "desc",
)