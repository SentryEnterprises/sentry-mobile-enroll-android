package com.secure.jnet.wallet.data.mappers.remote

import com.secure.jnet.wallet.data.mappers.DataModelMapper
import com.secure.jnet.wallet.data.models.remote.NetworkDTO
import com.secure.jnet.wallet.domain.models.remote.NetworkEntity
import javax.inject.Inject

class NetworkDataMapper @Inject constructor() : DataModelMapper<NetworkEntity, NetworkDTO> {

    override fun mapToEntity(dataModel: NetworkDTO): NetworkEntity {
        return NetworkEntity(
            id = dataModel.data.id,
            type = dataModel.data.type,
            balanceType = dataModel.data.attributes.balanceType,
            isPriorityFeeUsed = dataModel.data.attributes.isPriorityFeeUsed,
            name = dataModel.data.attributes.name,
            symbol = dataModel.data.attributes.symbol,
        )
    }
}