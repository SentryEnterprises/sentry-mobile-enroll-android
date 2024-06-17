package com.secure.jnet.wallet.domain.repository

import com.secure.jnet.wallet.domain.models.Result
import com.secure.jnet.wallet.domain.models.TransactionEntity
import com.secure.jnet.wallet.domain.models.remote.BalanceEntity
import com.secure.jnet.wallet.domain.models.remote.FeeEntity
import com.secure.jnet.wallet.domain.models.remote.NetworkEntity
import com.secure.jnet.wallet.domain.models.remote.NonceEntity
import com.secure.jnet.wallet.domain.models.remote.UtxoEntity
import retrofit2.Response

interface CryptoRepository {

    suspend fun getNetworkData(networkId: String): Result<NetworkEntity>

    suspend fun getBalance(
        fiat: String?,
        btcNetworkId: String,
        ethNetworkId: String,
        btcAddresses: List<String>,
        ethAddresses: List<String>
    ): Result<List<BalanceEntity>>

    suspend fun getTransactionHistory(
        fiat: String?,
        networkId: String,
        address: String,
        pageToken: String = ""
    ): Result<List<TransactionEntity>>

    suspend fun getNetworkFee(networkId: String): Result<FeeEntity>

    suspend fun getUtxo(networkId: String, addresses: List<String>): Result<List<UtxoEntity>>

    suspend fun getNonce(networkId: String, address: String): Result<NonceEntity>

    suspend fun submitTx(networkId: String, tx: String): Result<Response<Unit>>
}