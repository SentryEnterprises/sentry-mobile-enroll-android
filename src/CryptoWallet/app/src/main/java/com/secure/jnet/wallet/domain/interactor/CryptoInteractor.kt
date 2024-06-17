//package com.secure.jnet.wallet.domain.interactor
//
//import com.secure.jnet.wallet.data.CryptoWallet
//import com.secure.jnet.wallet.domain.models.Result
//import com.secure.jnet.wallet.domain.models.TransactionEntity
//import com.secure.jnet.wallet.domain.models.enums.CryptoCurrency
//import com.secure.jnet.wallet.domain.models.remote.BalanceEntity
//import com.secure.jnet.wallet.domain.models.remote.FeeEntity
//import com.secure.jnet.wallet.domain.models.remote.NonceEntity
//import com.secure.jnet.wallet.domain.models.remote.UtxoEntity
//import com.secure.jnet.wallet.domain.repository.CryptoRepository
//import com.secure.jnet.wallet.util.TESTNET
//import retrofit2.Response
//import javax.inject.Inject
//import javax.inject.Singleton
//
//@Singleton
//class CryptoInteractor @Inject constructor(
//    private val cryptoWallet: CryptoWallet,
//    private val cryptoRepository: CryptoRepository,
//) {
//
//    suspend fun getBalance(): Result<List<BalanceEntity>> {
//        val btcNetworkId = if (TESTNET) CryptoCurrency.Bitcoin.ticker + TESTNET_SUFFIX
//        else CryptoCurrency.Bitcoin.ticker
//
//        val ethNetworkId = if (TESTNET) CryptoCurrency.Ethereum.ticker + TESTNET_SUFFIX
//        else CryptoCurrency.Ethereum.ticker
//
//        val btcAddresses = listOf(cryptoWallet.getAddress(CryptoCurrency.Bitcoin))
//        val ethAddresses = listOf(cryptoWallet.getAddress(CryptoCurrency.Ethereum))
//
//        return cryptoRepository.getBalance(
//            fiat = DEFAULT_FIAT,
//            btcNetworkId = btcNetworkId,
//            ethNetworkId = ethNetworkId,
//            btcAddresses = btcAddresses,
//            ethAddresses = ethAddresses,
//        ).also { result ->
//            if (result is Result.Success) {
//                result.data.forEach {
//                    cryptoWallet.setBalance(it.cryptoCurrency, it.amountToken)
//                    cryptoWallet.setRate(it.cryptoCurrency, it.rate)
//                }
//            }
//        }
//    }
//
//    suspend fun getTransactionHistory(
//        cryptoCurrency: CryptoCurrency,
//    ): Result<List<TransactionEntity>> {
//        val networkId = if (TESTNET) cryptoCurrency.ticker + TESTNET_SUFFIX
//        else cryptoCurrency.ticker
//
//        val address = cryptoWallet.getAddress(cryptoCurrency)
//
//        return cryptoRepository.getTransactionHistory(
//            networkId = networkId,
//            address = address,
//            fiat = null
//        )
//    }
//
//    suspend fun getAllTransactionHistory(): Result<List<TransactionEntity>> {
//        val btcNetworkId = if (TESTNET) CryptoCurrency.Bitcoin.ticker + TESTNET_SUFFIX
//        else CryptoCurrency.Bitcoin.ticker
//
//        val ethNetworkId = if (TESTNET) CryptoCurrency.Ethereum.ticker + TESTNET_SUFFIX
//        else CryptoCurrency.Ethereum.ticker
//
//        val btcAddress = cryptoWallet.getAddress(CryptoCurrency.Bitcoin)
//        val ethAddress = cryptoWallet.getAddress(CryptoCurrency.Ethereum)
//
//        val btcResult = cryptoRepository.getTransactionHistory(
//            networkId = btcNetworkId,
//            address = btcAddress,
//            fiat = null
//        )
//
//        val ethResult = cryptoRepository.getTransactionHistory(
//            networkId = ethNetworkId,
//            address = ethAddress,
//            fiat = null
//        )
//
//        if (btcResult is Result.Error) {
//            return btcResult
//        }
//
//        if (ethResult is Result.Error) {
//            return ethResult
//        }
//
//        val resultList = btcResult.asSuccess().data + ethResult.asSuccess().data
//
//        return Result.Success(resultList)
//    }
//
//    suspend fun getNetworkFee(cryptoCurrency: CryptoCurrency): Result<FeeEntity> {
//        return cryptoRepository.getNetworkFee(cryptoCurrency.ticker)
//    }
//
//    suspend fun getUtxo(): Result<List<UtxoEntity>> {
//        val networkId = if (TESTNET) CryptoCurrency.Bitcoin.ticker + TESTNET_SUFFIX
//        else CryptoCurrency.Bitcoin.ticker
//
//        val addresses = listOf(cryptoWallet.getAddress(CryptoCurrency.Bitcoin))
//
//        return cryptoRepository.getUtxo(
//            networkId = networkId,
//            addresses = addresses,
//        )
//    }
//
//    suspend fun getNonce(): Result<NonceEntity> {
//        val networkId = if (TESTNET) CryptoCurrency.Ethereum.ticker + TESTNET_SUFFIX
//        else CryptoCurrency.Ethereum.ticker
//
//        val address = cryptoWallet.getAddress(CryptoCurrency.Ethereum)
//
//        return cryptoRepository.getNonce(
//            networkId = networkId,
//            address = address,
//        )
//    }
//
//    suspend fun submitTx(cryptoCurrency: CryptoCurrency, tx: String): Result<Response<Unit>> {
//        val id = if (TESTNET) cryptoCurrency.ticker + TESTNET_SUFFIX
//        else cryptoCurrency.ticker
//
//        return cryptoRepository.submitTx(id, tx)
//    }
//
//    companion object {
//        private const val DEFAULT_FIAT = "usd"
//        private const val TESTNET_SUFFIX = "-T"
//    }
//}