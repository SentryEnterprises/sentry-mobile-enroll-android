package com.secure.jnet.wallet.data.models.remote

import com.google.gson.annotations.SerializedName

data class TransactionDTO(
    @SerializedName("data")
    val data: List<TransactionDataDTO>
)

data class TransactionDataDTO(
    @SerializedName("id")
    val id: String,
    @SerializedName("attributes")
    val attributes: TransactionAttributesDTO
)

data class TransactionAttributesDTO(
    @SerializedName("address")
    val address: String,
    @SerializedName("next_page_token")
    val nextPageToken: String,
    @SerializedName("simplified_transactions")
    val simplifiedTransactions: List<SimplifiedTransactionDTO>
)

data class SimplifiedTransactionDTO(
    @SerializedName("amount")
    val amount: String,
    @SerializedName("confirmations")
    val confirmations: Int,
    @SerializedName("date")
    val date: Long,
    @SerializedName("decimals")
    val decimals: Int,
    @SerializedName("denomination")
    val denomination: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("price")
    val price: PriceDTO,
    @SerializedName("status")
    val status: String,
    @SerializedName("type")
    val type: String
)

data class PriceDTO(
    @SerializedName("id")
    val id: String,
    @SerializedName("attributes")
    val attributes: PriceAttributesDTO
)

data class PriceAttributesDTO(
    @SerializedName("contract_address")
    val contractAddress: String,
    @SerializedName("fiat")
    val fiat: String,
    @SerializedName("market_cap")
    val marketCap: String,
    @SerializedName("network")
    val network: String,
    @SerializedName("price")
    val price: String,
    @SerializedName("timestamp")
    val timestamp: Int,
    @SerializedName("total_volume")
    val totalVolume: String
)