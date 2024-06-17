package com.secure.jnet.wallet.data.models.remote

import com.google.gson.annotations.SerializedName

data class GetUtxoBodyDTO(
    @SerializedName("data")
    val data: List<GetUtxoDataBodyDTO>,
)

data class GetUtxoDataBodyDTO(
    @SerializedName("id")
    val id: String = "bitcoin",
    @SerializedName("type")
    val type: String = "UTXO_list",
    @SerializedName("attributes")
    val attributes: GetUtxoAttributesBodyDTO
)

data class GetUtxoAttributesBodyDTO(
    @SerializedName("address")
    val address: String,
    @SerializedName("page_limit")
    val pageLimit: Int = DEFAULT_PAGE_LIMIT,
    @SerializedName("page_order")
    val pageOrder: String = DEFAULT_PAGE_ORDER,
)

private const val DEFAULT_PAGE_LIMIT = 1000
private const val DEFAULT_PAGE_ORDER = "desc"