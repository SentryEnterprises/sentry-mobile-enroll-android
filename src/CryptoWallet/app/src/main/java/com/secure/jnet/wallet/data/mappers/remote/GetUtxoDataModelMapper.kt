package com.secure.jnet.wallet.data.mappers.remote

import com.secure.jnet.wallet.data.mappers.DataModelMapper
import com.secure.jnet.wallet.data.models.remote.GetUtxoDTO
import com.secure.jnet.wallet.domain.models.remote.UtxoEntity
import javax.inject.Inject

class GetUtxoDataModelMapper @Inject constructor() :
    DataModelMapper<List<UtxoEntity>, GetUtxoDTO> {

    override fun mapToEntity(dataModel: GetUtxoDTO): List<UtxoEntity> {
        val getUtxoList = mutableListOf<UtxoEntity>()

        dataModel.data.forEach { getUtxoDataDTO ->
            getUtxoDataDTO.attributes.utxo.forEach {
                getUtxoList.add(
                    UtxoEntity(
                        address = getUtxoDataDTO.id,
                        amount = it.amount.toLong(),
                        index = it.index,
                        script = it.script,
                        scriptType = it.scriptType,
                        transactionId = it.transactionId,
                    )
                )
            }
        }

        return getUtxoList
    }
}