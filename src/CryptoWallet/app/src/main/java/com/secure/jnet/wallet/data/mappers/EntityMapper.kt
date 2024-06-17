package com.secure.jnet.wallet.data.mappers

interface EntityMapper<EntityModel, DataModel> {

    fun mapFromEntity(entityModel: EntityModel): DataModel
}