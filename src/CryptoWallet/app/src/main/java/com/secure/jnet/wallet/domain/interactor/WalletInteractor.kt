//package com.secure.jnet.wallet.domain.interactor
//
//import com.secure.jnet.wallet.data.CryptoWallet
//import com.secure.jnet.wallet.data.crypto.bitcoin.BitcoinTransactionBuilder
//import com.secure.jnet.wallet.data.crypto.ethereum.EthereumTransactionBuilder
//import com.secure.jnet.wallet.data.crypto.models.RawTransactionDTO
//import com.secure.jnet.wallet.domain.models.AccountEntity
//import com.secure.jnet.wallet.domain.models.TokenEntity
//import com.secure.jnet.wallet.domain.models.enums.CryptoCurrency
//import com.secure.jnet.wallet.domain.models.remote.BalanceEntity
//import com.secure.jnet.wallet.domain.models.remote.BitcoinFeeEntity
//import com.secure.jnet.wallet.domain.models.remote.EthereumFeeEntity
//import com.secure.jnet.wallet.domain.models.remote.FeeEntity
//import com.secure.jnet.wallet.domain.models.remote.UtxoEntity
//import java.math.BigInteger
//import javax.inject.Inject
//import javax.inject.Singleton
//
//@Singleton
//class WalletInteractor @Inject constructor(
//    private val cryptoWallet: CryptoWallet,
//    private val bitcoinTransactionBuilder: BitcoinTransactionBuilder,
//    private val ethereumTransactionBuilder: EthereumTransactionBuilder,
//) {
//
//    fun initWallet(accounts: List<AccountEntity>) {
//        accounts.forEach {
//            cryptoWallet.setAddress(it.cryptoCurrency, it.address)
//        }
//    }
//
//    fun getMyTokens(): List<TokenEntity> {
//        return cryptoWallet.getMyTokens()
//    }
//
//    fun getRate(cryptoCurrency: CryptoCurrency): BigInteger {
//        return cryptoWallet.getRate(cryptoCurrency)
//    }
//
//    fun getAddress(cryptoCurrency: CryptoCurrency): String {
//        return cryptoWallet.getAddress(cryptoCurrency)
//    }
//
//    fun getBalance(cryptoCurrency: CryptoCurrency): BalanceEntity {
//        return cryptoWallet.getBalance(cryptoCurrency)
//    }
//
//    fun calculateFee(
//        cryptoCurrency: CryptoCurrency,
//        tokenAmount: BigInteger,
//        feeRate: FeeEntity,
//        utxo: List<UtxoEntity>?,
//        maxAmount: Boolean,
//    ): BigInteger {
//        return when (cryptoCurrency) {
//            CryptoCurrency.Bitcoin -> {
//                bitcoinTransactionBuilder.calculateFee(
//                    amount = tokenAmount.toLong(),
//                    utxo = utxo!!,
//                    feeRate = feeRate as BitcoinFeeEntity,
//                    maxAmount,
//                ).toBigInteger()
//            }
//
//            CryptoCurrency.Ethereum -> {
//                ethereumTransactionBuilder.calculateFee(
//                    maxFeePerGas = (feeRate as EthereumFeeEntity).maxFeePerGas
//                ).toBigInteger()
//            }
//        }
//    }
//
//    fun getMaximumSpendableAmount(
//        cryptoCurrency: CryptoCurrency,
//        feeRate: FeeEntity,
//        utxo: List<UtxoEntity>?,
//    ): BigInteger {
//        val tokenBalance = cryptoWallet.getBalance(cryptoCurrency).amountToken
//
//        val fee = calculateFee(
//            cryptoCurrency = cryptoCurrency,
//            tokenAmount = tokenBalance,
//            feeRate = feeRate,
//            utxo,
//            true
//        )
//
//        return tokenBalance - fee
//    }
//
//    fun buildTransaction(
//        cryptoCurrency: CryptoCurrency,
//        address: String,
//        tokenAmount: BigInteger,
//        fee: FeeEntity,
//        utxo: List<UtxoEntity>?,
//        nonce: Long?,
//        maxAmount: Boolean,
//    ): RawTransactionDTO {
//        return when (cryptoCurrency) {
//            CryptoCurrency.Bitcoin -> {
//                bitcoinTransactionBuilder.buildTransaction(
//                    amount = tokenAmount.toLong(),
//                    addressTo = address,
//                    addressFrom = cryptoWallet.getAddress(cryptoCurrency),
//                    utxo = utxo!!,
//                    feeRate = fee as BitcoinFeeEntity,
//                    maxAmount = maxAmount,
//                )
//            }
//
//            CryptoCurrency.Ethereum -> {
//                ethereumTransactionBuilder.buildTransaction(
//                    nonce = nonce!!,
//                    maxPriorityFeePerGas = (fee as EthereumFeeEntity).maxPriorityFeePerGas,
//                    maxFeePerGas = fee.maxFeePerGas,
//                    amount = tokenAmount,
//                    addressTo = address
//                )
//            }
//        }
//    }
//}