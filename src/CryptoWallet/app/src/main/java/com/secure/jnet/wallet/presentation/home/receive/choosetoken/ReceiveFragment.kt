//package com.secure.jnet.wallet.presentation.home.receive.choosetoken
//
//import android.os.Bundle
//import android.view.View
//import androidx.core.content.ContextCompat
//import androidx.core.view.isVisible
//import androidx.fragment.app.viewModels
//import androidx.navigation.fragment.findNavController
//import androidx.recyclerview.widget.DividerItemDecoration
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.secure.jnet.wallet.R
//import com.secure.jnet.wallet.databinding.FragmentReceiveBinding
//import com.secure.jnet.wallet.domain.models.enums.CryptoCurrency
//import com.secure.jnet.wallet.presentation.base.BaseFragment
//import com.secure.jnet.wallet.presentation.home.receive.choosetoken.adapter.ReceiveTokensAdapter
//import com.secure.jnet.wallet.presentation.view.SearchView
//import com.secure.jnet.wallet.util.ext.observe
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class ReceiveFragment : BaseFragment<FragmentReceiveBinding>(R.layout.fragment_receive) {
//
//    private val viewModel: ReceiveViewModel by viewModels()
//
//    private val tokensAdapter by lazy {
//        ReceiveTokensAdapter {
//            viewModel.onTokenClick(it)
//        }
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding.apply {
//            ivBack.setOnClickListener { findNavController().popBackStack() }
//
//            rvReceiveTokens.apply {
//                layoutManager = LinearLayoutManager(requireContext())
//                adapter = this@ReceiveFragment.tokensAdapter
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
//        observe(viewModel.tokens) {
//            if (it.isEmpty()) {
//                binding.tvNoSearchResult.isVisible = true
//                binding.rvReceiveTokens.isVisible = false
//            } else {
//                binding.tvNoSearchResult.isVisible = false
//                binding.rvReceiveTokens.isVisible = true
//
//                tokensAdapter.submitList(it)
//            }
//        }
//
//        observe(viewModel.navigateToReceiveDetailsScreen) {
//            navigateToReceiveDetailsScreen(it)
//        }
//    }
//
//    private fun navigateToReceiveDetailsScreen(cryptoCurrency: CryptoCurrency) {
//        findNavController().navigate(
//            ReceiveFragmentDirections.actionReceiveFragmentToReceiveDetailsFragment(cryptoCurrency)
//        )
//    }
//}