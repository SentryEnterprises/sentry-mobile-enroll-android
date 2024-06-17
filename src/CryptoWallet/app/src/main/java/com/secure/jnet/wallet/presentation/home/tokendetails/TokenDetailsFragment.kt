//package com.secure.jnet.wallet.presentation.home.tokendetails
//
//import android.os.Bundle
//import android.view.View
//import androidx.core.content.ContextCompat
//import androidx.core.view.isVisible
//import androidx.fragment.app.viewModels
//import androidx.navigation.fragment.findNavController
//import androidx.navigation.fragment.navArgs
//import com.secure.jnet.wallet.R
//import com.secure.jnet.wallet.databinding.FragmentTokenDetailsBinding
//import com.secure.jnet.wallet.domain.models.enums.CryptoCurrency
//import com.secure.jnet.wallet.domain.models.enums.getIcon
//import com.secure.jnet.wallet.presentation.base.BaseFragment
//import com.secure.jnet.wallet.presentation.common.adapter.TransactionItemDecoration
//import com.secure.jnet.wallet.presentation.common.adapter.TransactionsAdapter
//import com.secure.jnet.wallet.presentation.models.Balance
//import com.secure.jnet.wallet.util.BlockchainUtils
//import com.secure.jnet.wallet.util.ext.observe
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class TokenDetailsFragment : BaseFragment<FragmentTokenDetailsBinding>(
//    R.layout.fragment_token_details
//) {
//
//    private val viewModel: TokenDetailsViewModel by viewModels()
//
//    private val args by navArgs<TokenDetailsFragmentArgs>()
//    private val cryptoCurrency by lazy { args.cryptoCurrency }
//
//    private val transactionsAdapter by lazy {
//        TransactionsAdapter {
//            BlockchainUtils.openBlockchainExplorer(it.cryptoCurrency, requireContext(), it.hash)
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        viewModel.init(cryptoCurrency)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding.apply {
//            tvTitle.text = cryptoCurrency.name
//
//            ivTokenIcon.setImageDrawable(
//                ContextCompat.getDrawable(
//                    requireContext(),
//                    cryptoCurrency.getIcon()
//                )
//            )
//
//            ivBack.setOnClickListener { findNavController().popBackStack() }
//
//            btnReceive.setOnClickListener { navigateToReceiveScreen(cryptoCurrency) }
//
//            btnSend.setOnClickListener { navigateToSendScreen(cryptoCurrency) }
//
//            rvMyTransactions.apply {
//                adapter = transactionsAdapter
//                addItemDecoration(
//                    TransactionItemDecoration(
//                        ContextCompat.getDrawable(
//                            requireContext(),
//                            R.drawable.divider
//                        )!!,
//                        TransactionsAdapter.TYPE_DATA
//                    )
//                )
//            }
//        }
//    }
//
//    override fun onBindLiveData() {
//        observe(viewModel.balance) {
//            showPrimaryBalance(it)
//        }
//
//        observe(viewModel.showTransactionsProgress) {
//            binding.apply {
//                progressTransactions.isVisible = it
//                rvMyTransactions.isVisible = !it
//            }
//        }
//
//        observe(viewModel.transactions) {
//            transactionsAdapter.submitList(it)
//
//            binding.apply {
//                rvMyTransactions.isVisible = it.isNotEmpty()
//                ivNoActivity.isVisible = it.isEmpty()
//                tvNoActivity.isVisible = it.isEmpty()
//            }
//        }
//    }
//
//    private fun showPrimaryBalance(balance: Balance) {
//        binding.apply {
//            tvBalance.text = balance.amountToken
//            tvBalanceInFiat.text = balance.amountFiat
//        }
//    }
//
//    private fun navigateToReceiveScreen(cryptoCurrency: CryptoCurrency) {
//        findNavController().navigate(
//            TokenDetailsFragmentDirections.actionTokenDetailsFragmentToReceiveDetailsFragment(
//                cryptoCurrency
//            )
//        )
//    }
//
//    private fun navigateToSendScreen(cryptoCurrency: CryptoCurrency) {
//        findNavController().navigate(
//            TokenDetailsFragmentDirections.actionTokenDetailsFragmentToSendGraph(
//                address = null,
//                cryptoCurrencyName = cryptoCurrency.name,
//            )
//        )
//    }
//}