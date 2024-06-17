//package com.secure.jnet.wallet.data.crypto
//
//import com.secure.jnet.jcwkit.JCWKitUtils
//import com.secure.jnet.wallet.domain.models.enums.CryptoCurrency
//import javax.inject.Inject
//
//class AddressValidator @Inject constructor(
//    private val jcwKitUtils: JCWKitUtils
//) {
//
//    fun validateAddress(address: String): CryptoCurrency? {
//        return try {
//            jcwKitUtils.addressToScript(address)
//
//            if (address.startsWith("0x")) {
//                CryptoCurrency.Ethereum
//            } else {
//                CryptoCurrency.Bitcoin
//            }
//        } catch (e: Exception) {
//            null
//        }
//    }
//}