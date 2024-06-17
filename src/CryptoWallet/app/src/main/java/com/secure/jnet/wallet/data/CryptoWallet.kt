//package com.secure.jnet.wallet.data
//
////import com.secure.jnet.wallet.domain.models.TokenEntity
//import com.secure.jnet.wallet.domain.models.enums.CryptoCurrency
//import com.secure.jnet.wallet.domain.models.remote.BalanceEntity
////import com.secure.jnet.wallet.util.AmountConverter.tokenToFiat
//import java.math.BigInteger
//import javax.inject.Inject
//import javax.inject.Singleton
//
//@Singleton
//class CryptoWallet @Inject constructor() {
//
//    private lateinit var btcAddress: String
//    private lateinit var ethAddress: String
//
//    private var btcBalance = BigInteger.ZERO
//    private var ethBalance = BigInteger.ZERO
//
//    private var btcRate = BigInteger.ZERO
//    private var ethRate = BigInteger.ZERO
//
//    fun setAddress(cryptoCurrency: CryptoCurrency, address: String) {
//        when (cryptoCurrency) {
//            CryptoCurrency.Bitcoin -> btcAddress = address
//            CryptoCurrency.Ethereum -> ethAddress = address
//        }
//    }
//
//    fun setBalance(cryptoCurrency: CryptoCurrency, balance: BigInteger) {
//        when (cryptoCurrency) {
//            CryptoCurrency.Bitcoin -> btcBalance = balance
//            CryptoCurrency.Ethereum -> ethBalance = balance
//        }
//    }
//
//    fun setRate(cryptoCurrency: CryptoCurrency, rate: BigInteger) {
//        when (cryptoCurrency) {
//            CryptoCurrency.Bitcoin -> btcRate = rate
//            CryptoCurrency.Ethereum -> ethRate = rate
//        }
//    }
//
//    fun getAddress(cryptoCurrency: CryptoCurrency): String {
//        return when (cryptoCurrency) {
//            CryptoCurrency.Bitcoin -> btcAddress
//            CryptoCurrency.Ethereum -> ethAddress
//        }
//    }
//
////    fun getMyTokens(): List<TokenEntity> {
////        return listOf(
////            TokenEntity(
////                CryptoCurrency.Bitcoin,
////                btcBalance,
////                btcBalance.tokenToFiat(CryptoCurrency.Bitcoin, btcRate)
////            ),
////            TokenEntity(
////                CryptoCurrency.Ethereum,
////                ethBalance,
////                ethBalance.tokenToFiat(CryptoCurrency.Ethereum, ethRate)
////            ),
////        )
////    }
//
//    fun getBalance(cryptoCurrency: CryptoCurrency): BalanceEntity {
//        return when (cryptoCurrency) {
//            CryptoCurrency.Bitcoin -> BalanceEntity(
//                CryptoCurrency.Bitcoin,
//                btcBalance,
//                btcRate
//            )
//            CryptoCurrency.Ethereum -> BalanceEntity(
//                CryptoCurrency.Ethereum,
//                ethBalance,
//                ethRate
//            )
//        }
//    }
//
//    fun getRate(cryptoCurrency: CryptoCurrency): BigInteger {
//        return when (cryptoCurrency) {
//            CryptoCurrency.Bitcoin -> btcRate
//            CryptoCurrency.Ethereum -> ethRate
//        }
//    }
//}