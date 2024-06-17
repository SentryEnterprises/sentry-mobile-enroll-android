//package com.secure.jnet.wallet.presentation.common.adapter
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.core.content.ContextCompat
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.ListAdapter
//import androidx.recyclerview.widget.RecyclerView
//import com.secure.jnet.wallet.databinding.ItemTokenBinding
//import com.secure.jnet.wallet.databinding.ItemTokenFooterBinding
//import com.secure.jnet.wallet.domain.models.enums.getIcon
//import com.secure.jnet.wallet.presentation.models.TokenItem
//
//class TokensAdapter(
//    private val onTokenClick: (tokenData: TokenItem.TokenData) -> Unit,
//) : ListAdapter<TokenItem, RecyclerView.ViewHolder>(DiffUtilCallback()) {
//
//    private var onAddTokensClick: (() -> Unit)? = null
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        return when (viewType) {
//            TYPE_DATA -> TokenDataVH(
//                ItemTokenBinding.inflate(
//                    LayoutInflater.from(parent.context), parent, false
//                )
//            )
//
//            TYPE_FOOTER -> TokenFooterVH(
//                ItemTokenFooterBinding.inflate(
//                    LayoutInflater.from(parent.context), parent, false
//                )
//            )
//
//            else -> throw IllegalStateException("Unsupported view type.")
//        }
//    }
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        when (val item = getItem(position)) {
//            is TokenItem.TokenData -> (holder as TokenDataVH).bind(item)
//            is TokenItem.TokenFooter -> (holder as TokenFooterVH)
//        }
//    }
//
//    override fun getItemViewType(position: Int): Int {
//        return when (getItem(position)) {
//            is TokenItem.TokenData -> TYPE_DATA
//            is TokenItem.TokenFooter -> TYPE_FOOTER
//        }
//    }
//
//    fun setOnAddTokensClick(onAddTokensClick: () -> Unit) {
//        this.onAddTokensClick?.invoke()
//    }
//
//    inner class TokenDataVH(
//        private val binding: ItemTokenBinding,
//    ) : RecyclerView.ViewHolder(binding.root) {
//
//        init {
//            binding.root.setOnClickListener {
//                onTokenClick(getItem(adapterPosition) as TokenItem.TokenData)
//            }
//        }
//
//        fun bind(tokenData: TokenItem.TokenData) {
//            binding.apply {
//                tvTokenName.text = tokenData.cryptoCurrency.name
//
//                ivTokenIcon.setImageDrawable(
//                    ContextCompat.getDrawable(
//                        ivTokenIcon.context,
//                        tokenData.cryptoCurrency.getIcon()
//                    )
//                )
//
//                tvAmount.text = tokenData.balance.amountToken
//                tvAmountInFiat.text = tokenData.balance.amountFiat
//            }
//        }
//    }
//
//    inner class TokenFooterVH(
//        binding: ItemTokenFooterBinding,
//    ) : RecyclerView.ViewHolder(binding.root) {
//
//        init {
//            binding.root.setOnClickListener {
//                onAddTokensClick?.invoke()
//            }
//        }
//    }
//
//    private class DiffUtilCallback : DiffUtil.ItemCallback<TokenItem>() {
//        override fun areItemsTheSame(oldItem: TokenItem, newItem: TokenItem): Boolean =
//            oldItem == newItem
//
//        override fun areContentsTheSame(
//            oldItem: TokenItem,
//            newItem: TokenItem
//        ): Boolean =
//            oldItem == newItem
//    }
//
//    private companion object {
//        private const val TYPE_DATA = 0
//        private const val TYPE_FOOTER = 1
//    }
//}