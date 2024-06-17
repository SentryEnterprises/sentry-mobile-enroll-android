//package com.secure.jnet.wallet.data.mappers
//
//import com.secure.jnet.jcwkit.models.WalletStatusDTO
//import com.secure.jnet.wallet.domain.models.enums.WalletStatus
//import javax.inject.Inject
//
//class WalletStatusDataModelMapper @Inject constructor() :
//    DataModelMapper<WalletStatus, WalletStatusDTO> {
//
//    override fun mapToEntity(dataModel: WalletStatusDTO): WalletStatus {
//        return if (dataModel.gwlcs.persoDone) {
//            WalletStatus.HAS_ACCOUNT
//        } else {
//            WalletStatus.NOT_INITIALIZED
//        }
//    }
//}