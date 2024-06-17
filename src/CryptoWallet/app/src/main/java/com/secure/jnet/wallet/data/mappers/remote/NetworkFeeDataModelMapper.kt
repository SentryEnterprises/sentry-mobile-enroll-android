package com.secure.jnet.wallet.data.mappers.remote

import com.secure.jnet.wallet.data.mappers.DataModelMapper
import com.secure.jnet.wallet.data.models.remote.NetworkFeeDTO
import com.secure.jnet.wallet.domain.models.remote.BitcoinFeeEntity
import com.secure.jnet.wallet.domain.models.remote.EthereumFeeEntity
import com.secure.jnet.wallet.domain.models.remote.FeeEntity
import javax.inject.Inject

class NetworkFeeDataModelMapper @Inject constructor() : DataModelMapper<FeeEntity, NetworkFeeDTO> {

    override fun mapToEntity(dataModel: NetworkFeeDTO): FeeEntity {

        return if (dataModel.data.attributes.normal.attributes.bitcoinLikeFee != null) {
            BitcoinFeeEntity(
                dataModel.data.attributes.normal.attributes.bitcoinLikeFee.fee
            )
        } else {
            EthereumFeeEntity(
                dataModel.data.attributes.normal.attributes.ethereumFee!!.maxTotalFee,
                dataModel.data.attributes.normal.attributes.ethereumFee.maxPriorityFee,
            )
        }
    }
}