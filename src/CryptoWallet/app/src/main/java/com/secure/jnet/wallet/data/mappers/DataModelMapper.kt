package com.secure.jnet.wallet.data.mappers

interface DataModelMapper<EntityModel, DataModel> {

    fun mapToEntity(dataModel: DataModel): EntityModel
}