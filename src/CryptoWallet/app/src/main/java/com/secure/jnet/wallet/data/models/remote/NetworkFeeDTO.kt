package com.secure.jnet.wallet.data.models.remote


import com.google.gson.annotations.SerializedName

data class NetworkFeeDTO(
    @SerializedName("data")
    val data: NetworkFeeDataDTO,
    @SerializedName("included")
    val included: List<Any>
)

data class NetworkFeeDataDTO(
    @SerializedName("id")
    val id: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("attributes")
    val attributes: NetworkFeeAttributesDTO
)

data class NetworkFeeAttributesDTO(
    @SerializedName("high")
    val high: FeePriorityDTO,
    @SerializedName("low")
    val low: FeePriorityDTO,
    @SerializedName("normal")
    val normal: FeePriorityDTO
)

data class FeePriorityDTO(
    @SerializedName("id")
    val id: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("attributes")
    val attributes: FeeAttributesDTO
)

data class FeeAttributesDTO(
    @SerializedName("evm_fee_estimate")
    val ethereumFee: EthereumFeeDTO?,
    @SerializedName("fee_estimate")
    val bitcoinLikeFee: BitcoinFeeDTO? = null
)

data class BitcoinFeeDTO(
    @SerializedName("fee")
    val fee: Long,
)

data class EthereumFeeDTO(
    @SerializedName("max_priority_fee")
    val maxPriorityFee: Long,
    @SerializedName("max_total_fee")
    val maxTotalFee: Long
)