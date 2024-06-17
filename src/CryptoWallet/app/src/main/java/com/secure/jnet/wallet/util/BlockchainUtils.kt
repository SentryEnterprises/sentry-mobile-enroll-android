//package com.secure.jnet.wallet.util
//
//import android.content.Context
//import android.content.Intent
//import android.net.Uri
//import androidx.core.content.ContextCompat
//import com.secure.jnet.wallet.domain.models.enums.CryptoCurrency
//
//object BlockchainUtils {
//
//    fun openBlockchainExplorer(
//        cryptoCurrency: CryptoCurrency,
//        context: Context,
//        transactionHash: String,
//    ) {
//        val baseUrl = when (cryptoCurrency) {
//            CryptoCurrency.Bitcoin -> {
//                if (TESTNET) {
//                    "https://sochain.com/tx/BTCTEST/"
//                } else {
//                    "https://mempool.space/tx/"
//                }
//            }
//            CryptoCurrency.Ethereum -> {
//                if (TESTNET) {
//                    "https://holesky.etherscan.io/tx/"
//                } else {
//                    "https://etherscan.io/tx/"
//                }
//            }
//        }
//
//        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(baseUrl + transactionHash))
//        ContextCompat.startActivity(context, intent, null)
//    }
//}