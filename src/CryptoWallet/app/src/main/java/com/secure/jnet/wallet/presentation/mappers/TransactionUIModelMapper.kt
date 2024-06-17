//package com.secure.jnet.wallet.presentation.mappers
//
//import com.secure.jnet.wallet.domain.models.TransactionEntity
//import com.secure.jnet.wallet.presentation.models.TransactionItem
//import com.secure.jnet.wallet.util.DATE_FORMAT
//import com.secure.jnet.wallet.util.SECOND
//import com.secure.jnet.wallet.util.formatToToken
//import java.text.SimpleDateFormat
//import java.util.Date
//import java.util.Locale
//import javax.inject.Inject
//
//class TransactionUIModelMapper @Inject constructor() :
//    UIModelMapper<TransactionItem, TransactionEntity> {
//
//    override fun mapToUIModel(entityModel: TransactionEntity): TransactionItem {
//        return TransactionItem.Data(
//            cryptoCurrency = entityModel.cryptoCurrency,
//            amountToken = entityModel.amountToken.formatToToken(
//                cryptoCurrency = entityModel.cryptoCurrency,
//                withTicker = false
//            ),
//            hash = entityModel.hash,
//            timestamp = entityModel.timestamp,
//            status = entityModel.status,
//            type = entityModel.type,
//        )
//    }
//}
//
//class TransactionListUIModelMapper @Inject constructor(
//    private val transactionUIModelMapper: TransactionUIModelMapper,
//) : UIModelMapper<List<TransactionItem>, List<TransactionEntity>> {
//
//    override fun mapToUIModel(entityModel: List<TransactionEntity>): List<TransactionItem> {
//        return convertAndGroupByDate(entityModel)
//    }
//
//    private fun convertAndGroupByDate(originalList: List<TransactionEntity>): List<TransactionItem> {
//        val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
//
//        val groupedItems = originalList
//            .sortedByDescending {
//                it.timestamp
//            }
//            .groupBy {
//                dateFormat.format(Date(it.timestamp * SECOND))
//            }
//
//        val resultList = mutableListOf<TransactionItem>()
//
//        groupedItems.forEach { (_, itemList) ->
//            // Add header item
//            resultList.add(TransactionItem.HeaderItem(itemList.first().timestamp))
//
//            // Add data items
//            itemList.forEach { originalDataItem ->
//                resultList.add(
//                    transactionUIModelMapper.mapToUIModel(originalDataItem)
//                )
//            }
//        }
//
//        return resultList
//    }
//}