package com.secure.jnet.wallet.data.mappers.remote

import com.secure.jnet.wallet.data.mappers.EntityMapper
import com.secure.jnet.wallet.data.models.remote.GetUtxoAttributesBodyDTO
import com.secure.jnet.wallet.data.models.remote.GetUtxoBodyDTO
import com.secure.jnet.wallet.data.models.remote.GetUtxoDataBodyDTO
import javax.inject.Inject

class GetUtxoEntityMapper @Inject constructor() : EntityMapper<List<String>, GetUtxoBodyDTO> {

    override fun mapFromEntity(entityModel: List<String>): GetUtxoBodyDTO {
        val getUtxoDataBodyDTO: List<GetUtxoDataBodyDTO> = entityModel.map {
            GetUtxoDataBodyDTO(
                attributes = GetUtxoAttributesBodyDTO(
                    address = it
                )
            )
        }

        return GetUtxoBodyDTO(
            data = getUtxoDataBodyDTO
        )
    }
}