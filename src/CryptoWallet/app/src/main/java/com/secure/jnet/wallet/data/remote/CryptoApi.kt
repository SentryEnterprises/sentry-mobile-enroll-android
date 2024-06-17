package com.secure.jnet.wallet.data.remote

import com.secure.jnet.wallet.data.models.remote.BalanceBodyDTO
import com.secure.jnet.wallet.data.models.remote.BalanceDTO
import com.secure.jnet.wallet.data.models.remote.GetUtxoBodyDTO
import com.secure.jnet.wallet.data.models.remote.GetUtxoDTO
import com.secure.jnet.wallet.data.models.remote.NetworkDTO
import com.secure.jnet.wallet.data.models.remote.NetworkFeeDTO
import com.secure.jnet.wallet.data.models.remote.NonceDTO
import com.secure.jnet.wallet.data.models.remote.SubmitTxBodyDTO
import com.secure.jnet.wallet.data.models.remote.TransactionBodyDTO
import com.secure.jnet.wallet.data.models.remote.TransactionDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CryptoApi {

    @GET("network/{id}")
    suspend fun getNetworkData(
        @Path("id") id: String,
    ): NetworkDTO

    @POST("balances")
    suspend fun getBalance(
        @Query("fiat_currency") fiatCurrency: String? = null,
        @Body balanceBodyDTO: BalanceBodyDTO,
    ): BalanceDTO

    @POST("network/{id}/tx/history")
    suspend fun getTransactionHistory(
        @Path("id") id: String,
        @Query("fiat_currency") fiatCurrency: String? = null,
        @Query("simplified") simplified: Boolean = true,
        @Body transactionBodyDTO: TransactionBodyDTO,
    ): TransactionDTO

    @GET("network/{id}/fee")
    suspend fun getNetworkFee(
        @Path("id") id: String,
        @Query("fiat_currency") fiatCurrency: String? = null,
    ): NetworkFeeDTO

    @POST("network/{id}/utxo")
    suspend fun getUTXO(
        @Path("id") id: String,
        @Query("fiat_currency") fiatCurrency: String? = null,
        @Body getUtxoBodyDTO: GetUtxoBodyDTO,
    ): GetUtxoDTO

    @GET("network/{id}/nonce/{address}")
    suspend fun getNonce(
        @Path("id") id: String,
        @Path("address") address: String,
        @Query("fiat_currency") fiatCurrency: String? = null,
    ): NonceDTO

    @POST("network/{id}/tx")
    suspend fun submitTx(
        @Path("id") id: String,
        @Body submitTxBodyDTO: SubmitTxBodyDTO,
    ): Response<Unit>
}