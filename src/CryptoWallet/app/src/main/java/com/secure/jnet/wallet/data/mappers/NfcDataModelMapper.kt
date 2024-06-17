//package com.secure.jnet.wallet.data.mappers
//
//import com.secure.jnet.jcwkit.models.WalletStatusDTO
//import com.secure.jnet.wallet.domain.models.enums.WalletStatus
//
//fun WalletStatusDTO.mapToEntity(): WalletStatus {
//    return if (this.gwlcs.persoDone) {
//        WalletStatus.HAS_ACCOUNT
//    } else {
//        WalletStatus.NOT_INITIALIZED
//    }
//}