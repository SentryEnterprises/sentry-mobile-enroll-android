package com.secure.jnet.wallet.data.mappers.remote

import com.secure.jnet.wallet.data.mappers.DataModelMapper
import com.secure.jnet.wallet.data.models.remote.BalanceDTO
import com.secure.jnet.wallet.domain.models.enums.parseCryptoCurrency
import com.secure.jnet.wallet.domain.models.remote.BalanceEntity
import java.math.BigInteger
import javax.inject.Inject

class BalanceDataModelMapper @Inject constructor() :
    DataModelMapper<List<BalanceEntity>, BalanceDTO> {

    override fun mapToEntity(dataModel: BalanceDTO): List<BalanceEntity> {
        val balanceList = mutableListOf<BalanceEntity>()

        dataModel.data.forEach {
            val cryptoCurrency = it.attributes.symbol.parseCryptoCurrency()

            val price: String = dataModel.included.firstOrNull { tokenPriceDTO ->
                tokenPriceDTO.attributes.network.lowercase() == cryptoCurrency.name.lowercase()
            }?.attributes?.value ?: ""

            balanceList.add(
                BalanceEntity(
                    cryptoCurrency = cryptoCurrency,
                    amountToken = BigInteger(it.attributes.confirmedBalance),
                    rate = BigInteger(price)
                )
            )
        }

        return balanceList
    }
}