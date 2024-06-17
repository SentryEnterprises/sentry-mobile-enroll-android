//package com.secure.jnet.wallet.presentation.common.adapter
//
//import android.annotation.SuppressLint
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.core.content.ContextCompat
//import androidx.core.view.isVisible
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.ListAdapter
//import androidx.recyclerview.widget.RecyclerView
//import com.secure.jnet.wallet.R
//import com.secure.jnet.wallet.databinding.ItemTransactionBinding
//import com.secure.jnet.wallet.databinding.ItemTransactionHeaderBinding
//import com.secure.jnet.wallet.domain.models.TransactionStatus
//import com.secure.jnet.wallet.domain.models.TransactionType
//import com.secure.jnet.wallet.domain.models.enums.getIcon
//import com.secure.jnet.wallet.presentation.models.TransactionItem
//import com.secure.jnet.wallet.util.ext.isToday
//import com.secure.jnet.wallet.util.ext.isYesterday
//import com.secure.jnet.wallet.util.formatDate
//import com.secure.jnet.wallet.util.formatTime
//import java.util.Date
//
//class TransactionsAdapter(
//    private val onItemClick: (transactionData: TransactionItem.Data) -> Unit,
//) : ListAdapter<TransactionItem, RecyclerView.ViewHolder>(DiffUtilCallback()) {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        return when (viewType) {
//            TYPE_HEADER -> HeaderVH(
//                ItemTransactionHeaderBinding.inflate(
//                    LayoutInflater.from(parent.context), parent, false
//                ),
//            )
//
//            TYPE_DATA -> TransactionDataVH(
//                ItemTransactionBinding.inflate(
//                    LayoutInflater.from(parent.context), parent, false
//                ),
//            )
//
//            else -> throw IllegalStateException("Unsupported view type.")
//        }
//    }
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        when (val item = getItem(position)) {
//            is TransactionItem.HeaderItem ->
//                (holder as HeaderVH).bind(item)
//
//            is TransactionItem.Data ->
//                (holder as TransactionDataVH).bind(item)
//        }
//    }
//
//    override fun getItemViewType(position: Int): Int {
//        return when (getItem(position)) {
//            is TransactionItem.HeaderItem -> TYPE_HEADER
//            is TransactionItem.Data -> TYPE_DATA
//        }
//    }
//
//    inner class HeaderVH(
//        private val binding: ItemTransactionHeaderBinding,
//    ) : RecyclerView.ViewHolder(binding.root) {
//
//        fun bind(transaction: TransactionItem.HeaderItem) {
//            val date = Date(transaction.timestamp * SECOND)
//
//            binding.apply {
//                tvTransactionDate.text = if (date.isToday()) {
//                    tvTransactionDate.context.getString(R.string.today)
//                } else if (date.isYesterday()) {
//                    tvTransactionDate.context.getString(R.string.yesterday)
//                } else {
//                    date.formatDate()
//                }
//            }
//        }
//    }
//
//    inner class TransactionDataVH(
//        private val binding: ItemTransactionBinding,
//    ) : RecyclerView.ViewHolder(binding.root) {
//
//        init {
//            binding.root.setOnClickListener {
//                onItemClick(getItem(adapterPosition) as TransactionItem.Data)
//            }
//        }
//
//        @SuppressLint("SetTextI18n")
//        fun bind(transaction: TransactionItem.Data) {
//
//            binding.apply {
//                ivTokenIcon.setImageDrawable(
//                    ContextCompat.getDrawable(
//                        ivTokenIcon.context,
//                        transaction.cryptoCurrency.getIcon()
//                    )
//                )
//
//                tvTokenName.text = transaction.cryptoCurrency.name
//
//                val date = Date(transaction.timestamp * SECOND)
//                tvTransactionTime.text = date.formatTime().lowercase()
//
//                val amount = transaction.amountToken
//
//                when (transaction.type) {
//                    TransactionType.Incoming -> {
//                        tvAmount.text = "+$amount"
//                    }
//
//                    TransactionType.Outgoing -> {
//                        tvAmount.text = "-$amount"
//                    }
//                }
//
//                when (transaction.status) {
//                    TransactionStatus.Completed -> {
//                        when (transaction.type) {
//                            TransactionType.Incoming -> {
//                                tvAmount.setTextColor(
//                                    ContextCompat.getColor(tvAmount.context, R.color.green)
//                                )
//                            }
//
//                            TransactionType.Outgoing -> {
//                                tvAmount.setTextColor(
//                                    ContextCompat.getColor(tvAmount.context, R.color.color_on_surface_high)
//                                )
//                            }
//                        }
//
//                        tvStatus.isVisible = false
//                    }
//
//                    TransactionStatus.Pending -> {
//                        tvAmount.setTextColor(
//                            ContextCompat.getColor(tvAmount.context, R.color.color_on_surface_high)
//                        )
//                        tvStatus.apply {
//                            text = transaction.status.name
//                            setTextColor(
//                                ContextCompat.getColor(tvAmount.context, R.color.color_on_surface_high)
//                            )
//                            isVisible = true
//                        }
//                    }
//
//                    TransactionStatus.Failed -> {
//                        tvAmount.setTextColor(
//                            ContextCompat.getColor(tvAmount.context, R.color.red)
//                        )
//                        tvStatus.apply {
//                            text = transaction.status.name
//                            setTextColor(
//                                ContextCompat.getColor(tvAmount.context, R.color.red)
//                            )
//                            isVisible = true
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    private class DiffUtilCallback : DiffUtil.ItemCallback<TransactionItem>() {
//        override fun areItemsTheSame(
//            oldItem: TransactionItem,
//            newItem: TransactionItem,
//        ): Boolean =
//            oldItem == newItem
//
//        override fun areContentsTheSame(
//            oldItem: TransactionItem,
//            newItem: TransactionItem,
//        ): Boolean =
//            oldItem == newItem
//    }
//
//    companion object {
//        const val TYPE_HEADER = 0
//        const val TYPE_DATA = 1
//
//        private const val SECOND = 1000L
//    }
//}