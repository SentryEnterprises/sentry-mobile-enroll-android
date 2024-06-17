package com.secure.jnet.wallet.data.repository

import com.secure.jnet.wallet.data.mappers.remote.BalanceDataModelMapper
import com.secure.jnet.wallet.data.mappers.remote.GetUtxoDataModelMapper
import com.secure.jnet.wallet.data.mappers.remote.GetUtxoEntityMapper
import com.secure.jnet.wallet.data.mappers.remote.NetworkDataMapper
import com.secure.jnet.wallet.data.mappers.remote.NetworkFeeDataModelMapper
import com.secure.jnet.wallet.data.mappers.remote.NonceDataModelMapper
import com.secure.jnet.wallet.data.mappers.remote.TransactionDataModelMapper
import com.secure.jnet.wallet.data.models.remote.AddressDataDTO
import com.secure.jnet.wallet.data.models.remote.BalanceBodyAttributesDTO
import com.secure.jnet.wallet.data.models.remote.BalanceBodyDTO
import com.secure.jnet.wallet.data.models.remote.BalanceBodyDataDTO
import com.secure.jnet.wallet.data.models.remote.SubmitTxBodyAttributesDTO
import com.secure.jnet.wallet.data.models.remote.SubmitTxBodyDTO
import com.secure.jnet.wallet.data.models.remote.SubmitTxBodyDataDTO
import com.secure.jnet.wallet.data.models.remote.TransactionBodyAttributesDTO
import com.secure.jnet.wallet.data.models.remote.TransactionBodyDTO
import com.secure.jnet.wallet.data.models.remote.TransactionBodyDataDTO
import com.secure.jnet.wallet.data.remote.CryptoApi
import com.secure.jnet.wallet.data.remote.utils.makeRequest
import com.secure.jnet.wallet.domain.models.Result
import com.secure.jnet.wallet.domain.models.TransactionEntity
import com.secure.jnet.wallet.domain.models.remote.BalanceEntity
import com.secure.jnet.wallet.domain.models.remote.FeeEntity
import com.secure.jnet.wallet.domain.models.remote.NetworkEntity
import com.secure.jnet.wallet.domain.models.remote.NonceEntity
import com.secure.jnet.wallet.domain.models.remote.UtxoEntity
import com.secure.jnet.wallet.domain.repository.CryptoRepository
import kotlinx.coroutines.CoroutineDispatcher
import retrofit2.Response
import javax.inject.Inject

class CryptoRepositoryImpl @Inject constructor(
    private val dispatcher: CoroutineDispatcher,
    private val cryptoApi: CryptoApi,
    private val networkDataMapper: NetworkDataMapper,
    private val networkFeeDataModelMapper: NetworkFeeDataModelMapper,
    private val balanceDataModelMapper: BalanceDataModelMapper,
    private val getUtxoDataModelMapper: GetUtxoDataModelMapper,
    private val getUtxoEntityMapper: GetUtxoEntityMapper,
    private val nonceDataModelMapper: NonceDataModelMapper,
    private val transactionDataModelMapper: TransactionDataModelMapper,
) : CryptoRepository {

    override suspend fun getNetworkData(networkId: String): Result<NetworkEntity> =
        dispatcher.makeRequest {
            val response = cryptoApi.getNetworkData(networkId)

            networkDataMapper.mapToEntity(response)
        }

    override suspend fun getNetworkFee(networkId: String): Result<FeeEntity> =
        dispatcher.makeRequest {
            val response = cryptoApi.getNetworkFee(networkId)

            networkFeeDataModelMapper.mapToEntity(response)
        }

    override suspend fun getBalance(
        fiat: String?,
        btcNetworkId: String,
        ethNetworkId: String,
        btcAddresses: List<String>,
        ethAddresses: List<String>,
    ): Result<List<BalanceEntity>> =
        dispatcher.makeRequest {
            val addressList = mutableListOf<BalanceBodyDataDTO>()

            if (btcAddresses.isNotEmpty()) {
                addressList.add(
                    BalanceBodyDataDTO(
                        BalanceBodyAttributesDTO(
                            btcNetworkId,
                            btcAddresses,
                            emptyList()
                        ),
                        btcNetworkId
                    )
                )
            }

            if (ethAddresses.isNotEmpty()) {
                addressList.add(
                    BalanceBodyDataDTO(
                        BalanceBodyAttributesDTO(
                            ethNetworkId,
                            ethAddresses,
                            emptyList()
                        ),
                        ethNetworkId
                    )
                )
            }

            val balanceBodyDTO = BalanceBodyDTO(
                addressList
            )

            val response = cryptoApi.getBalance(
                fiatCurrency = fiat,
                balanceBodyDTO = balanceBodyDTO
            )

            balanceDataModelMapper.mapToEntity(response)
        }

    override suspend fun getTransactionHistory(
        fiat: String?,
        networkId: String,
        address: String,
        pageToken: String,
    ): Result<List<TransactionEntity>> =
        dispatcher.makeRequest {
            val transactionBodyDTO = TransactionBodyDTO(
                TransactionBodyDataDTO(
                    TransactionBodyAttributesDTO(
                        listOf(
                            AddressDataDTO(
                                address = address,
                                pageToken = pageToken,
                            )
                        )
                    ),
                    networkId,
                )
            )

            val response = cryptoApi.getTransactionHistory(
                networkId,
                fiatCurrency = fiat,
                transactionBodyDTO = transactionBodyDTO,
            )

            transactionDataModelMapper.mapToEntity(response)
        }

    override suspend fun getUtxo(
        networkId: String,
        addresses: List<String>,
    ): Result<List<UtxoEntity>> =
        dispatcher.makeRequest {
            val getUtxoBodyDTO = getUtxoEntityMapper.mapFromEntity(addresses)

            val response = cryptoApi.getUTXO(networkId, null, getUtxoBodyDTO)

            getUtxoDataModelMapper.mapToEntity(response)
        }

    override suspend fun getNonce(
        networkId: String,
        address: String,
    ): Result<NonceEntity> =
        dispatcher.makeRequest {
            val response = cryptoApi.getNonce(networkId, address)

            nonceDataModelMapper.mapToEntity(response)
        }

    override suspend fun submitTx(
        networkId: String,
        tx: String,
    ): Result<Response<Unit>> =
        dispatcher.makeRequest {
            val submitTxBodyDTO = SubmitTxBodyDTO(
                data = SubmitTxBodyDataDTO(
                    attributes = SubmitTxBodyAttributesDTO(
                        tx = tx
                    ),
                    id = networkId,
                )
            )
            val response = cryptoApi.submitTx(networkId, submitTxBodyDTO)

            response
        }
}