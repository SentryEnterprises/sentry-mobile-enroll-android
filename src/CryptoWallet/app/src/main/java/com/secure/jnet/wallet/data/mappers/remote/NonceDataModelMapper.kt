package com.secure.jnet.wallet.data.mappers.remote

import com.secure.jnet.wallet.data.mappers.DataModelMapper
import com.secure.jnet.wallet.data.models.remote.NonceDTO
import com.secure.jnet.wallet.domain.models.remote.NonceEntity
import javax.inject.Inject

class NonceDataModelMapper @Inject constructor() : DataModelMapper<NonceEntity, NonceDTO> {

    override fun mapToEntity(dataModel: NonceDTO): NonceEntity {
        return NonceEntity(
            dataModel.data.attributes.nonce
        )
    }
}