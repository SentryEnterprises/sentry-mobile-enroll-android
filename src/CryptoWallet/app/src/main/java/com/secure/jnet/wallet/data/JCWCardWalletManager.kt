//package com.secure.jnet.wallet.data
//
//import android.nfc.Tag
//import android.nfc.tech.IsoDep
//import com.secure.jnet.jcwkit.JCWKit
//import com.secure.jnet.jcwkit.SmartCardApduCallback
//import com.secure.jnet.wallet.data.nfc.NfcAction
//import com.secure.jnet.wallet.domain.models.enums.WalletStatus
//import com.secure.jnet.wallet.util.ByteUtility
//import timber.log.Timber
//import javax.inject.Inject
//import javax.inject.Singleton
//
//// TODO: This isn't used and should be removed
//@Singleton
//class JCWCardWalletManager @Inject constructor() {
//
//    private val jcwKit = JCWKit()
//
//    private var tag: Tag? = null
//    private var mIsoDep: IsoDep? = null
//
//    private var nfcAction: NfcAction? = null
//
//    @Volatile
//    private var inProcess = false
//
////    private val callBack = SmartCardApduCallback { dataIn, dataInLen, dataOut, dataOutLen ->
////        val dataInBytes = dataIn?.getByteArray(0, dataInLen)!!
////
////        dataInBytes.logCommand()
////
////        // Exceptions thrown from this call are getting lost, and returning to the C code without any idea that something happened.
////        // things like TagLostException and others really need to be caught here and dealt with by returning an error code
////        val response = mIsoDep!!.transceive(dataInBytes)
////        val responseLength = response.size
////
////        response.logResponse()
////
////        dataOut!!.write(0, response, 0, responseLength)
////        dataOutLen!!.setInt(0, responseLength)
////
////        0
////    }
//
//    fun onTagDiscovered(tag: Tag?) {
//        Timber.d("----> onTagDiscovered()")
//
//        this@JCWCardWalletManager.tag = tag
//
////        nfcAction?.let {
////            startCardExchange(it)
////        }
//    }
//
////    fun getWalletStatus(): WalletStatus {
////        jcwKit.initWalletSdk(callBack)
////
////        jcwKit.selectWallet()
////
////        val walletStatus = jcwKit.getWalletStatus()
////        Timber.d("-------> walletStatus = $walletStatus")
////
////        jcwKit.deinitWalletSdk()
////
//////        return walletStatus.mapToEntity()
////        return WalletStatus.NOT_INITIALIZED
////    }
//
////    private fun openConnection(): Boolean {
////        return try {
////            mIsoDep = IsoDep.get(tag)
////            mIsoDep!!.timeout = 30000
////            mIsoDep!!.connect()
////            true
////        } catch (e: Exception) {
////            Timber.e(e)
////            false
////        }
////    }
////
////    private fun closeConnection() {
////        try {
////            mIsoDep?.close()
////        } catch (e: Exception) {
////            Timber.e(e)
////        } finally {
////            mIsoDep = null
////        }
////    }
//
//    private fun ByteArray.logCommand() {
//        Timber.d("---------------------------")
//        Timber.d("=> ${
//            ByteUtility
//                .byteArrayToHexString(this)
//                .chunked(2).joinToString(separator = " ")
//        }")
//    }
//
//    private fun ByteArray.logResponse() {
//        Timber.d("<= ${
//            ByteUtility
//                .byteArrayToHexString(this)
//                .chunked(2).joinToString(separator = " ")
//        }")
//        Timber.d("---------------------------\n")
//    }
//}