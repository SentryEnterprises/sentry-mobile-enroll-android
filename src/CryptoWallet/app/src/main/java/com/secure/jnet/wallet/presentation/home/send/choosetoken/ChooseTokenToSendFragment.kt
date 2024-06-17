//package com.secure.jnet.wallet.presentation.home.send.choosetoken
//
//import android.os.Bundle
//import android.view.View
//import androidx.core.view.isVisible
//import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
//import androidx.navigation.fragment.findNavController
//import androidx.navigation.fragment.navArgs
//import androidx.recyclerview.widget.DividerItemDecoration
//import androidx.recyclerview.widget.RecyclerView
//import com.secure.jnet.wallet.R
//import com.secure.jnet.wallet.SendGraphArgs
//import com.secure.jnet.wallet.databinding.FragmentChooseTokenToSendBinding
//import com.secure.jnet.wallet.domain.models.enums.CryptoCurrency
//import com.secure.jnet.wallet.presentation.base.BaseFragment
//import com.secure.jnet.wallet.presentation.common.adapter.TokensAdapter
//import com.secure.jnet.wallet.presentation.home.send.SendTransactionViewModel
//import com.secure.jnet.wallet.presentation.models.TokenItem
//import com.secure.jnet.wallet.presentation.view.SearchView
//import com.secure.jnet.wallet.util.ext.observe
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class ChooseTokenToSendFragment : BaseFragment<FragmentChooseTokenToSendBinding>(
//    R.layout.fragment_choose_token_to_send
//) {
//
//    private val viewModel: SendTransactionViewModel by hiltNavGraphViewModels(R.id.send_graph)
//
//    private val graphArgs by navArgs<SendGraphArgs>()
//
//    private val tokensAdapter by lazy {
//        TokensAdapter {
//            viewModel.onCryptoCurrencySelected(it.cryptoCurrency)
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        graphArgs.cryptoCurrencyName?.let {
//            val cryptoCurrency = CryptoCurrency.valueOf(it)
//            viewModel.onCryptoCurrencySelected(cryptoCurrency)
//            return
//        }
//
//        graphArgs.address?.let {
//            viewModel.onAddressQRScanned(it)
//            return
//        }
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding.apply {
//            ivBack.setOnClickListener { findNavController().popBackStack() }
//
//            rvSendTokens.apply {
//                adapter = tokensAdapter
//                addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))
//            }
//
//            searchView.setSearchTextChangeListener(object : SearchView.OnSearchTextListener {
//                override fun onSearchTextChanged(text: String) {
//                    viewModel.filterTokens(text)
//                }
//            })
//        }
//    }
//
//    override fun onBindLiveData() {
//        observe(viewModel.showProgress) {
//            binding.apply {
//                progress.isVisible = it
//                rvSendTokens.isVisible = !it
//            }
//        }
//
//        observe(viewModel.tokens) {
//            showTokens(it)
//        }
//
//        observe(viewModel.navigateToEnterAmountScreen) {
//            navigateToEnterAmountScreen()
//        }
//    }
//
//    private fun showTokens(tokenItems: List<TokenItem>) {
//        binding.apply {
//            if (tokenItems.isEmpty()) {
//                tvNoSearchResult.isVisible = true
//                rvSendTokens.isVisible = false
//            } else {
//                tvNoSearchResult.isVisible = false
//                rvSendTokens.isVisible = true
//
//                tokensAdapter.submitList(tokenItems)
//            }
//        }
//    }
//
//    private fun navigateToEnterAmountScreen() {
//        findNavController().navigate(
//            ChooseTokenToSendFragmentDirections
//                .actionChooseSendTokenFragmentToSendTransactionFragment()
//        )
//    }
//}